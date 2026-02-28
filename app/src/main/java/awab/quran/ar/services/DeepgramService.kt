package awab.quran.ar.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class DeepgramService(private val context: Context) {

    private val API_URL = "https://Scanor-Ndem.hf.space/transcribe"

    private val SILENCE_THRESHOLD = 1500
    private val SILENCE_DURATION_MS = 800L

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val audioBuffer = ByteArrayOutputStream()

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate, channelConfig, audioFormat
    )

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

                            launch {
                                sendAudioToAPI(audioData)
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

            val client = OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build()

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

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            android.util.Log.d("API_RESPONSE", "الرد: $body")

            if (response.isSuccessful && body != null) {
                android.util.Log.d("API_RESPONSE", "ناجح: $body")
                val text = JSONObject(body).getString("text")
                android.util.Log.d("API_RESPONSE", "النص: $text")
                if (text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onTranscriptionReceived?.invoke(text)
                    }
                }
            } else {
                android.util.Log.d("API_RESPONSE", "فشل: ${response.code}")
                onError?.invoke("خطأ: ${response.code}")
            }

        } catch (e: Exception) {
            android.util.Log.d("API_RESPONSE", "استثناء: ${e.message}")
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
