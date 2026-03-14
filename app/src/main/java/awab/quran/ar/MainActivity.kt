package awab.quran.ar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import awab.quran.ar.data.ThemeRepository
import awab.quran.ar.ui.navigation.NadeemNavigation
import awab.quran.ar.ui.theme.NadeemTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
