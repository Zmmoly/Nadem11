package awab.quran.ar.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

class DeepgramService(private val context: Context) {

    private val apiKey = "bd345e01709fb47368c5d12e56a124f2465fdf8d"

    // الـ API Key عبر Header + encoding وsample_rate في URL
    private val websocketUrl = "wss://api.deepgram.com/v1/listen?" +
            "language=ar&" +
            "model=nova-3&" +
            "smart_format=false&" +
            "encoding=linear16&" +
            "sample_rate=16000&" +
            "channels=1"

    private var webSocket: WebSocket? = null
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = maxOf(
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 4,
        3200
    )

    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null

    fun startRecitation() {
        if (isRecording) return

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError?.invoke("صلاحية الميكروفون غير ممنوحة")
            return
        }

        connectWebSocket()
    }

    fun stopRecitation() {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null

        webSocket?.close(1000, "تم إنهاء التسميع")
        webSocket = null
    }

    private fun connectWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(websocketUrl)
            .addHeader("Authorization", "Token $apiKey")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("✅ WebSocket opened")
                onConnectionEstablished?.invoke()
                startAudioCapture()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("📩 Message: $text")
                handleTranscription(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("❌ WebSocket failure: ${t.message}")
                onError?.invoke("خطأ في الاتصال: ${t.message}")
                stopRecitation()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("🔴 WebSocket closing: $code - $reason")
                stopRecitation()
            }
        })
    }

    private fun startAudioCapture() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            onError?.invoke("فشل في تهيئة الميكروفون")
            recorder.release()
            return
        }

        audioRecord = recorder
        recorder.startRecording()
        isRecording = true
        println("🎤 Audio capture started, bufferSize=$bufferSize")

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(bufferSize)

            while (isActive && isRecording) {
                val readSize = recorder.read(buffer, 0, buffer.size)

                if (readSize > 0) {
                    val byteArray = buffer.copyOfRange(0, readSize)
                    val byteString = ByteString.of(*byteArray)
                    val sent = webSocket?.send(byteString) ?: false
                    println("🔊 Sent $readSize bytes, success=$sent")
                }
            }
            println("🛑 Audio capture loop ended")
        }
    }

    private fun handleTranscription(jsonText: String) {
        try {
            val json = JSONObject(jsonText)

            if (json.has("channel")) {
                val channel = json.getJSONObject("channel")
                val alternatives = channel.getJSONArray("alternatives")

                if (alternatives.length() > 0) {
                    val transcript = alternatives.getJSONObject(0).getString("transcript")

                    if (transcript.isNotEmpty()) {
                        onTranscriptionReceived?.invoke(transcript)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isRecording(): Boolean = isRecording
}
