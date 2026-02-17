package awab.quran.ar.data

import android.content.Context

/**
 * مستودع بيانات صفحات القرآن
 * يستخدم QuranPages.kt الموجود بالفعل في المشروع
 */
class QuranPageRepository(private val context: Context) {
    
    // استخدام QuranRepository الموجود للحصول على نصوص الآيات
    private val quranRepository = QuranRepository(context)
    
    /**
     * الحصول على صفحة معينة بكل آياتها
     */
    fun getPage(pageNumber: Int): QuranPage? {
        if (pageNumber < 1 || pageNumber > 604) return null
        
        // الحصول على معلومات الصفحة من QuranPages الموجود
        val pageInfo = QuranPages.getPageInfo(pageNumber) ?: return null
        
        val ayahs = mutableListOf<PageAyah>()
        
        // جمع جميع الآيات في الصفحة
        var currentSura = pageInfo.startSura
        var currentAya = pageInfo.startAya
        
        var isFirstAyah = true
        
        while (true) {
            // التحقق من وصولنا لنهاية الصفحة
            if (currentSura > pageInfo.endSura) break
            if (currentSura == pageInfo.endSura && currentAya > pageInfo.endAya) break
            
            // الحصول على الآيات من QuranRepository
            val suraAyahs = quranRepository.getSurahAyahs(currentSura)
            val ayah = suraAyahs.getOrNull(currentAya - 1)
            
            if (ayah != null) {
                // الحصول على اسم السورة
                val suraName = getSuraName(currentSura)
                
                // تحديد إذا كانت أول/آخر آية في السورة
                val isFirstInSura = (currentAya == 1)
                val isLastInSura = (currentAya == suraAyahs.size)
                
                // تحديد إذا كانت آخر آية في الصفحة
                val isLastInPage = (currentSura == pageInfo.endSura && 
                                   currentAya == pageInfo.endAya)
                
                // حذف البسملة من نص الآية الأولى لأنها تُعرض منفصلة كـ BasmalaHeader
                // (ما عدا الفاتحة التي بسملتها هي الآية 1، والتوبة التي لا بسملة لها)
                val basmalaPrefix = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ"
                val ayahText = if (isFirstInSura && currentSura != 1 && currentSura != 9 &&
                    ayah.text.startsWith(basmalaPrefix)) {
                    ayah.text.removePrefix(basmalaPrefix).trim()
                } else {
                    ayah.text
                }

                ayahs.add(
                    PageAyah(
                        suraNumber = currentSura,
                        suraName = suraName,
                        ayaNumber = currentAya,
                        text = ayahText,
                        isFirstInPage = isFirstAyah,
                        isLastInPage = isLastInPage,
                        isFirstInSura = isFirstInSura,
                        isLastInSura = isLastInSura
                    )
                )
                
                isFirstAyah = false
            }
            
            // الانتقال للآية التالية
            currentAya++
            
            // إذا انتهت آيات السورة، انتقل للسورة التالية
            val suraAyahCount = quranRepository.getSurahAyahs(currentSura).size
            if (currentAya > suraAyahCount) {
                currentSura++
                currentAya = 1
            }
        }
        
        return QuranPage(
            pageNumber = pageNumber,
            startSura = pageInfo.startSura,
            startAya = pageInfo.startAya,
            ayahs = ayahs
        )
    }
    
    /**
     * البحث عن رقم الصفحة التي تحتوي على سورة وآية معينة
     */
    fun findPageNumber(suraNumber: Int, ayaNumber: Int): Int? {
        return QuranPages.getPageForAya(suraNumber, ayaNumber)
    }
    
