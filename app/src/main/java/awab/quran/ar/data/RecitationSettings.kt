package awab.quran.ar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance
val Context.recitationDataStore: DataStore<Preferences> by preferencesDataStore(name = "recitation_settings")

data class RecitationSettings(
    val ignoreTashkeel: Boolean = true,     // تجاهل التشكيل
    val ignoreHaa: Boolean = false,          // تجاهل حرف الحاء (ح/ه)
    val ignoreAyn: Boolean = false,          // تجاهل حرف العين (ع/ء/أ)
    val ignoreMadd: Boolean = false,         // تجاهل المدود
    val ignoreWaqf: Boolean = false          // تجاهل مواضع الوقف
)

class RecitationSettingsRepository(private val context: Context) {

    companion object {
        val KEY_IGNORE_TASHKEEL = booleanPreferencesKey("ignore_tashkeel")
        val KEY_IGNORE_HAA      = booleanPreferencesKey("ignore_haa")
        val KEY_IGNORE_AYN      = booleanPreferencesKey("ignore_ayn")
        val KEY_IGNORE_MADD     = booleanPreferencesKey("ignore_madd")
        val KEY_IGNORE_WAQF     = booleanPreferencesKey("ignore_waqf")
    }

    val settingsFlow: Flow<RecitationSettings> = context.recitationDataStore.data.map { prefs ->
        RecitationSettings(
            ignoreTashkeel = prefs[KEY_IGNORE_TASHKEEL] ?: true,
            ignoreHaa      = prefs[KEY_IGNORE_HAA]      ?: false,
            ignoreAyn      = prefs[KEY_IGNORE_AYN]      ?: false,
            ignoreMadd     = prefs[KEY_IGNORE_MADD]     ?: false,
            ignoreWaqf     = prefs[KEY_IGNORE_WAQF]     ?: false
        )
    }

    suspend fun save(settings: RecitationSettings) {
        context.recitationDataStore.edit { prefs ->
            prefs[KEY_IGNORE_TASHKEEL] = settings.ignoreTashkeel
            prefs[KEY_IGNORE_HAA]      = settings.ignoreHaa
            prefs[KEY_IGNORE_AYN]      = settings.ignoreAyn
            prefs[KEY_IGNORE_MADD]     = settings.ignoreMadd
            prefs[KEY_IGNORE_WAQF]     = settings.ignoreWaqf
        }
    }
}
