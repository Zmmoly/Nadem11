package awab.quran.ar

import android.app.Application
import android.content.Context
import awab.quran.ar.utils.LocaleHelper
import com.google.firebase.FirebaseApp

class NadeemApplication : Application() {

    // ── تطبيق اللغة المحفوظة على مستوى الـ Application كاملاً ──
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
