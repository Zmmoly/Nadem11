package awab.quran.ar.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import awab.quran.ar.MainActivity
import awab.quran.ar.R
import java.util.concurrent.TimeUnit

class QuranReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val prefs = context.getSharedPreferences("nadeem_prefs", Context.MODE_PRIVATE)
        val lastOpenMs = prefs.getLong("last_open_timestamp", 0L)

        if (lastOpenMs == 0L) return Result.success()

        val now = System.currentTimeMillis()
        val daysSince = (now - lastOpenMs) / (1000L * 60 * 60 * 24)

        if (daysSince >= 3) {
            sendNotification()
        }

        return Result.success()
    }

    private fun sendNotification() {
        val channelId = "quran_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // إنشاء القناة (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تذكير القرآن",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير بمراجعة القرآن الكريم"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // عند الضغط على الإشعار يفتح التطبيق
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("نادم — تذكير قرآني")
            .setContentText("لا تهجر كتاب الله، مرّت ٣ أيام منذ آخر مرة فتحت التطبيق")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("لا تهجر كتاب الله ﷻ، مرّت ٣ أيام منذ آخر مرة تلوت أو سمّعت. عُد إلى القرآن الكريم.")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1001, notification)
    }

    companion object {
        private const val WORK_NAME = "quran_reminder_work"

        // جدولة التحقق كل يوم
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<QuranReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
