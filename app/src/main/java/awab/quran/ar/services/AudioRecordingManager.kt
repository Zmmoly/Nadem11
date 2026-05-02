package awab.quran.ar.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * مدير تسجيل الصوت
 * يتولى حفظ المقاطع الصوتية بصيغة WAV، استرجاعها، ومشاركتها
 */
class AudioRecordingManager(private val context: Context) {

    // مجلد الحفظ داخل التطبيق
    private val recordingsDir: File by lazy {
        File(context.filesDir, "recordings").also { it.mkdirs() }
    }

    private var currentFile: File? = null
    private var currentOutputStream: FileOutputStream? = null
    private var currentSampleRate: Int = 16000
    private var totalDataBytes: Int = 0

    // ────────────────────────────────────────────────
    // بدء جلسة التسجيل
    // ────────────────────────────────────────────────
    fun startNewRecording(sampleRate: Int = 16000): File {
        stopRecording() // أوقف أي تسجيل سابق إن وُجد

        currentSampleRate = sampleRate
        totalDataBytes = 0

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(recordingsDir, "تسجيل_$timestamp.wav")

        currentOutputStream = FileOutputStream(file)
        // اكتب رأس WAV مؤقتاً (سيُحدَّث عند الإيقاف)
        writeWavHeader(currentOutputStream!!, 0, sampleRate)

        currentFile = file
        return file
    }

    // ────────────────────────────────────────────────
    // إلحاق بيانات صوتية خام (PCM 16-bit)
    // ────────────────────────────────────────────────
    fun appendAudioData(pcmData: ByteArray, length: Int) {
        currentOutputStream?.write(pcmData, 0, length)
        totalDataBytes += length
    }

    // ────────────────────────────────────────────────
    // إنهاء التسجيل وتحديث رأس WAV
    // ────────────────────────────────────────────────
    fun stopRecording(): File? {
        val file = currentFile ?: return null

        currentOutputStream?.close()
        currentOutputStream = null

        // حدّث رأس WAV بالحجم الفعلي
        updateWavHeader(file, totalDataBytes, currentSampleRate)

        currentFile = null
        totalDataBytes = 0

        return file
    }

    // ────────────────────────────────────────────────
    // حذف تسجيل
    // ────────────────────────────────────────────────
    fun deleteRecording(file: File): Boolean = file.delete()

    // ────────────────────────────────────────────────
    // استرجاع قائمة التسجيلات مرتبةً من الأحدث للأقدم
    // ────────────────────────────────────────────────
    fun getAllRecordings(): List<File> =
        recordingsDir.listFiles { f -> f.extension == "wav" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

    // ────────────────────────────────────────────────
    // فتح مشغّل الصوت (Intent)
    // ────────────────────────────────────────────────
    fun playRecording(file: File) {
        val uri = getUriForFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "audio/wav")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "استمع إلى التسجيل").also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    // ────────────────────────────────────────────────
    // مشاركة الملف
    // ────────────────────────────────────────────────
    fun shareRecording(file: File): Intent {
        val uri = getUriForFile(file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/wav"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "تسجيل قرآني - ${file.nameWithoutExtension}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // ────────────────────────────────────────────────
    // الحصول على URI آمن عبر FileProvider
    // ────────────────────────────────────────────────
    private fun getUriForFile(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    // ════════════════════════════════════════════════
    // WAV Header Utils
    // ════════════════════════════════════════════════

    /** يكتب رأس WAV بحجم مؤقت = 0 */
    private fun writeWavHeader(out: FileOutputStream, dataSize: Int, sampleRate: Int) {
        val channels = 1
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        out.write("RIFF".toByteArray())
        out.write(intToLittleEndian(36 + dataSize))
        out.write("WAVE".toByteArray())
        out.write("fmt ".toByteArray())
        out.write(intToLittleEndian(16))            // PCM chunk size
        out.write(shortToLittleEndian(1))           // PCM format
        out.write(shortToLittleEndian(channels))
        out.write(intToLittleEndian(sampleRate))
        out.write(intToLittleEndian(byteRate))
        out.write(shortToLittleEndian(blockAlign))
        out.write(shortToLittleEndian(bitsPerSample))
        out.write("data".toByteArray())
        out.write(intToLittleEndian(dataSize))
    }

    /** يُحدّث رأس WAV بالحجم الفعلي للبيانات */
    private fun updateWavHeader(file: File, dataSize: Int, sampleRate: Int) {
        if (!file.exists()) return
        try {
            RandomAccessFile(file, "rw").use { raf ->
                // RIFF chunk size = 36 + dataSize
                raf.seek(4)
                raf.write(intToLittleEndian(36 + dataSize))
                // data chunk size
                raf.seek(40)
                raf.write(intToLittleEndian(dataSize))
            }
        } catch (_: Exception) {}
    }

    private fun intToLittleEndian(value: Int): ByteArray = byteArrayOf(
        (value and 0xff).toByte(),
        (value shr 8 and 0xff).toByte(),
        (value shr 16 and 0xff).toByte(),
        (value shr 24 and 0xff).toByte()
    )

    private fun shortToLittleEndian(value: Int): ByteArray = byteArrayOf(
        (value and 0xff).toByte(),
        (value shr 8 and 0xff).toByte()
    )
}
