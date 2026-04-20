package awab.quran.ar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import awab.quran.ar.data.ThemeRepository
import awab.quran.ar.ui.navigation.NadeemNavigation
import awab.quran.ar.ui.theme.NadeemTheme
import awab.quran.ar.workers.QuranReminderWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // سواء قبل أو رفض، نجدول الـ Worker (يعمل على أقل من 13 بدون إذن)
        QuranReminderWorker.schedule(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // حفظ وقت آخر فتح للتطبيق
        getSharedPreferences("nadeem_prefs", MODE_PRIVATE)
            .edit()
            .putLong("last_open_timestamp", System.currentTimeMillis())
            .apply()

        // طلب إذن الإشعارات (Android 13+) أو جدولة مباشرة للإصدارات الأقدم
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                QuranReminderWorker.schedule(this)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            QuranReminderWorker.schedule(this)
        }

        // قراءة الثيم المحفوظ قبل العرض لتجنب الوميض
        val themeRepo = ThemeRepository(this)
        val initialDarkMode = runBlocking { themeRepo.isDarkModeFlow.first() }

        setContent {
            val isDarkMode = remember { mutableStateOf(initialDarkMode) }

            NadeemTheme(darkTheme = isDarkMode.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NadeemNavigation(
                        isDarkMode = isDarkMode.value,
                        onToggleDarkMode = { isDarkMode.value = it }
                    )
                }
            }
        }
    }
}
