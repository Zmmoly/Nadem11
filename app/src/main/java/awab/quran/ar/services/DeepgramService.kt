package awab.quran.ar.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import awab.quran.ar.BuildConfig  // ✅ استيراد BuildConfig
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class DeepgramService(private val context: Context) {

    // ✅ الحل: URL يأتي من BuildConfig وليس hardcoded
    private val API_URL: String = BuildConfig.TRANSCRIBE_API_URL

    private val SILENCE_THRESHOLD = 1500
    private val SILENCE_DURATION_MS = 500L

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

    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onInterimTranscription: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null

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
                                    sendAudioToAPI(audioData)
                                    isProcessing = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendAudioToAPI(pcmData: ByteArray) {
        try {
            if (pcmData.isEmpty()) return

            val wavBytes = pcmToWav(pcmData, sampleRate)

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", "audio.wav",
                    wavBytes.toRequestBody("audio/wav".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url(API_URL)
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
                onError?.invoke("خطأ: ${response.code}")
            }

        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                onError?.invoke("خطأ: ${e.message}")
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