    /**
     * الحصول على اسم السورة
     */
    private fun getSuraName(suraNumber: Int): String {
        return when (suraNumber) {
            1 -> "الفاتحة"
            2 -> "البقرة"
            3 -> "آل عمران"
            4 -> "النساء"
            5 -> "المائدة"
            6 -> "الأنعام"
            7 -> "الأعراف"
            8 -> "الأنفال"
            9 -> "التوبة"
            10 -> "يونس"
            11 -> "هود"
            12 -> "يوسف"
            13 -> "الرعد"
            14 -> "إبراهيم"
            15 -> "الحجر"
            16 -> "النحل"
            17 -> "الإسراء"
            18 -> "الكهف"
            19 -> "مريم"
            20 -> "طه"
            21 -> "الأنبياء"
            22 -> "الحج"
            23 -> "المؤمنون"
            24 -> "النور"
            25 -> "الفرقان"
            26 -> "الشعراء"
            27 -> "النمل"
            28 -> "القصص"
            29 -> "العنكبوت"
            30 -> "الروم"
            31 -> "لقمان"
            32 -> "السجدة"
            33 -> "الأحزاب"
            34 -> "سبأ"
            35 -> "فاطر"
            36 -> "يس"
            37 -> "الصافات"
            38 -> "ص"
            39 -> "الزمر"
            40 -> "غافر"
            41 -> "فصلت"
            42 -> "الشورى"
            43 -> "الزخرف"
            44 -> "الدخان"
            45 -> "الجاثية"
            46 -> "الأحقاف"
            47 -> "محمد"
            48 -> "الفتح"
            49 -> "الحجرات"
            50 -> "ق"
            51 -> "الذاريات"
            52 -> "الطور"
            53 -> "النجم"
            54 -> "القمر"
            55 -> "الرحمن"
            56 -> "الواقعة"
            57 -> "الحديد"
            58 -> "المجادلة"
            59 -> "الحشر"
            60 -> "الممتحنة"
            61 -> "الصف"
            62 -> "الجمعة"
            63 -> "المنافقون"
            64 -> "التغابن"
            65 -> "الطلاق"
            66 -> "التحريم"
            67 -> "الملك"
            68 -> "القلم"
            69 -> "الحاقة"
            70 -> "المعارج"
            71 -> "نوح"
            72 -> "الجن"
            73 -> "المزمل"
            74 -> "المدثر"
            75 -> "القيامة"
            76 -> "الإنسان"
            77 -> "المرسلات"
            78 -> "النبأ"
            79 -> "النازعات"
            80 -> "عبس"
            81 -> "التكوير"
            82 -> "الإنفطار"
            83 -> "المطففين"
            84 -> "الإنشقاق"
            85 -> "البروج"
            86 -> "الطارق"
            87 -> "الأعلى"
            88 -> "الغاشية"
            89 -> "الفجر"
            90 -> "البلد"
            91 -> "الشمس"
            92 -> "الليل"
            93 -> "الضحى"
            94 -> "الشرح"
            95 -> "التين"
            96 -> "العلق"
            97 -> "القدر"
            98 -> "البينة"
            99 -> "الزلزلة"
            100 -> "العاديات"
            101 -> "القارعة"
            102 -> "التكاثر"
            103 -> "العصر"
            104 -> "الهمزة"
            105 -> "الفيل"
            106 -> "قريش"
            107 -> "الماعون"
            108 -> "الكوثر"
            109 -> "الكافرون"
            110 -> "النصر"
            111 -> "المسد"
            112 -> "الإخلاص"
            113 -> "الفلق"
            114 -> "الناس"
            else -> "السورة $suraNumber"
        }
    }
}

/**
 * نموذج بيانات صفحة المصحف
 */
data class QuranPage(
    val pageNumber: Int,
    val startSura: Int,
    val startAya: Int,
    val ayahs: List<PageAyah>
)

/**
 * نموذج بيانات آية في الصفحة
 */
data class PageAyah(
    val suraNumber: Int,
    val suraName: String,
    val ayaNumber: Int,
    val text: String,
    val isFirstInPage: Boolean = false,
    val isLastInPage: Boolean = false,
    val isFirstInSura: Boolean = false,
    val isLastInSura: Boolean = false
)
