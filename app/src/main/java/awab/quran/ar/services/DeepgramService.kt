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

    // \u2705 \u062b\u0644\u0627\u062b\u0629 URLs \u0645\u0646\u0641\u0635\u0644\u0629
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

    // \u0639\u0645\u064a\u0644 HTTP \u0639\u0627\u062f\u064a \u0644\u0644\u062a\u0641\u0631\u064a\u063a \u0627\u0644\u0635\u0648\u062a\u064a
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // \u2705 \u0639\u0645\u064a\u0644 \u0633\u0631\u064a\u0639 \u0644\u0644\u062a\u062d\u0642\u0642 \u0645\u0646 /health (\u062b\u0627\u0646\u064a\u062a\u0627\u0646 \u0641\u0642\u0637)
    private val fastHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    // \u2705 \u0639\u0645\u064a\u0644 \u0644\u0644\u0625\u064a\u0642\u0627\u0638 \u2014 \u064a\u0646\u062a\u0638\u0631 \u0623\u0637\u0648\u0644 \u0644\u0623\u0646 Modal \u064a\u062d\u0645\u0651\u0644 \u0627\u0644\u0646\u0645\u0648\u0630\u062c
    private val warmupHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    var onTranscriptionReceived: ((String) -> Unit)? = null
    var onInterimTranscription: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onConnectionEstablished: (() -> Unit)? = null
    var onModelChanged: ((String) -> Unit)? = null

    fun startRecitation() {
        if (isRecording) return
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_
