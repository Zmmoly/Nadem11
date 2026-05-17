package awab.quran.ar.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import awab.quran.ar.MainActivity
import awab.quran.ar.utils.LocaleHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NadeemFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID   = "nadeem_general"
        private const val CHANNEL_NAME = "إشعارات نديم"
        private const val NOTIF_ID     = 1001

        // اشتراك المستخدم في topic لغته الحالية وإلغاء باقي اللغات
        fun subscribeToLanguageTopic(context: Context) {
            val currentLang = LocaleHelper.getSavedLanguage(context)
            val allLangs    = listOf("ar", "en", "in", "ms", "tr", "kk", "ru")

            allLangs.forEach { lang ->
                val topic = "lang_$lang"
                if (lang == currentLang) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic)
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                }
            }
        }
    }

    // يُستدعى عند وصول رسالة
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "نديم"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: return

        showNotification(title, body)
    }

    // يُستدعى عند تجديد التوكن — نعيد الاشتراك تلقائياً
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        subscribeToLanguageTopic(applicationContext)
    }

    private fun showNotification(title: String, body: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(NOTIF_ID, notification)
    }
}
