package awab.quran.ar.data

import android.content.Context
import awab.quran.ar.utils.LocaleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * يقرأ ملفي النقحرة والترجمة من assets بناءً على لغة التطبيق الحالية.
 *
 * ─── مصادر الملفات (tanzil.net) ───────────────────────────────────────────
 *
 *  النقحرة (واحدة لجميع اللغات):
 *    assets/transliteration.txt   ← tanzil.net/trans/en.transliteration
 *
 *  الترجمات (ملف لكل لغة):
 *    assets/translation_ar.txt    ← الترجمة العربية (النص الأصلي - لا حاجة لها لأن القرآن عربي)
 *    assets/translation_en.txt    ← tanzil.net/trans/en.sahih       (Saheeh International)
 *    assets/translation_in.txt    ← tanzil.net/trans/id.indonesian  (Kementerian Agama RI) [الحالي = translation.txt]
 *    assets/translation_ms.txt    ← tanzil.net/trans/ms.basmeih     (Basmeih)
 *    assets/translation_tr.txt    ← tanzil.net/trans/tr.diyanet      (Diyanet İşleri)
 *    assets/translation_kk.txt    ← tanzil.net/trans/kk.altai        (Khalifah Altai)
 *
 *  تنسيق كل سطر في جميع الملفات: surahNumber|ayahNumber|text
 * ──────────────────────────────────────────────────────────────────────────
 */
class TranslationRepository(private val context: Context) {

    companion object {
        private const val TRANSLITERATION_FILE = "transliteration.txt"

        /** خريطة رمز اللغة ← اسم ملف الترجمة في assets */
        private val TRANSLATION_FILES = mapOf(
            "en" to "translation_en.txt",
            "in" to "translation_in.txt",
            "ms" to "translation_ms.txt",
            "tr" to "translation_tr.txt",
            "kk" to "translation_kk.txt"
            // العربية لا تحتاج ترجمة (النص القرآني هو الأصل)
        )

        /** fallback: إن لم يوجد ملف اللغة استخدم الإنجليزية */
        private const val FALLBACK_LANG = "en"
    }

    // كاش في الذاكرة
    private var transliterationMap: Map<Pair<Int, Int>, String>? = null
    private var translationMap: Map<Pair<Int, Int>, String>? = null
    private var loadedTranslationLang: String? = null

    // ── اللغة الحالية ────────────────────────────────────────────────────────
    private fun currentLang(): String = LocaleHelper.getSavedLanguage(context)

    private fun translationFileName(): String {
        val lang = currentLang()
        return TRANSLATION_FILES[lang]
            ?: TRANSLATION_FILES[FALLBACK_LANG]!!
    }

    // ── التحقق من وجود الملفات ───────────────────────────────────────────────
    fun isTransliterationAvailable(): Boolean = assetExists(TRANSLITERATION_FILE)

    fun isTranslationAvailable(): Boolean {
        val lang = currentLang()
        if (lang == "ar") return false          // العربية لا تحتاج ترجمة
        val file = TRANSLATION_FILES[lang] ?: TRANSLATION_FILES[FALLBACK_LANG]!!
        return assetExists(file)
    }

    private fun assetExists(fileName: String): Boolean = try {
        context.assets.open(fileName).close(); true
    } catch (e: Exception) { false }

    // ── قراءة الملف ──────────────────────────────────────────────────────────
    private fun parseAssetFile(fileName: String): Map<Pair<Int, Int>, String> {
        val map = mutableMapOf<Pair<Int, Int>, String>()
        try {
            context.assets.open(fileName).bufferedReader().forEachLine { line ->
                if (line.isBlank() || line.startsWith("#")) return@forEachLine
                val parts = line.split("|", limit = 3)
                if (parts.size == 3) {
                    val surah = parts[0].trim().toIntOrNull() ?: return@forEachLine
                    val ayah  = parts[1].trim().toIntOrNull() ?: return@forEachLine
                    map[Pair(surah, ayah)] = parts[2].trim()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    // ── Getters مع كاش ────────────────────────────────────────────────────────
    fun getTransliteration(surah: Int, ayah: Int): String? {
        if (transliterationMap == null)
            transliterationMap = parseAssetFile(TRANSLITERATION_FILE)
        return transliterationMap?.get(Pair(surah, ayah))
    }

    fun getTranslation(surah: Int, ayah: Int): String? {
        val targetFile = translationFileName()
        // أعد تحميل الكاش إن تغيّرت اللغة
        if (loadedTranslationLang != currentLang()) {
            translationMap = null
            loadedTranslationLang = currentLang()
        }
        if (translationMap == null)
            translationMap = parseAssetFile(targetFile)
        return translationMap?.get(Pair(surah, ayah))
    }

    /** مسح الكاش عند تغيير اللغة (استدعِ من ProfileScreen بعد تغيير اللغة) */
    fun clearCache() {
        translationMap = null
        loadedTranslationLang = null
    }

    suspend fun getPageTranslations(
        ayahs: List<PageAyah>
    ): Map<Pair<Int, Int>, AyahTranslation> = withContext(Dispatchers.IO) {
        ayahs.associate { ayah ->
            Pair(ayah.suraNumber, ayah.ayaNumber) to AyahTranslation(
                transliteration = getTransliteration(ayah.suraNumber, ayah.ayaNumber),
                translation     = getTranslation(ayah.suraNumber, ayah.ayaNumber)
            )
        }
    }
}

data class AyahTranslation(
    val transliteration: String? = null,
    val translation:     String? = null
)
