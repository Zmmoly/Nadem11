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

class DeepgramService(private val context: Context) {

    // ─── URLs والمفاتيح ────────────────────────────────
    private val MODAL_API_URL: String = BuildConfig.TRANSCRIBE_API_URL
    private val DEEPGRAM_API_KEY: String = BuildConfig.DEEPGRAM_API_KEY
    private val DEEPGRAM_URL =
        "https://api.deepgram.com/v1/listen?language=ar&model=nova-2&punctuate=true"

    // ─── حالة GPU ─────────────────────────────────────
    // true = Modal GPU جاهز، false = استخدم Deepgram
    @Volatile private var isGpuReady = false

    // ─── إعدادات الصوت ────────────────────────────────
    private val SILENCE_THRESHOLD = 1500
    private val SILENCE_DURATION_MS = 800L   // رُفع من 500 لتقليل الطلبات
    private val MIN_AUDIO_BYTES = 16000      // تجاهل الأصوات القصيرة جداً (~0.5 ثانية)

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
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // ─── Callbacks ────────────────────────────────────
    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onInterimTranscription: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null

    // ─── بدء التسجيل ──────────────────────────────────
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
        onConnectionEstablished?.invoke()

        // ✅ أيقظ GPU في الخلفية فور بدء التسجيل
        warmupGpu()

        startAudioCapture()
    }

    // ─── إيقاظ GPU بشكل غير متزامن ───────────────────
    private fun warmupGpu() {
        if (MODAL_API_URL.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("$MODAL_API_URL/health")  // endpoint بسيط للإيقاظ
                    .get()
                    .build()
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    isGpuReady = true
                }
            } catch (_: Exception) {
                // GPU لم يستجب — نبقى على Deepgram
                isGpuReady = false
            }
        }
    }

    // ─── إيقاف التسجيل ────────────────────────────────
    fun stopRecitation() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        audioBuffer.reset()
        isGpuReady = false  // ريست عند الإيقاف
    }

    // ─── التقاط الصوت ─────────────────────────────────
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

                            // ✅ تجاهل الأصوات القصيرة جداً (ضوضاء)
                            if (audioData.size < MIN_AUDIO_BYTES) continue

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

    // ─── توجيه الصوت: Deepgram أم Modal؟ ─────────────
    private fun sendAudio(pcmData: ByteArray) {
        val wavBytes = pcmToWav(pcmData, sampleRate)

        if (isGpuReady && MODAL_API_URL.isNotEmpty()) {
            // ✅ GPU جاهز → Modal (النموذج الخاص)
            sendToModal(wavBytes)
        } else {
            // ⏳ GPU نايم → Deepgram فوراً
            sendToDeepgram(wavBytes)
        }
    }

    // ─── إرسال لـ Deepgram ────────────────────────────
    // Deepgram يستقبل raw bytes مباشرة (ليس multipart)
    private fun sendToDeepgram(wavBytes: ByteArray) {
        try {
            if (DEEPGRAM_API_KEY.isEmpty()) {
                onError?.invoke("مفتاح Deepgram غير موجود")
                return
            }

            val requestBody = wavBytes.toRequestBody("audio/wav".toMediaType())

            val request = Request.Builder()
                .url(DEEPGRAM_URL)
                .addHeader("Authorization", "Token $DEEPGRAM_API_KEY")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                // استخراج النص من رد Deepgram المتداخل
                val text = JSONObject(body)
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
                    onError?.invoke("Deepgram خطأ: ${response.code}")
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                onError?.invoke("Deepgram خطأ: ${e.message}")
            }
        }
    }

    // ─── إرسال لـ Modal (النموذج الخاص) ──────────────
    // Modal يستقبل multipart/form-data
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
            } else {
                // ✅ Modal فشل → ارجع لـ Deepgram تلقائياً
                isGpuReady = false
                sendToDeepgram(wavBytes)
            }
        } catch (e: Exception) {
            // ✅ Modal فشل → ارجع لـ Deepgram تلقائياً
            isGpuReady = false
            CoroutineScope(Dispatchers.IO).launch {
                sendToDeepgram(wavBytes)
            }
        }
    }

    // ─── PCM → WAV ────────────────────────────────────
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
