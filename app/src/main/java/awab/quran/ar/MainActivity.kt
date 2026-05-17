package awab.quran.ar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import awab.quran.ar.data.ThemeRepository
import awab.quran.ar.ui.navigation.NadeemNavigation
import awab.quran.ar.ui.theme.NadeemTheme
import awab.quran.ar.utils.ForceUpdateChecker
import awab.quran.ar.utils.LocaleHelper
import awab.quran.ar.workers.QuranReminderWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        QuranReminderWorker.schedule(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSharedPreferences("nadeem_prefs", MODE_PRIVATE)
            .edit()
            .putLong("last_open_timestamp", System.currentTimeMillis())
            .apply()

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

        val themeRepo = ThemeRepository(this)
        val initialDarkMode = runBlocking { themeRepo.isDarkModeFlow.first() }

        // versionCode الحالي
        val currentVersionCode = packageManager
            .getPackageInfo(packageName, 0)
            .longVersionCode.toInt()

        setContent {
            val isDarkMode = remember { mutableStateOf(initialDarkMode) }

            // ── حالة التحديث الإجباري ──
            var showUpdateDialog by remember { mutableStateOf(false) }

            // فحص التحديث الإجباري عبر Remote Config
            LaunchedEffect(Unit) {
                ForceUpdateChecker.check(
                    currentVersionCode = currentVersionCode,
                    onUpdateRequired   = { showUpdateDialog = true },
                    onUpToDate         = { /* لا شيء */ }
                )
            }

            NadeemTheme(darkTheme = isDarkMode.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NadeemNavigation(
                        isDarkMode      = isDarkMode.value,
                        onToggleDarkMode = { isDarkMode.value = it }
                    )

                    // ── Dialog التحديث الإجباري ──
                    if (showUpdateDialog) {
                        AlertDialog(
                            onDismissRequest = { /* لا تسمح بالإغلاق */ },
                            title   = { Text("تحديث مطلوب") },
                            text    = { Text("يوجد إصدار جديد من التطبيق. يرجى التحديث للمتابعة.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    // افتح صفحة التطبيق في المتجر
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    startActivity(intent)
                                }) {
                                    Text("تحديث الآن")
                                }
                            }
                            // بدون dismissButton = لا يمكن تجاهل الـ Dialog
                        )
                    }
                }
            }
        }
    }
}
