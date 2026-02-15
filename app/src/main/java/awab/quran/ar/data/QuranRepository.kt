package awab.quran.ar.data

import android.content.Context
import org.json.JSONObject

class QuranRepository(private val context: Context) {
    
    private var quranData: Map<String, List<Ayah>>? = null
    
    // قراءة ملف JSON
    fun loadQuranData(): Map<String, List<Ayah>> {
        if (quranData == null) {
            val jsonString = context.assets.open("quran.json")
                .bufferedReader()
                .use { it.readText() }
            
            val jsonObject = JSONObject(jsonString)
            val data = mutableMapOf<String, List<Ayah>>()
            
            jsonObject.keys().forEach { surahKey ->
                val ayahsArray = jsonObject.getJSONArray(surahKey)
                val ayahsList = mutableListOf<Ayah>()
                
                for (i in 0 until ayahsArray.length()) {
                    val ayahObj = ayahsArray.getJSONObject(i)
                    ayahsList.add(
                        Ayah(
                            number = ayahObj.getInt("number"),
                            text = ayahObj.getString("text")
                        )
                    )
                }
                
                data[surahKey] = ayahsList
            }
            
            quranData = data
        }
        return quranData!!
    }
    
    // الحصول على آيات سورة معينة
    fun getSurahAyahs(surahNumber: Int): List<Ayah> {
        val data = loadQuranData()
        return data[surahNumber.toString()] ?: emptyList()
    }
    
    // الحصول على آية محددة
    fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? {
        return getSurahAyahs(surahNumber)
            .find { it.number == ayahNumber }
    }
    
    // الحصول على نص سورة كامل
    fun getSurahFullText(surahNumber: Int): String {
        val ayahs = getSurahAyahs(surahNumber)
        return ayahs.joinToString(" ۝ ") { it.text }
    }
}
