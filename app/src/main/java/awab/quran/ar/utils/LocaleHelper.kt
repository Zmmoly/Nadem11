package awab.quran.ar.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "nadeem_prefs"
    private const val KEY_LANGUAGE = "app_language"

    fun saveLanguage(context: Context, langCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, langCode)
            .apply()
    }

    fun getSavedLanguage(context: Context): String {
        val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, null)

        if (saved != null) return saved

        val deviceLang = Locale.getDefault().language
        val supportedLangs = setOf("ar", "en", "in", "ms", "tr", "kk", "ru")
        return if (deviceLang in supportedLangs) deviceLang else "ar"
    }

    fun applyLocale(context: Context): Context {
        val lang = getSavedLanguage(context)
        return wrap(context, lang)
    }

    fun wrap(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
