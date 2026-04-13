package awab.quran.ar.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import awab.quran.ar.BuildConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DeepgramService(private val context: Context) {

    private val MODAL_TRANSCRIBE_URL = "https://nadem--quran-transcription-transcribe-endpoint.modal.run"
    private val MODAL_HEALTH_URL     = "https://nadem--quran-transcription-health.modal.run"
    private val MODAL_WARMUP_URL     = "https://nadem--quran-transcription-warmup-endpoint.modal.run"

    private val DEEPGRAM_API_KEY: String = BuildConfig.DEEPGRAM_API_KEY
    private val DEEPGRAM_URL = "https://api.deepgram.com/v1/listen?model=nova-3&language=ar"

    private val SILENCE_THRESHOLD = 1500
    private val SILENCE_DURATION_MS = 500L
    private val GPU_CHECK_TIMEOUT_MS = 2000L

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

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val fastHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    private val warmupHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    // Firebase Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    // منطق العينة — 1 كل 50
    private var clipCounter = 0
    private val SAMPLE_EVERY = 50
    private var targetClip = (1..50).random()

    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onInterimTranscription: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null
    var onModelChanged: ((String) -> Unit)? = null

    // ─────────────────────────────────────────
    // عينة عشوائية: 1 من كل 50 مقطع
    // ─────────────────────────────────────────
    private fun shouldSample(): Boolean {
        clipCounter++
        if (clipCounter == targetClip) {
            clipCounter = 0
            targetClip = (1..SAMPLE_EVERY).random()
            return true
        }
        if (clipCounter >= SAMPLE_EVERY) {
            clipCounter = 0
            targetClip = (1..SAMPLE_EVERY).random()
        }
        return false
    }

    // ─────────────────────────────────────────
    // رفع الصوت والنص إلى Firebase بشكل مجهول
    // ─────────────────────────────────────────
    private fun uploadSampleToFirebase(wavBytes: ByteArray, transcript: String) {
        if (!shouldSample()) return

        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss_SSS", Locale.getDefault()
        ).format(Date())

        // رفع الصوت
        storageRef.child("samples/audio/$timestamp.wav")
            .putBytes(wavBytes)
            .addOnFailureListener { /* تجاهل في الخلفية */ }

        // رفع النص
        storageRef.child("samples/text/$timestamp.txt")
            .putBytes(transcript.toByteArray(Charsets.UTF_8))
            .addOnFailureListener { /* تجاهل في الخلفية */ }
    }

    // ─────────────────────────────────────────
    // بدء التسجيل
    // ─────────────────────────────────────────
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

        CoroutineScope(Dispatchers.IO).launch { wakeUpModal() }
        CoroutineScope(Dispatchers.IO).launch { checkGpuStatus() }

        onConnectionEstablished?.invoke()
        startAudioCapture()
    }

    // ─────────────────────────────────────────
    // إيقاف التسجيل
    // ─────────────────────────────────────────
    fun stopRecitation() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        audioBuffer.reset()
    }

    // ─────────────────────────────────────────
    // التحقق من حالة GPU
    // ─────────────────────────────────────────
    private fun checkGpuStatus() {
        if (isCheckingGpu) return
        isCheckingGpu = true
        try {
            val request = Request.Builder().url(MODAL_HEALTH_URL).get().build()
            val startTime = System.currentTimeMillis()
            val response = fastHttpClient.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - startTime

            isGpuAwake = response.isSuccessful && elapsed < GPU_CHECK_TIMEOUT_MS
            response.close()

            CoroutineScope(Dispatchers.Main).launch {
                onModelChanged?.invoke(if (isGpuAwake) "modal" else "deepgram")
            }
        } catch (e: Exception) {
            isGpuAwake = false
            CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("deepgram") }
        } finally {
            isCheckingGpu = false
        }
    }

    // ─────────────────────────────────────────
    // التقاط الصوت وكشف الصمت
    // ─────────────────────────────────────────
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
                        byteBuffer[i * 2]     = (buffer[i].toInt() and 0xFF).toByte()
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

    // ─────────────────────────────────────────
    // اختيار النموذج وإرسال الصوت
    // ─────────────────────────────────────────
    private fun sendAudio(pcmData: ByteArray) {
        if (pcmData.isEmpty()) return
        val wavBytes = pcmToWav(pcmData, sampleRate)

        if (isGpuAwake) {
            CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("modal") }
            sendToModal(wavBytes)
        } else {
            CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("deepgram") }
            sendToDeepgram(wavBytes)
        }
    }

    // ─────────────────────────────────────────
    // الإرسال إلى Modal
    // ─────────────────────────────────────────
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
                .url(MODAL_TRANSCRIBE_URL)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                parseAndReturn(body, wavBytes)
            } else {
                isGpuAwake = false
                CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("deepgram") }
                onError?.invoke("تعذر الاتصال بالسيرفر، جاري التحويل...")
            }
        } catch (e: Exception) {
            isGpuAwake = false
            CoroutineScope(Dispatchers.Main).launch { onError?.invoke("خطأ: ${e.message}") }
        }
    }

    // ─────────────────────────────────────────
    // الإرسال إلى Deepgram
    // ─────────────────────────────────────────
    private fun sendToDeepgram(wavBytes: ByteArray) {
        try {
            val request = Request.Builder()
                .url(DEEPGRAM_URL)
                .addHeader("Authorization", "Token $DEEPGRAM_API_KEY")
                .post(wavBytes.toRequestBody("audio/wav".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                val json = JSONObject(body)
                val rawText = json
                    .getJSONObject("results")
                    .getJSONArray("channels")
                    .getJSONObject(0)
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
                    .getString("transcript")

                val text = cleanText(rawText)
                if (text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        uploadSampleToFirebase(wavBytes, text)
                    }
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
            CoroutineScope(Dispatchers.Main).launch { onError?.invoke("خطأ: ${e.message}") }
        }
    }

    // ─────────────────────────────────────────
    // تحليل رد Modal وإرجاع النص
    // ─────────────────────────────────────────
    private fun parseAndReturn(body: String, wavBytes: ByteArray) {
        try {
            val rawText = JSONObject(body).getString("text")
            val text = cleanText(rawText)
            if (text.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    uploadSampleToFirebase(wavBytes, text)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    onTranscriptionReceived?.invoke(text)
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch { onError?.invoke("خطأ في تحليل الرد") }
        }
    }

    // ─────────────────────────────────────────
    // إيقاظ Modal
    // ─────────────────────────────────────────
    private fun wakeUpModal() {
        try {
            val request = Request.Builder().url(MODAL_WARMUP_URL).get().build()
            val response = warmupHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                isGpuAwake = true
                CoroutineScope(Dispatchers.Main).launch { onModelChanged?.invoke("modal") }
            }
            response.close()
        } catch (e: Exception) {
            isGpuAwake = false
        }
    }

    // ─────────────────────────────────────────
    // تنظيف النص
    // ─────────────────────────────────────────
    private fun cleanText(raw: String): String {
        return raw
            .replace(Regex("\\[[^\\]]*\\]"), "")
            .replace(Regex("\\([^)]*\\)"), "")
            .replace(Regex("[\\u0022\\u0027\\u201C\\u201D\\u2018\\u2019]"), "")
            .replace(Regex("[\\{\\}]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // ─────────────────────────────────────────
    // تحويل PCM إلى WAV
    // ─────────────────────────────────────────
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
