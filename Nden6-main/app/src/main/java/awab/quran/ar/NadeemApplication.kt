package awab.quran.ar

import android.app.Application
import com.google.firebase.FirebaseApp

class NadeemApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
