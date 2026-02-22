package awab.quran.ar

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import awab.quran.ar.ui.navigation.NadeemNavigation
import awab.quran.ar.ui.theme.NadeemTheme
import com.facebook.CallbackManager

class MainActivity : ComponentActivity() {

    // Facebook CallbackManager — يجب أن يكون هنا ليستقبل onActivityResult
    val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NadeemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NadeemNavigation()
                }
            }
        }
    }

    // مطلوب لكي تعمل نتيجة Facebook Login
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
