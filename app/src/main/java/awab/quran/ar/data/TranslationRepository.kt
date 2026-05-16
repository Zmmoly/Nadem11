package awab.quran.ar.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * يقرأ ملفي النقحرة والترجمة مباشرةً من assets
 * الملفان المطلوبان في assets/:
 *   - transliteration.txt  (من tanzil.net/trans/en.transliteration)
 *   - translation.txt      (من tanzil.net/trans/en.sahih)
 *
 * تنسيق كل سطر في الملفين: surahNumber|ayahNumber|text
 */
class TranslationRepository(private val context: Context) {

    companion object {
        private const val TRANSLITERATION_FILE = "transliteration.txt"
        private const val TRANSLATION_FILE     = "translation.txt"
    }

    // كاش في الذاكرة: Pair(surah, ayah) -> نص
    private var transliterationMap: Map<Pair<Int,Int>, String>? = null
    private var translationMap:     Map<Pair<Int,Int>, String>? = null

    fun isTransliterationAvailable(): Boolean = assetExists(TRANSLITERATION_FILE)
    fun isTranslationAvailable():     Boolean = assetExists(TRANSLATION_FILE)

    private fun assetExists(fileName: String): Boolean = try {
        context.assets.open(fileName).close(); true
    } catch (e: Exception) { false }

    private fun parseAssetFile(fileName: String): Map<Pair<Int,Int>, String> {
        val map = mutableMapOf<Pair<Int,Int>, String>()
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

    fun getTransliteration(surah: Int, ayah: Int): String? {
        if (transliterationMap == null)
            transliterationMap = parseAssetFile(TRANSLITERATION_FILE)
        return transliterationMap?.get(Pair(surah, ayah))
    }

    fun getTranslation(surah: Int, ayah: Int): String? {
        if (translationMap == null)
            translationMap = parseAssetFile(TRANSLATION_FILE)
        return translationMap?.get(Pair(surah, ayah))
    }

    suspend fun getPageTranslations(
        ayahs: List<PageAyah>
    ): Map<Pair<Int,Int>, AyahTranslation> = withContext(Dispatchers.IO) {
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
