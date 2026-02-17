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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DeepgramService(private val context: Context) {
    
    private val apiKey = "bd345e01709fb47368c5d12e56a124f2465fdf8d"
    private val websocketUrl = "wss://api.deepgram.com/v1/listen?" +
            "token=$apiKey&" +
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
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    
    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null
    
    /**
     * بدء التسميع والاتصال بـ Deepgram
     */
    fun startRecitation() {
        if (isRecording) return
        
        // التحقق من صلاحية الميكروفون
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError?.invoke("صلاحية الميكروفون غير ممنوحة")
            return
        }
        
        // إنشاء اتصال WebSocket
        connectWebSocket()
    }
    
    /**
     * إيقاف التسميع
     */
    fun stopRecitation() {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null
        
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        
        // إغلاق WebSocket
        webSocket?.close(1000, "تم إنهاء التسميع")
        webSocket = null
    }
    
    /**
     * إنشاء اتصال WebSocket مع Deepgram
     */
    private fun connectWebSocket() {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(0, java.util.concurrent.TimeUnit.SECONDS)   // 0 = لا timeout للقراءة
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        
        val request = Request.Builder()
            .url(websocketUrl)
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Deepgram WebSocket opened successfully")
                onConnectionEstablished?.invoke()
                startAudioCapture()
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received message: $text")
                handleTranscription(text)
            }
            
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // غير مستخدم
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val errorMsg = "خطأ في الاتصال: ${t.message}"
                println("WebSocket failure: ${t.message}")
                println("Response: ${response?.code} - ${response?.message}")
                response?.body?.string()?.let { body ->
                    println("Response body: $body")
                }
                onError?.invoke(errorMsg)
                stopRecitation()
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closing: $code - $reason")
                stopRecitation()
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $code - $reason")
            }
        })
    }
    
    /**
     * بدء التقاط الصوت وإرساله للـ WebSocket
     */
    private fun startAudioCapture() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize * 2
        )
        
        audioRecord?.startRecording()
        isRecording = true
        
        // بدء إرسال الصوت في الخلفية
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(bufferSize)
            
            while (isActive && isRecording) {
                val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                
                if (readSize > 0) {
                    // تحويل البيانات إلى ByteString وإرسالها
                    val byteArray = buffer.copyOfRange(0, readSize)
                    val byteString = ByteString.of(*byteArray)
                    webSocket?.send(byteString)
                }
                
                // تأخير صغير لتجنب الضغط الزائد
                delay(10)
            }
        }
    }
    
    /**
     * معالجة النص المستلم من Deepgram
     */
    private fun handleTranscription(jsonText: String) {
        try {
            val json = JSONObject(jsonText)
            
            // التحقق من وجود نتيجة
            if (json.has("channel")) {
                val channel = json.getJSONObject("channel")
                val alternatives = channel.getJSONArray("alternatives")
                
                if (alternatives.length() > 0) {
                    val transcript = alternatives.getJSONObject(0).getString("transcript")
                    
                    // إرسال النص المستخرج
                    if (transcript.isNotEmpty()) {
                        onTranscriptionReceived?.invoke(transcript)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * التحقق من حالة التسجيل
     */
    fun isRecording(): Boolean = isRecording
}
