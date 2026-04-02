package awab.quran.ar.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import awab.quran.ar.BuildConfig
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.concurrent.TimeUnit

class DeepgramService(private val context: Context) {

    private val MODAL_API_URL: String = BuildConfig.TRANSCRIBE_API_URL
    private val DEEPGRAM_API_KEY: String = BuildConfig.DEEPGRAM_API_KEY
    private val DEEPGRAM_URL = "https://api.deepgram.com/v1/listen?model=nova-3&language=ar"

    private val SILENCE_THRESHOLD = 1500
    private val SILENCE_DURATION_MS = 500L
    private val GPU_CHECK_TIMEOUT_MS = 2000L // ثانيتان للتحقق من الـ GPU

    // حالة الـ GPU
    private var isGpuAwake = false
    private var isCheckingGpu = false

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var isProcessing = false
    private var recordingJob: Job? = null
    private val audioBuffer = ByteArrayOutputStream()

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    }

    // عميل HTTP عادي
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // عميل HTTP سريع للتحقق من الـ GPU (ثانيتان فقط)
    private val fastHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onInterimTranscription: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null
    var onModelChanged: ((String) -> Unit)? = null // "modal" أو "deepgram"

    fun startRecitation() {
        if (isRecording) return
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError?.invoke("صلاحية الميكروفون غير ممنوحة")
            return
        }
        audioBuffer.reset()
        isGpuAwake = false

        // تحقق من الـ GPU عند البدء
        CoroutineScope(Dispatchers.IO).launch {
            checkGpuStatus()
        }

        onConnectionEstablished?.invoke()
        startAudioCapture()
    }

    fun stopRecitation() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        audioBuffer.reset()
    }

    // التحقق من حالة الـ GPU في Modal
    private fun checkGpuStatus() {
        if (isCheckingGpu) return
        isCheckingGpu = true
        try {
            val request = Request.Builder()
                .url(MODAL_API_URL)
                .head() // طلب HEAD خفيف فقط للتحقق
                .build()

            val startTime = System.currentTimeMillis()
            val response = fastHttpClient.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - startTime

            // إذا رجع في أقل من ثانيتين الـ GPU مفتوح
            isGpuAwake = response.isSuccessful && elapsed < GPU_CHECK_TIMEOUT_MS
            response.close()
            // أعلم الواجهة بالنموذج المستخدم فور معرفة الحالة
            CoroutineScope(Dispatchers.Main).launch {
                onModelChanged?.invoke(if (isGpuAwake) "modal" else "deepgram")
            }
        } catch (e: Exception) {
            // تأخر أو فشل = الـ GPU نائم
            isGpuAwake = false
            CoroutineScope(Dispatchers.Main).launch {
                onModelChanged?.invoke("deepgram")
            }
        } finally {
            isCheckingGpu = false
        }
    }

    private fun startAudioCapture() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate, channelConfig, audioFormat,
            bufferSize * 2
        )
        audioRecord?.startRecording()
        isRecording = true

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ShortArray(bufferSize)
            var silenceStart = 0L
            var isSilent = false
            var hasAudio = false

            while (isActive && isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                if (read > 0) {
                    val amplitude = buffer.take(read).map {
                        Math.abs(it.toInt())
                    }.average()

                    val byteBuffer = ByteArray(read * 2)
                    for (i in 0 until read) {
                        byteBuffer[i * 2] = (buffer[i].toInt() and 0xFF).toByte()
                        byteBuffer[i * 2 + 1] = (buffer[i].toInt() shr 8).toByte()
                    }
                    audioBuffer.write(byteBuffer)

                    if (amplitude > SILENCE_THRESHOLD) {
                        isSilent = false
                        hasAudio = true
                        silenceStart = 0L
                    } else {
                        if (!isSilent) {
                            silenceStart = System.currentTimeMillis()
                            isSilent = true
                        }

                        val silenceDuration = System.currentTimeMillis() - silenceStart
                        if (isSilent &&
                            silenceStart > 0 &&
                            silenceDuration >= SILENCE_DURATION_MS &&
                            hasAudio
                        ) {
                            val audioData = audioBuffer.toByteArray()
                            audioBuffer.reset()
                            hasAudio = false
                            isSilent = false
                            silenceStart = 0L

                            if (!isProcessing) {
                                isProcessing = true
                                launch {
                                    sendAudio(audioData)
                                    isProcessing = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // يقرر هل يرسل لـ Deepgram أو Modal بناءً على حالة الـ GPU
    private fun sendAudio(pcmData: ByteArray) {
        if (pcmData.isEmpty()) return
        val wavBytes = pcmToWav(pcmData, sampleRate)

        if (isGpuAwake) {
            // الـ GPU مفتوح — أرسل لـ Modal مباشرة
            CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("modal") }
            sendToModal(wavBytes)
        } else {
            // الـ GPU نائم — استخدم Deepgram الآن وأيقظ Modal في الخلفية
            CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("deepgram") }
            sendToDeepgram(wavBytes)
            // أيقظ الـ GPU في الخلفية
            CoroutineScope(Dispatchers.IO).launch {
                wakeUpModal()
            }
        }
    }

    // إرسال الصوت لـ Modal API
    private fun sendToModal(wavBytes: ByteArray) {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", "audio.wav",
                    wavBytes.toRequestBody("audio/wav".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url(MODAL_API_URL)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                parseAndReturn(body)
            } else {
                // فشل Modal — ارجع لـ Deepgram
                isGpuAwake = false
                CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("deepgram") }
                onError?.invoke("تعذر الاتصال بالسيرفر، جاري التحويل...")
            }
        } catch (e: Exception) {
            isGpuAwake = false
            CoroutineScope(Dispatchers.Main).launch {
                onError?.invoke("خطأ: ${e.message}")
            }
        }
    }

    // إرسال الصوت لـ Deepgram
    private fun sendToDeepgram(wavBytes: ByteArray) {
        try {
            val requestBody = wavBytes.toRequestBody("audio/wav".toMediaType())

            val request = Request.Builder()
                .url(DEEPGRAM_URL)
                .addHeader("Authorization", "Token $DEEPGRAM_API_KEY")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                // استخراج النص من رد Deepgram
                val json = JSONObject(body)
                val text = json
                    .getJSONObject("results")
                    .getJSONArray("channels")
                    .getJSONObject(0)
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
                    .getString("transcript")
                    .trim()

                if (text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onTranscriptionReceived?.invoke(text)
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    onError?.invoke("خطأ Deepgram: ${response.code}")
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                onError?.invoke("خطأ: ${e.message}")
            }
        }
    }

    // إيقاظ الـ GPU في Modal عن طريق إرسال طلب بسيط
    private fun wakeUpModal() {
        try {
            val request = Request.Builder()
                .url(MODAL_API_URL)
                .head()
                .build()
            val response = httpClient.newCall(request).execute()
            // إذا رجع بنجاح الـ GPU استيقظ
            if (response.isSuccessful) {
                isGpuAwake = true
            }
            response.close()
        } catch (e: Exception) {
            isGpuAwake = false
        }
    }

    // تحليل رد Modal وإرجاع النص
    private fun parseAndReturn(body: String) {
        try {
            val rawText = JSONObject(body).getString("text")
            val text = rawText
                .replace(Regex("\\[[^\\]]*\\]"), "")
                .replace(Regex("^['\"]|['\"]$"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (text.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    onTranscriptionReceived?.invoke(text)
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                onError?.invoke("خطأ في تحليل الرد")
            }
        }
    }

    private fun pcmToWav(pcm: ByteArray, sampleRate: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val dataSize = pcm.size
        DataOutputStream(out).apply {
            writeBytes("RIFF")
            writeInt(Integer.reverseBytes(dataSize + 36))
            writeBytes("WAVE")
            writeBytes("fmt ")
            writeInt(Integer.reverseBytes(16))
            writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt())
            writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt())
            writeInt(Integer.reverseBytes(sampleRate))
            writeInt(Integer.reverseBytes(sampleRate * 2))
            writeShort(java.lang.Short.reverseBytes(2.toShort()).toInt())
            writeShort(java.lang.Short.reverseBytes(16.toShort()).toInt())
            writeBytes("data")
            writeInt(Integer.reverseBytes(dataSize))
            write(pcm)
        }
        return out.toByteArray()
    }

    fun isRecording(): Boolean = isRecording
}
