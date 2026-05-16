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
 *    assets/translation_en.txt    ← tanzil.net/trans/en.sahih
 *    assets/translation_in.txt    ← tanzil.net/trans/id.indonesian
 *    assets/translation_ms.txt    ← tanzil.net/trans/ms.basmeih
 *    assets/translation_tr.txt    ← tanzil.net/trans/tr.diyanet
 *    assets/translation_kk.txt    ← tanzil.net/trans/kk.altai
 *
 *  تنسيق كل سطر: surahNumber|ayahNumber|text
 * ──────────────────────────────────────────────────────────────────────────
 */
class TranslationRepository(private val context: Context) {

    companion object {
        private const val TRANSLITERATION_FILE = "transliteration.txt"

        private val TRANSLATION_FILES = mapOf(
            "en" to "translation_en.txt",
            "in" to "translation_in.txt",
            "ms" to "translation_ms.txt",
            "tr" to "translation_tr.txt",
            "kk" to "translation_kk.txt"
        )

        private const val FALLBACK_LANG = "en"
    }

    private var transliterationMap: Map<Pair<Int, Int>, String>? = null
    private var translationMap: Map<Pair<Int, Int>, String>? = null
    private var loadedTranslationLang: String? = null

    private fun currentLang(): String = LocaleHelper.getSavedLanguage(context)

    private fun translationFileName(): String {
        val lang = currentLang()
        return TRANSLATION_FILES[lang] ?: TRANSLATION_FILES[FALLBACK_LANG]!!
    }

    fun isTransliterationAvailable(): Boolean = assetExists(TRANSLITERATION_FILE)

    fun isTranslationAvailable(): Boolean {
        val lang = currentLang()
        if (lang == "ar") return false
        val file = TRANSLATION_FILES[lang] ?: TRANSLATION_FILES[FALLBACK_LANG]!!
        return assetExists(file)
    }

    private fun assetExists(fileName: String): Boolean = try {
        context.assets.open(fileName).close(); true
    } catch (e: Exception) { false }

    private fun parseAssetFile(fileName: String, isTransliteration: Boolean = false): Map<Pair<Int, Int>, String> {
        val map = mutableMapOf<Pair<Int, Int>, String>()
        try {
            context.assets.open(fileName).bufferedReader().forEachLine { line ->
                if (line.isBlank() || line.startsWith("#")) return@forEachLine
                val parts = line.split("|", limit = 3)
                if (parts.size == 3) {
                    val surah = parts[0].trim().toIntOrNull() ?: return@forEachLine
                    val ayah  = parts[1].trim().toIntOrNull() ?: return@forEachLine
                    val text = if (isTransliteration)
                        convertTransliterationTags(parts[2].trim())
                    else
                        parts[2].trim()
                    map[Pair(surah, ayah)] = text
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    /**
     * تحويل وسوم HTML في ملف النقحرة إلى رموز أكاديمية صحيحة
     * <u>x</u> -> ā ḥ ṣ ṭ ṯ ḍ ẓ ī ū
     * <b>x</b> -> يبقى النص كما هو
     */
    private fun convertTransliterationTags(text: String): String {
        val uMap = mapOf(
            "aa" to "ā", "ee" to "ī", "oo" to "ū",
            "a"  to "ā", "i"  to "ī", "u"  to "ū",
            "ss" to "ṣ", "dd" to "ḍ", "th" to "ṯ",
            "h"  to "ḥ", "s"  to "ṣ", "t"  to "ṭ",
            "d"  to "ḍ", "z"  to "ẓ",
        )

        var result = text

        result = Regex("<[uU]>([^<]+)<\\/[uU]>", RegexOption.IGNORE_CASE).replace(result) { m ->
            val inner = m.groupValues[1]
            val key = inner.lowercase()
            uMap.keys.sortedByDescending { it.length }
                .firstOrNull { key == it }
                ?.let { uMap[it] } ?: inner
        }

        result = Regex("<[bB]>([^<]+)<\\/[bB]>", RegexOption.IGNORE_CASE).replace(result) { m ->
            m.groupValues[1]
        }

        result = result.replace(Regex("<[^>]+>"), "")

        return result
    }

    fun getTransliteration(surah: Int, ayah: Int): String? {
        if (transliterationMap == null)
            transliterationMap = parseAssetFile(TRANSLITERATION_FILE, isTransliteration = true)
        return transliterationMap?.get(Pair(surah, ayah))
    }

    fun getTranslation(surah: Int, ayah: Int): String? {
        val targetFile = translationFileName()
        if (loadedTranslationLang != currentLang()) {
            translationMap = null
            loadedTranslationLang = currentLang()
        }
        if (translationMap == null)
            translationMap = parseAssetFile(targetFile)
        return translationMap?.get(Pair(surah, ayah))
    }

    /** مسح الكاش عند تغيير اللغة */
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
