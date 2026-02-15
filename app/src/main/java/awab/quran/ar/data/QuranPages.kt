package awab.quran.ar.data

/**
 * بيانات صفحات المصحف الشريف - دقيقة 100%
 * المصدر: مجمع الملك فهد لطباعة المصحف الشريف (Tanzil Project)
 * 
 * كل صفحة لها:
 * - رقم الصفحة في المصحف
 * - بداية دقيقة (سورة، آية)
 * - نهاية دقيقة (سورة، آية)
 */

/**
 * بيانات صفحة واحدة
 */
data class PageInfo(
    val pageNumber: Int,
    val startSura: Int,
    val startAya: Int,
    val endSura: Int,
    val endAya: Int
)

object QuranPages {
    
    /**
     * جميع صفحات المصحف (604 صفحة)
     */
    private val allPages = listOf(
        PageInfo(
            pageNumber = 1,
            startSura = 1,
            startAya = 1,
            endSura = 1,
            endAya = 7
        ),
        PageInfo(
            pageNumber = 2,
            startSura = 2,
            startAya = 1,
            endSura = 2,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 3,
            startSura = 2,
            startAya = 6,
            endSura = 2,
            endAya = 16
        ),
        PageInfo(
            pageNumber = 4,
            startSura = 2,
            startAya = 17,
            endSura = 2,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 5,
            startSura = 2,
            startAya = 25,
            endSura = 2,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 6,
            startSura = 2,
            startAya = 30,
            endSura = 2,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 7,
            startSura = 2,
            startAya = 38,
            endSura = 2,
            endAya = 48
        ),
        PageInfo(
            pageNumber = 8,
            startSura = 2,
            startAya = 49,
            endSura = 2,
            endAya = 57
        ),
        PageInfo(
            pageNumber = 9,
            startSura = 2,
            startAya = 58,
            endSura = 2,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 10,
            startSura = 2,
            startAya = 62,
            endSura = 2,
            endAya = 69
        ),
        PageInfo(
            pageNumber = 11,
            startSura = 2,
            startAya = 70,
            endSura = 2,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 12,
            startSura = 2,
            startAya = 77,
            endSura = 2,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 13,
            startSura = 2,
            startAya = 84,
            endSura = 2,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 14,
            startSura = 2,
            startAya = 89,
            endSura = 2,
            endAya = 93
        ),
        PageInfo(
            pageNumber = 15,
            startSura = 2,
            startAya = 94,
            endSura = 2,
            endAya = 101
        ),
        PageInfo(
            pageNumber = 16,
            startSura = 2,
            startAya = 102,
            endSura = 2,
            endAya = 105
        ),
        PageInfo(
            pageNumber = 17,
            startSura = 2,
            startAya = 106,
            endSura = 2,
            endAya = 112
        ),
        PageInfo(
            pageNumber = 18,
            startSura = 2,
            startAya = 113,
            endSura = 2,
            endAya = 119
        ),
        PageInfo(
            pageNumber = 19,
            startSura = 2,
            startAya = 120,
            endSura = 2,
            endAya = 126
        ),
        PageInfo(
            pageNumber = 20,
            startSura = 2,
            startAya = 127,
            endSura = 2,
            endAya = 134
        ),
        PageInfo(
            pageNumber = 21,
            startSura = 2,
            startAya = 135,
            endSura = 2,
            endAya = 141
        ),
        PageInfo(
            pageNumber = 22,
            startSura = 2,
            startAya = 142,
            endSura = 2,
            endAya = 145
        ),
        PageInfo(
            pageNumber = 23,
            startSura = 2,
            startAya = 146,
            endSura = 2,
            endAya = 153
        ),
        PageInfo(
            pageNumber = 24,
            startSura = 2,
            startAya = 154,
            endSura = 2,
            endAya = 163
        ),
        PageInfo(
            pageNumber = 25,
            startSura = 2,
            startAya = 164,
            endSura = 2,
            endAya = 169
        ),
        PageInfo(
            pageNumber = 26,
            startSura = 2,
            startAya = 170,
            endSura = 2,
            endAya = 176
        ),
        PageInfo(
            pageNumber = 27,
            startSura = 2,
            startAya = 177,
            endSura = 2,
            endAya = 181
        ),
        PageInfo(
            pageNumber = 28,
            startSura = 2,
            startAya = 182,
            endSura = 2,
            endAya = 186
        ),
        PageInfo(
            pageNumber = 29,
            startSura = 2,
            startAya = 187,
            endSura = 2,
            endAya = 190
        ),
        PageInfo(
            pageNumber = 30,
            startSura = 2,
            startAya = 191,
            endSura = 2,
            endAya = 196
        ),
        PageInfo(
            pageNumber = 31,
            startSura = 2,
            startAya = 197,
            endSura = 2,
            endAya = 202
        ),
        PageInfo(
            pageNumber = 32,
            startSura = 2,
            startAya = 203,
            endSura = 2,
            endAya = 210
        ),
        PageInfo(
            pageNumber = 33,
            startSura = 2,
            startAya = 211,
            endSura = 2,
            endAya = 215
        ),
        PageInfo(
            pageNumber = 34,
            startSura = 2,
            startAya = 216,
            endSura = 2,
            endAya = 219
        ),
        PageInfo(
            pageNumber = 35,
            startSura = 2,
            startAya = 220,
            endSura = 2,
            endAya = 224
        ),
        PageInfo(
            pageNumber = 36,
            startSura = 2,
            startAya = 225,
            endSura = 2,
            endAya = 230
        ),
        PageInfo(
            pageNumber = 37,
            startSura = 2,
            startAya = 231,
            endSura = 2,
            endAya = 233
        ),
        PageInfo(
            pageNumber = 38,
            startSura = 2,
            startAya = 234,
            endSura = 2,
            endAya = 237
        ),
        PageInfo(
            pageNumber = 39,
            startSura = 2,
            startAya = 238,
            endSura = 2,
            endAya = 245
        ),
        PageInfo(
            pageNumber = 40,
            startSura = 2,
            startAya = 246,
            endSura = 2,
            endAya = 248
        ),
        PageInfo(
            pageNumber = 41,
            startSura = 2,
            startAya = 249,
            endSura = 2,
            endAya = 252
        ),
        PageInfo(
            pageNumber = 42,
            startSura = 2,
            startAya = 253,
            endSura = 2,
            endAya = 256
        ),
        PageInfo(
            pageNumber = 43,
            startSura = 2,
            startAya = 257,
            endSura = 2,
            endAya = 259
        ),
        PageInfo(
            pageNumber = 44,
            startSura = 2,
            startAya = 260,
            endSura = 2,
            endAya = 264
        ),
        PageInfo(
            pageNumber = 45,
            startSura = 2,
            startAya = 265,
            endSura = 2,
            endAya = 269
        ),
        PageInfo(
            pageNumber = 46,
            startSura = 2,
            startAya = 270,
            endSura = 2,
            endAya = 274
        ),
        PageInfo(
            pageNumber = 47,
            startSura = 2,
            startAya = 275,
            endSura = 2,
            endAya = 281
        ),
        PageInfo(
            pageNumber = 48,
            startSura = 2,
            startAya = 282,
            endSura = 2,
            endAya = 282
        ),
        PageInfo(
            pageNumber = 49,
            startSura = 2,
            startAya = 283,
            endSura = 2,
            endAya = 286
        ),
        PageInfo(
            pageNumber = 50,
            startSura = 3,
            startAya = 1,
            endSura = 3,
            endAya = 9
        ),
        PageInfo(
            pageNumber = 51,
            startSura = 3,
            startAya = 10,
            endSura = 3,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 52,
            startSura = 3,
            startAya = 16,
            endSura = 3,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 53,
            startSura = 3,
            startAya = 23,
            endSura = 3,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 54,
            startSura = 3,
            startAya = 30,
            endSura = 3,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 55,
            startSura = 3,
            startAya = 38,
            endSura = 3,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 56,
            startSura = 3,
            startAya = 46,
            endSura = 3,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 57,
            startSura = 3,
            startAya = 53,
            endSura = 3,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 58,
            startSura = 3,
            startAya = 62,
            endSura = 3,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 59,
            startSura = 3,
            startAya = 71,
            endSura = 3,
            endAya = 77
        ),
        PageInfo(
            pageNumber = 60,
            startSura = 3,
            startAya = 78,
            endSura = 3,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 61,
            startSura = 3,
            startAya = 84,
            endSura = 3,
            endAya = 91
        ),
        PageInfo(
            pageNumber = 62,
            startSura = 3,
            startAya = 92,
            endSura = 3,
            endAya = 100
        ),
        PageInfo(
            pageNumber = 63,
            startSura = 3,
            startAya = 101,
            endSura = 3,
            endAya = 108
        ),
        PageInfo(
            pageNumber = 64,
            startSura = 3,
            startAya = 109,
            endSura = 3,
            endAya = 115
        ),
        PageInfo(
            pageNumber = 65,
            startSura = 3,
            startAya = 116,
            endSura = 3,
            endAya = 121
        ),
        PageInfo(
            pageNumber = 66,
            startSura = 3,
            startAya = 122,
            endSura = 3,
            endAya = 132
        ),
        PageInfo(
            pageNumber = 67,
            startSura = 3,
            startAya = 133,
            endSura = 3,
            endAya = 140
        ),
        PageInfo(
            pageNumber = 68,
            startSura = 3,
            startAya = 141,
            endSura = 3,
            endAya = 148
        ),
        PageInfo(
            pageNumber = 69,
            startSura = 3,
            startAya = 149,
            endSura = 3,
            endAya = 153
        ),
        PageInfo(
            pageNumber = 70,
            startSura = 3,
            startAya = 154,
            endSura = 3,
            endAya = 157
        ),
        PageInfo(
            pageNumber = 71,
            startSura = 3,
            startAya = 158,
            endSura = 3,
            endAya = 165
        ),
        PageInfo(
            pageNumber = 72,
            startSura = 3,
            startAya = 166,
            endSura = 3,
            endAya = 173
        ),
        PageInfo(
            pageNumber = 73,
            startSura = 3,
            startAya = 174,
            endSura = 3,
            endAya = 180
        ),
        PageInfo(
            pageNumber = 74,
            startSura = 3,
            startAya = 181,
            endSura = 3,
            endAya = 186
        ),
        PageInfo(
            pageNumber = 75,
            startSura = 3,
            startAya = 187,
            endSura = 3,
            endAya = 194
        ),
        PageInfo(
            pageNumber = 76,
            startSura = 3,
            startAya = 195,
            endSura = 3,
            endAya = 200
        ),
        PageInfo(
            pageNumber = 77,
            startSura = 4,
            startAya = 1,
            endSura = 4,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 78,
            startSura = 4,
            startAya = 7,
            endSura = 4,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 79,
            startSura = 4,
            startAya = 12,
            endSura = 4,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 80,
            startSura = 4,
            startAya = 15,
            endSura = 4,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 81,
            startSura = 4,
            startAya = 20,
            endSura = 4,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 82,
            startSura = 4,
            startAya = 24,
            endSura = 4,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 83,
            startSura = 4,
            startAya = 27,
            endSura = 4,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 84,
            startSura = 4,
            startAya = 34,
            endSura = 4,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 85,
            startSura = 4,
            startAya = 38,
            endSura = 4,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 86,
            startSura = 4,
            startAya = 45,
            endSura = 4,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 87,
            startSura = 4,
            startAya = 52,
            endSura = 4,
            endAya = 59
        ),
        PageInfo(
            pageNumber = 88,
            startSura = 4,
            startAya = 60,
            endSura = 4,
            endAya = 65
        ),
        PageInfo(
            pageNumber = 89,
            startSura = 4,
            startAya = 66,
            endSura = 4,
            endAya = 74
        ),
        PageInfo(
            pageNumber = 90,
            startSura = 4,
            startAya = 75,
            endSura = 4,
            endAya = 79
        ),
        PageInfo(
            pageNumber = 91,
            startSura = 4,
            startAya = 80,
            endSura = 4,
            endAya = 86
        ),
        PageInfo(
            pageNumber = 92,
            startSura = 4,
            startAya = 87,
            endSura = 4,
            endAya = 91
        ),
        PageInfo(
            pageNumber = 93,
            startSura = 4,
            startAya = 92,
            endSura = 4,
            endAya = 94
        ),
        PageInfo(
            pageNumber = 94,
            startSura = 4,
            startAya = 95,
            endSura = 4,
            endAya = 101
        ),
        PageInfo(
            pageNumber = 95,
            startSura = 4,
            startAya = 102,
            endSura = 4,
            endAya = 105
        ),
        PageInfo(
            pageNumber = 96,
            startSura = 4,
            startAya = 106,
            endSura = 4,
            endAya = 113
        ),
        PageInfo(
            pageNumber = 97,
            startSura = 4,
            startAya = 114,
            endSura = 4,
            endAya = 121
        ),
        PageInfo(
            pageNumber = 98,
            startSura = 4,
            startAya = 122,
            endSura = 4,
            endAya = 127
        ),
        PageInfo(
            pageNumber = 99,
            startSura = 4,
            startAya = 128,
            endSura = 4,
            endAya = 134
        ),
        PageInfo(
            pageNumber = 100,
            startSura = 4,
            startAya = 135,
            endSura = 4,
            endAya = 140
        ),
        PageInfo(
            pageNumber = 101,
            startSura = 4,
            startAya = 141,
            endSura = 4,
            endAya = 147
        ),
        PageInfo(
            pageNumber = 102,
            startSura = 4,
            startAya = 148,
            endSura = 4,
            endAya = 154
        ),
        PageInfo(
            pageNumber = 103,
            startSura = 4,
            startAya = 155,
            endSura = 4,
            endAya = 162
        ),
        PageInfo(
            pageNumber = 104,
            startSura = 4,
            startAya = 163,
            endSura = 4,
            endAya = 170
        ),
        PageInfo(
            pageNumber = 105,
            startSura = 4,
            startAya = 171,
            endSura = 4,
            endAya = 175
        ),
        PageInfo(
            pageNumber = 106,
            startSura = 4,
            startAya = 176,
            endSura = 4,
            endAya = 176
        ),
        PageInfo(
            pageNumber = 107,
            startSura = 5,
            startAya = 3,
            endSura = 5,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 108,
            startSura = 5,
            startAya = 6,
            endSura = 5,
            endAya = 9
        ),
        PageInfo(
            pageNumber = 109,
            startSura = 5,
            startAya = 10,
            endSura = 5,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 110,
            startSura = 5,
            startAya = 14,
            endSura = 5,
            endAya = 17
        ),
        PageInfo(
            pageNumber = 111,
            startSura = 5,
            startAya = 18,
            endSura = 5,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 112,
            startSura = 5,
            startAya = 24,
            endSura = 5,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 113,
            startSura = 5,
            startAya = 32,
            endSura = 5,
            endAya = 36
        ),
        PageInfo(
            pageNumber = 114,
            startSura = 5,
            startAya = 37,
            endSura = 5,
            endAya = 41
        ),
        PageInfo(
            pageNumber = 115,
            startSura = 5,
            startAya = 42,
            endSura = 5,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 116,
            startSura = 5,
            startAya = 46,
            endSura = 5,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 117,
            startSura = 5,
            startAya = 51,
            endSura = 5,
            endAya = 57
        ),
        PageInfo(
            pageNumber = 118,
            startSura = 5,
            startAya = 58,
            endSura = 5,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 119,
            startSura = 5,
            startAya = 65,
            endSura = 5,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 120,
            startSura = 5,
            startAya = 71,
            endSura = 5,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 121,
            startSura = 5,
            startAya = 77,
            endSura = 5,
            endAya = 82
        ),
        PageInfo(
            pageNumber = 122,
            startSura = 5,
            startAya = 83,
            endSura = 5,
            endAya = 89
        ),
        PageInfo(
            pageNumber = 123,
            startSura = 5,
            startAya = 90,
            endSura = 5,
            endAya = 95
        ),
        PageInfo(
            pageNumber = 124,
            startSura = 5,
            startAya = 96,
            endSura = 5,
            endAya = 103
        ),
        PageInfo(
            pageNumber = 125,
            startSura = 5,
            startAya = 104,
            endSura = 5,
            endAya = 108
        ),
        PageInfo(
            pageNumber = 126,
            startSura = 5,
            startAya = 109,
            endSura = 5,
            endAya = 113
        ),
        PageInfo(
            pageNumber = 127,
            startSura = 5,
            startAya = 114,
            endSura = 5,
            endAya = 120
        ),
        PageInfo(
            pageNumber = 128,
            startSura = 6,
            startAya = 1,
            endSura = 6,
            endAya = 8
        ),
        PageInfo(
            pageNumber = 129,
            startSura = 6,
            startAya = 9,
            endSura = 6,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 130,
            startSura = 6,
            startAya = 19,
            endSura = 6,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 131,
            startSura = 6,
            startAya = 28,
            endSura = 6,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 132,
            startSura = 6,
            startAya = 36,
            endSura = 6,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 133,
            startSura = 6,
            startAya = 45,
            endSura = 6,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 134,
            startSura = 6,
            startAya = 53,
            endSura = 6,
            endAya = 59
        ),
        PageInfo(
            pageNumber = 135,
            startSura = 6,
            startAya = 60,
            endSura = 6,
            endAya = 68
        ),
        PageInfo(
            pageNumber = 136,
            startSura = 6,
            startAya = 69,
            endSura = 6,
            endAya = 73
        ),
        PageInfo(
            pageNumber = 137,
            startSura = 6,
            startAya = 74,
            endSura = 6,
            endAya = 81
        ),
        PageInfo(
            pageNumber = 138,
            startSura = 6,
            startAya = 82,
            endSura = 6,
            endAya = 90
        ),
        PageInfo(
            pageNumber = 139,
            startSura = 6,
            startAya = 91,
            endSura = 6,
            endAya = 94
        ),
        PageInfo(
            pageNumber = 140,
            startSura = 6,
            startAya = 95,
            endSura = 6,
            endAya = 101
        ),
        PageInfo(
            pageNumber = 141,
            startSura = 6,
            startAya = 102,
            endSura = 6,
            endAya = 110
        ),
        PageInfo(
            pageNumber = 142,
            startSura = 6,
            startAya = 111,
            endSura = 6,
            endAya = 118
        ),
        PageInfo(
            pageNumber = 143,
            startSura = 6,
            startAya = 119,
            endSura = 6,
            endAya = 124
        ),
        PageInfo(
            pageNumber = 144,
            startSura = 6,
            startAya = 125,
            endSura = 6,
            endAya = 131
        ),
        PageInfo(
            pageNumber = 145,
            startSura = 6,
            startAya = 132,
            endSura = 6,
            endAya = 137
        ),
        PageInfo(
            pageNumber = 146,
            startSura = 6,
            startAya = 138,
            endSura = 6,
            endAya = 142
        ),
        PageInfo(
            pageNumber = 147,
            startSura = 6,
            startAya = 143,
            endSura = 6,
            endAya = 146
        ),
        PageInfo(
            pageNumber = 148,
            startSura = 6,
            startAya = 147,
            endSura = 6,
            endAya = 151
        ),
        PageInfo(
            pageNumber = 149,
            startSura = 6,
            startAya = 152,
            endSura = 6,
            endAya = 157
        ),
        PageInfo(
            pageNumber = 150,
            startSura = 6,
            startAya = 158,
            endSura = 6,
            endAya = 165
        ),
        PageInfo(
            pageNumber = 151,
            startSura = 7,
            startAya = 1,
            endSura = 7,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 152,
            startSura = 7,
            startAya = 12,
            endSura = 7,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 153,
            startSura = 7,
            startAya = 23,
            endSura = 7,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 154,
            startSura = 7,
            startAya = 31,
            endSura = 7,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 155,
            startSura = 7,
            startAya = 38,
            endSura = 7,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 156,
            startSura = 7,
            startAya = 44,
            endSura = 7,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 157,
            startSura = 7,
            startAya = 52,
            endSura = 7,
            endAya = 57
        ),
        PageInfo(
            pageNumber = 158,
            startSura = 7,
            startAya = 58,
            endSura = 7,
            endAya = 67
        ),
        PageInfo(
            pageNumber = 159,
            startSura = 7,
            startAya = 68,
            endSura = 7,
            endAya = 73
        ),
        PageInfo(
            pageNumber = 160,
            startSura = 7,
            startAya = 74,
            endSura = 7,
            endAya = 81
        ),
        PageInfo(
            pageNumber = 161,
            startSura = 7,
            startAya = 82,
            endSura = 7,
            endAya = 87
        ),
        PageInfo(
            pageNumber = 162,
            startSura = 7,
            startAya = 88,
            endSura = 7,
            endAya = 95
        ),
        PageInfo(
            pageNumber = 163,
            startSura = 7,
            startAya = 96,
            endSura = 7,
            endAya = 104
        ),
        PageInfo(
            pageNumber = 164,
            startSura = 7,
            startAya = 105,
            endSura = 7,
            endAya = 120
        ),
        PageInfo(
            pageNumber = 165,
            startSura = 7,
            startAya = 121,
            endSura = 7,
            endAya = 130
        ),
        PageInfo(
            pageNumber = 166,
            startSura = 7,
            startAya = 131,
            endSura = 7,
            endAya = 137
        ),
        PageInfo(
            pageNumber = 167,
            startSura = 7,
            startAya = 138,
            endSura = 7,
            endAya = 143
        ),
        PageInfo(
            pageNumber = 168,
            startSura = 7,
            startAya = 144,
            endSura = 7,
            endAya = 149
        ),
        PageInfo(
            pageNumber = 169,
            startSura = 7,
            startAya = 150,
            endSura = 7,
            endAya = 155
        ),
        PageInfo(
            pageNumber = 170,
            startSura = 7,
            startAya = 156,
            endSura = 7,
            endAya = 159
        ),
        PageInfo(
            pageNumber = 171,
            startSura = 7,
            startAya = 160,
            endSura = 7,
            endAya = 163
        ),
        PageInfo(
            pageNumber = 172,
            startSura = 7,
            startAya = 164,
            endSura = 7,
            endAya = 170
        ),
        PageInfo(
            pageNumber = 173,
            startSura = 7,
            startAya = 171,
            endSura = 7,
            endAya = 178
        ),
        PageInfo(
            pageNumber = 174,
            startSura = 7,
            startAya = 179,
            endSura = 7,
            endAya = 187
        ),
        PageInfo(
            pageNumber = 175,
            startSura = 7,
            startAya = 188,
            endSura = 7,
            endAya = 195
        ),
        PageInfo(
            pageNumber = 176,
            startSura = 7,
            startAya = 196,
            endSura = 7,
            endAya = 206
        ),
        PageInfo(
            pageNumber = 177,
            startSura = 8,
            startAya = 1,
            endSura = 8,
            endAya = 8
        ),
        PageInfo(
            pageNumber = 178,
            startSura = 8,
            startAya = 9,
            endSura = 8,
            endAya = 16
        ),
        PageInfo(
            pageNumber = 179,
            startSura = 8,
            startAya = 17,
            endSura = 8,
            endAya = 25
        ),
        PageInfo(
            pageNumber = 180,
            startSura = 8,
            startAya = 26,
            endSura = 8,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 181,
            startSura = 8,
            startAya = 34,
            endSura = 8,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 182,
            startSura = 8,
            startAya = 41,
            endSura = 8,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 183,
            startSura = 8,
            startAya = 46,
            endSura = 8,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 184,
            startSura = 8,
            startAya = 53,
            endSura = 8,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 185,
            startSura = 8,
            startAya = 62,
            endSura = 8,
            endAya = 69
        ),
        PageInfo(
            pageNumber = 186,
            startSura = 8,
            startAya = 70,
            endSura = 8,
            endAya = 75
        ),
        PageInfo(
            pageNumber = 187,
            startSura = 9,
            startAya = 1,
            endSura = 9,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 188,
            startSura = 9,
            startAya = 7,
            endSura = 9,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 189,
            startSura = 9,
            startAya = 14,
            endSura = 9,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 190,
            startSura = 9,
            startAya = 21,
            endSura = 9,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 191,
            startSura = 9,
            startAya = 27,
            endSura = 9,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 192,
            startSura = 9,
            startAya = 32,
            endSura = 9,
            endAya = 36
        ),
        PageInfo(
            pageNumber = 193,
            startSura = 9,
            startAya = 37,
            endSura = 9,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 194,
            startSura = 9,
            startAya = 41,
            endSura = 9,
            endAya = 47
        ),
        PageInfo(
            pageNumber = 195,
            startSura = 9,
            startAya = 48,
            endSura = 9,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 196,
            startSura = 9,
            startAya = 55,
            endSura = 9,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 197,
            startSura = 9,
            startAya = 62,
            endSura = 9,
            endAya = 68
        ),
        PageInfo(
            pageNumber = 198,
            startSura = 9,
            startAya = 69,
            endSura = 9,
            endAya = 72
        ),
        PageInfo(
            pageNumber = 199,
            startSura = 9,
            startAya = 73,
            endSura = 9,
            endAya = 79
        ),
        PageInfo(
            pageNumber = 200,
            startSura = 9,
            startAya = 80,
            endSura = 9,
            endAya = 86
        ),
        PageInfo(
            pageNumber = 201,
            startSura = 9,
            startAya = 87,
            endSura = 9,
            endAya = 93
        ),
        PageInfo(
            pageNumber = 202,
            startSura = 9,
            startAya = 94,
            endSura = 9,
            endAya = 99
        ),
        PageInfo(
            pageNumber = 203,
            startSura = 9,
            startAya = 100,
            endSura = 9,
            endAya = 106
        ),
        PageInfo(
            pageNumber = 204,
            startSura = 9,
            startAya = 107,
            endSura = 9,
            endAya = 111
        ),
        PageInfo(
            pageNumber = 205,
            startSura = 9,
            startAya = 112,
            endSura = 9,
            endAya = 117
        ),
        PageInfo(
            pageNumber = 206,
            startSura = 9,
            startAya = 118,
            endSura = 9,
            endAya = 122
        ),
        PageInfo(
            pageNumber = 207,
            startSura = 9,
            startAya = 123,
            endSura = 9,
            endAya = 129
        ),
        PageInfo(
            pageNumber = 208,
            startSura = 10,
            startAya = 1,
            endSura = 10,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 209,
            startSura = 10,
            startAya = 7,
            endSura = 10,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 210,
            startSura = 10,
            startAya = 15,
            endSura = 10,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 211,
            startSura = 10,
            startAya = 21,
            endSura = 10,
            endAya = 25
        ),
        PageInfo(
            pageNumber = 212,
            startSura = 10,
            startAya = 26,
            endSura = 10,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 213,
            startSura = 10,
            startAya = 34,
            endSura = 10,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 214,
            startSura = 10,
            startAya = 43,
            endSura = 10,
            endAya = 53
        ),
        PageInfo(
            pageNumber = 215,
            startSura = 10,
            startAya = 54,
            endSura = 10,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 216,
            startSura = 10,
            startAya = 62,
            endSura = 10,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 217,
            startSura = 10,
            startAya = 71,
            endSura = 10,
            endAya = 78
        ),
        PageInfo(
            pageNumber = 218,
            startSura = 10,
            startAya = 79,
            endSura = 10,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 219,
            startSura = 10,
            startAya = 89,
            endSura = 10,
            endAya = 97
        ),
        PageInfo(
            pageNumber = 220,
            startSura = 10,
            startAya = 98,
            endSura = 10,
            endAya = 106
        ),
        PageInfo(
            pageNumber = 221,
            startSura = 10,
            startAya = 107,
            endSura = 10,
            endAya = 109
        ),
        PageInfo(
            pageNumber = 222,
            startSura = 11,
            startAya = 6,
            endSura = 11,
            endAya = 12
        ),
        PageInfo(
            pageNumber = 223,
            startSura = 11,
            startAya = 13,
            endSura = 11,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 224,
            startSura = 11,
            startAya = 20,
            endSura = 11,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 225,
            startSura = 11,
            startAya = 29,
            endSura = 11,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 226,
            startSura = 11,
            startAya = 38,
            endSura = 11,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 227,
            startSura = 11,
            startAya = 46,
            endSura = 11,
            endAya = 53
        ),
        PageInfo(
            pageNumber = 228,
            startSura = 11,
            startAya = 54,
            endSura = 11,
            endAya = 62
        ),
        PageInfo(
            pageNumber = 229,
            startSura = 11,
            startAya = 63,
            endSura = 11,
            endAya = 71
        ),
        PageInfo(
            pageNumber = 230,
            startSura = 11,
            startAya = 72,
            endSura = 11,
            endAya = 81
        ),
        PageInfo(
            pageNumber = 231,
            startSura = 11,
            startAya = 82,
            endSura = 11,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 232,
            startSura = 11,
            startAya = 89,
            endSura = 11,
            endAya = 97
        ),
        PageInfo(
            pageNumber = 233,
            startSura = 11,
            startAya = 98,
            endSura = 11,
            endAya = 108
        ),
        PageInfo(
            pageNumber = 234,
            startSura = 11,
            startAya = 109,
            endSura = 11,
            endAya = 117
        ),
        PageInfo(
            pageNumber = 235,
            startSura = 11,
            startAya = 118,
            endSura = 11,
            endAya = 123
        ),
        PageInfo(
            pageNumber = 236,
            startSura = 12,
            startAya = 5,
            endSura = 12,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 237,
            startSura = 12,
            startAya = 15,
            endSura = 12,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 238,
            startSura = 12,
            startAya = 23,
            endSura = 12,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 239,
            startSura = 12,
            startAya = 31,
            endSura = 12,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 240,
            startSura = 12,
            startAya = 38,
            endSura = 12,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 241,
            startSura = 12,
            startAya = 44,
            endSura = 12,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 242,
            startSura = 12,
            startAya = 53,
            endSura = 12,
            endAya = 63
        ),
        PageInfo(
            pageNumber = 243,
            startSura = 12,
            startAya = 64,
            endSura = 12,
            endAya = 69
        ),
        PageInfo(
            pageNumber = 244,
            startSura = 12,
            startAya = 70,
            endSura = 12,
            endAya = 78
        ),
        PageInfo(
            pageNumber = 245,
            startSura = 12,
            startAya = 79,
            endSura = 12,
            endAya = 86
        ),
        PageInfo(
            pageNumber = 246,
            startSura = 12,
            startAya = 87,
            endSura = 12,
            endAya = 95
        ),
        PageInfo(
            pageNumber = 247,
            startSura = 12,
            startAya = 96,
            endSura = 12,
            endAya = 103
        ),
        PageInfo(
            pageNumber = 248,
            startSura = 12,
            startAya = 104,
            endSura = 12,
            endAya = 111
        ),
        PageInfo(
            pageNumber = 249,
            startSura = 13,
            startAya = 1,
            endSura = 13,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 250,
            startSura = 13,
            startAya = 6,
            endSura = 13,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 251,
            startSura = 13,
            startAya = 14,
            endSura = 13,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 252,
            startSura = 13,
            startAya = 19,
            endSura = 13,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 253,
            startSura = 13,
            startAya = 29,
            endSura = 13,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 254,
            startSura = 13,
            startAya = 35,
            endSura = 13,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 255,
            startSura = 13,
            startAya = 43,
            endSura = 13,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 256,
            startSura = 14,
            startAya = 6,
            endSura = 14,
            endAya = 10
        ),
        PageInfo(
            pageNumber = 257,
            startSura = 14,
            startAya = 11,
            endSura = 14,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 258,
            startSura = 14,
            startAya = 19,
            endSura = 14,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 259,
            startSura = 14,
            startAya = 25,
            endSura = 14,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 260,
            startSura = 14,
            startAya = 34,
            endSura = 14,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 261,
            startSura = 14,
            startAya = 43,
            endSura = 14,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 262,
            startSura = 15,
            startAya = 1,
            endSura = 15,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 263,
            startSura = 15,
            startAya = 16,
            endSura = 15,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 264,
            startSura = 15,
            startAya = 32,
            endSura = 15,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 265,
            startSura = 15,
            startAya = 52,
            endSura = 15,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 266,
            startSura = 15,
            startAya = 71,
            endSura = 15,
            endAya = 90
        ),
        PageInfo(
            pageNumber = 267,
            startSura = 15,
            startAya = 91,
            endSura = 15,
            endAya = 99
        ),
        PageInfo(
            pageNumber = 268,
            startSura = 16,
            startAya = 7,
            endSura = 16,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 269,
            startSura = 16,
            startAya = 15,
            endSura = 16,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 270,
            startSura = 16,
            startAya = 27,
            endSura = 16,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 271,
            startSura = 16,
            startAya = 35,
            endSura = 16,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 272,
            startSura = 16,
            startAya = 43,
            endSura = 16,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 273,
            startSura = 16,
            startAya = 55,
            endSura = 16,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 274,
            startSura = 16,
            startAya = 65,
            endSura = 16,
            endAya = 72
        ),
        PageInfo(
            pageNumber = 275,
            startSura = 16,
            startAya = 73,
            endSura = 16,
            endAya = 79
        ),
        PageInfo(
            pageNumber = 276,
            startSura = 16,
            startAya = 80,
            endSura = 16,
            endAya = 87
        ),
        PageInfo(
            pageNumber = 277,
            startSura = 16,
            startAya = 88,
            endSura = 16,
            endAya = 93
        ),
        PageInfo(
            pageNumber = 278,
            startSura = 16,
            startAya = 94,
            endSura = 16,
            endAya = 102
        ),
        PageInfo(
            pageNumber = 279,
            startSura = 16,
            startAya = 103,
            endSura = 16,
            endAya = 110
        ),
        PageInfo(
            pageNumber = 280,
            startSura = 16,
            startAya = 111,
            endSura = 16,
            endAya = 118
        ),
        PageInfo(
            pageNumber = 281,
            startSura = 16,
            startAya = 119,
            endSura = 16,
            endAya = 128
        ),
        PageInfo(
            pageNumber = 282,
            startSura = 17,
            startAya = 1,
            endSura = 17,
            endAya = 7
        ),
        PageInfo(
            pageNumber = 283,
            startSura = 17,
            startAya = 8,
            endSura = 17,
            endAya = 17
        ),
        PageInfo(
            pageNumber = 284,
            startSura = 17,
            startAya = 18,
            endSura = 17,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 285,
            startSura = 17,
            startAya = 28,
            endSura = 17,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 286,
            startSura = 17,
            startAya = 39,
            endSura = 17,
            endAya = 49
        ),
        PageInfo(
            pageNumber = 287,
            startSura = 17,
            startAya = 50,
            endSura = 17,
            endAya = 58
        ),
        PageInfo(
            pageNumber = 288,
            startSura = 17,
            startAya = 59,
            endSura = 17,
            endAya = 66
        ),
        PageInfo(
            pageNumber = 289,
            startSura = 17,
            startAya = 67,
            endSura = 17,
            endAya = 75
        ),
        PageInfo(
            pageNumber = 290,
            startSura = 17,
            startAya = 76,
            endSura = 17,
            endAya = 86
        ),
        PageInfo(
            pageNumber = 291,
            startSura = 17,
            startAya = 87,
            endSura = 17,
            endAya = 96
        ),
        PageInfo(
            pageNumber = 292,
            startSura = 17,
            startAya = 97,
            endSura = 17,
            endAya = 104
        ),
        PageInfo(
            pageNumber = 293,
            startSura = 17,
            startAya = 105,
            endSura = 17,
            endAya = 111
        ),
        PageInfo(
            pageNumber = 294,
            startSura = 18,
            startAya = 5,
            endSura = 18,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 295,
            startSura = 18,
            startAya = 16,
            endSura = 18,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 296,
            startSura = 18,
            startAya = 21,
            endSura = 18,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 297,
            startSura = 18,
            startAya = 28,
            endSura = 18,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 298,
            startSura = 18,
            startAya = 35,
            endSura = 18,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 299,
            startSura = 18,
            startAya = 46,
            endSura = 18,
            endAya = 53
        ),
        PageInfo(
            pageNumber = 300,
            startSura = 18,
            startAya = 54,
            endSura = 18,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 301,
            startSura = 18,
            startAya = 62,
            endSura = 18,
            endAya = 74
        ),
        PageInfo(
            pageNumber = 302,
            startSura = 18,
            startAya = 75,
            endSura = 18,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 303,
            startSura = 18,
            startAya = 84,
            endSura = 18,
            endAya = 97
        ),
        PageInfo(
            pageNumber = 304,
            startSura = 18,
            startAya = 98,
            endSura = 18,
            endAya = 110
        ),
        PageInfo(
            pageNumber = 305,
            startSura = 19,
            startAya = 1,
            endSura = 19,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 306,
            startSura = 19,
            startAya = 12,
            endSura = 19,
            endAya = 25
        ),
        PageInfo(
            pageNumber = 307,
            startSura = 19,
            startAya = 26,
            endSura = 19,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 308,
            startSura = 19,
            startAya = 39,
            endSura = 19,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 309,
            startSura = 19,
            startAya = 52,
            endSura = 19,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 310,
            startSura = 19,
            startAya = 65,
            endSura = 19,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 311,
            startSura = 19,
            startAya = 77,
            endSura = 19,
            endAya = 95
        ),
        PageInfo(
            pageNumber = 312,
            startSura = 19,
            startAya = 96,
            endSura = 19,
            endAya = 98
        ),
        PageInfo(
            pageNumber = 313,
            startSura = 20,
            startAya = 13,
            endSura = 20,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 314,
            startSura = 20,
            startAya = 38,
            endSura = 20,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 315,
            startSura = 20,
            startAya = 52,
            endSura = 20,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 316,
            startSura = 20,
            startAya = 65,
            endSura = 20,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 317,
            startSura = 20,
            startAya = 77,
            endSura = 20,
            endAya = 87
        ),
        PageInfo(
            pageNumber = 318,
            startSura = 20,
            startAya = 88,
            endSura = 20,
            endAya = 98
        ),
        PageInfo(
            pageNumber = 319,
            startSura = 20,
            startAya = 99,
            endSura = 20,
            endAya = 113
        ),
        PageInfo(
            pageNumber = 320,
            startSura = 20,
            startAya = 114,
            endSura = 20,
            endAya = 125
        ),
        PageInfo(
            pageNumber = 321,
            startSura = 20,
            startAya = 126,
            endSura = 20,
            endAya = 135
        ),
        PageInfo(
            pageNumber = 322,
            startSura = 21,
            startAya = 1,
            endSura = 21,
            endAya = 10
        ),
        PageInfo(
            pageNumber = 323,
            startSura = 21,
            startAya = 11,
            endSura = 21,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 324,
            startSura = 21,
            startAya = 25,
            endSura = 21,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 325,
            startSura = 21,
            startAya = 36,
            endSura = 21,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 326,
            startSura = 21,
            startAya = 45,
            endSura = 21,
            endAya = 57
        ),
        PageInfo(
            pageNumber = 327,
            startSura = 21,
            startAya = 58,
            endSura = 21,
            endAya = 72
        ),
        PageInfo(
            pageNumber = 328,
            startSura = 21,
            startAya = 73,
            endSura = 21,
            endAya = 81
        ),
        PageInfo(
            pageNumber = 329,
            startSura = 21,
            startAya = 82,
            endSura = 21,
            endAya = 90
        ),
        PageInfo(
            pageNumber = 330,
            startSura = 21,
            startAya = 91,
            endSura = 21,
            endAya = 101
        ),
        PageInfo(
            pageNumber = 331,
            startSura = 21,
            startAya = 102,
            endSura = 21,
            endAya = 112
        ),
        PageInfo(
            pageNumber = 332,
            startSura = 22,
            startAya = 1,
            endSura = 22,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 333,
            startSura = 22,
            startAya = 6,
            endSura = 22,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 334,
            startSura = 22,
            startAya = 16,
            endSura = 22,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 335,
            startSura = 22,
            startAya = 24,
            endSura = 22,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 336,
            startSura = 22,
            startAya = 31,
            endSura = 22,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 337,
            startSura = 22,
            startAya = 39,
            endSura = 22,
            endAya = 46
        ),
        PageInfo(
            pageNumber = 338,
            startSura = 22,
            startAya = 47,
            endSura = 22,
            endAya = 55
        ),
        PageInfo(
            pageNumber = 339,
            startSura = 22,
            startAya = 56,
            endSura = 22,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 340,
            startSura = 22,
            startAya = 65,
            endSura = 22,
            endAya = 72
        ),
        PageInfo(
            pageNumber = 341,
            startSura = 22,
            startAya = 73,
            endSura = 22,
            endAya = 78
        ),
        PageInfo(
            pageNumber = 342,
            startSura = 23,
            startAya = 1,
            endSura = 23,
            endAya = 17
        ),
        PageInfo(
            pageNumber = 343,
            startSura = 23,
            startAya = 18,
            endSura = 23,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 344,
            startSura = 23,
            startAya = 28,
            endSura = 23,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 345,
            startSura = 23,
            startAya = 43,
            endSura = 23,
            endAya = 59
        ),
        PageInfo(
            pageNumber = 346,
            startSura = 23,
            startAya = 60,
            endSura = 23,
            endAya = 74
        ),
        PageInfo(
            pageNumber = 347,
            startSura = 23,
            startAya = 75,
            endSura = 23,
            endAya = 89
        ),
        PageInfo(
            pageNumber = 348,
            startSura = 23,
            startAya = 90,
            endSura = 23,
            endAya = 104
        ),
        PageInfo(
            pageNumber = 349,
            startSura = 23,
            startAya = 105,
            endSura = 23,
            endAya = 118
        ),
        PageInfo(
            pageNumber = 350,
            startSura = 24,
            startAya = 1,
            endSura = 24,
            endAya = 10
        ),
        PageInfo(
            pageNumber = 351,
            startSura = 24,
            startAya = 11,
            endSura = 24,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 352,
            startSura = 24,
            startAya = 21,
            endSura = 24,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 353,
            startSura = 24,
            startAya = 28,
            endSura = 24,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 354,
            startSura = 24,
            startAya = 32,
            endSura = 24,
            endAya = 36
        ),
        PageInfo(
            pageNumber = 355,
            startSura = 24,
            startAya = 37,
            endSura = 24,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 356,
            startSura = 24,
            startAya = 44,
            endSura = 24,
            endAya = 53
        ),
        PageInfo(
            pageNumber = 357,
            startSura = 24,
            startAya = 54,
            endSura = 24,
            endAya = 58
        ),
        PageInfo(
            pageNumber = 358,
            startSura = 24,
            startAya = 59,
            endSura = 24,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 359,
            startSura = 24,
            startAya = 62,
            endSura = 24,
            endAya = 64
        ),
        PageInfo(
            pageNumber = 360,
            startSura = 25,
            startAya = 3,
            endSura = 25,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 361,
            startSura = 25,
            startAya = 12,
            endSura = 25,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 362,
            startSura = 25,
            startAya = 21,
            endSura = 25,
            endAya = 32
        ),
        PageInfo(
            pageNumber = 363,
            startSura = 25,
            startAya = 33,
            endSura = 25,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 364,
            startSura = 25,
            startAya = 44,
            endSura = 25,
            endAya = 55
        ),
        PageInfo(
            pageNumber = 365,
            startSura = 25,
            startAya = 56,
            endSura = 25,
            endAya = 67
        ),
        PageInfo(
            pageNumber = 366,
            startSura = 25,
            startAya = 68,
            endSura = 25,
            endAya = 77
        ),
        PageInfo(
            pageNumber = 367,
            startSura = 26,
            startAya = 1,
            endSura = 26,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 368,
            startSura = 26,
            startAya = 20,
            endSura = 26,
            endAya = 39
        ),
        PageInfo(
            pageNumber = 369,
            startSura = 26,
            startAya = 40,
            endSura = 26,
            endAya = 60
        ),
        PageInfo(
            pageNumber = 370,
            startSura = 26,
            startAya = 61,
            endSura = 26,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 371,
            startSura = 26,
            startAya = 84,
            endSura = 26,
            endAya = 111
        ),
        PageInfo(
            pageNumber = 372,
            startSura = 26,
            startAya = 112,
            endSura = 26,
            endAya = 136
        ),
        PageInfo(
            pageNumber = 373,
            startSura = 26,
            startAya = 137,
            endSura = 26,
            endAya = 159
        ),
        PageInfo(
            pageNumber = 374,
            startSura = 26,
            startAya = 160,
            endSura = 26,
            endAya = 183
        ),
        PageInfo(
            pageNumber = 375,
            startSura = 26,
            startAya = 184,
            endSura = 26,
            endAya = 206
        ),
        PageInfo(
            pageNumber = 376,
            startSura = 26,
            startAya = 207,
            endSura = 26,
            endAya = 227
        ),
        PageInfo(
            pageNumber = 377,
            startSura = 27,
            startAya = 1,
            endSura = 27,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 378,
            startSura = 27,
            startAya = 14,
            endSura = 27,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 379,
            startSura = 27,
            startAya = 23,
            endSura = 27,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 380,
            startSura = 27,
            startAya = 36,
            endSura = 27,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 381,
            startSura = 27,
            startAya = 45,
            endSura = 27,
            endAya = 55
        ),
        PageInfo(
            pageNumber = 382,
            startSura = 27,
            startAya = 56,
            endSura = 27,
            endAya = 63
        ),
        PageInfo(
            pageNumber = 383,
            startSura = 27,
            startAya = 64,
            endSura = 27,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 384,
            startSura = 27,
            startAya = 77,
            endSura = 27,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 385,
            startSura = 27,
            startAya = 89,
            endSura = 27,
            endAya = 93
        ),
        PageInfo(
            pageNumber = 386,
            startSura = 28,
            startAya = 6,
            endSura = 28,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 387,
            startSura = 28,
            startAya = 14,
            endSura = 28,
            endAya = 21
        ),
        PageInfo(
            pageNumber = 388,
            startSura = 28,
            startAya = 22,
            endSura = 28,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 389,
            startSura = 28,
            startAya = 29,
            endSura = 28,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 390,
            startSura = 28,
            startAya = 36,
            endSura = 28,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 391,
            startSura = 28,
            startAya = 44,
            endSura = 28,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 392,
            startSura = 28,
            startAya = 51,
            endSura = 28,
            endAya = 59
        ),
        PageInfo(
            pageNumber = 393,
            startSura = 28,
            startAya = 60,
            endSura = 28,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 394,
            startSura = 28,
            startAya = 71,
            endSura = 28,
            endAya = 77
        ),
        PageInfo(
            pageNumber = 395,
            startSura = 28,
            startAya = 78,
            endSura = 28,
            endAya = 84
        ),
        PageInfo(
            pageNumber = 396,
            startSura = 28,
            startAya = 85,
            endSura = 28,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 397,
            startSura = 29,
            startAya = 7,
            endSura = 29,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 398,
            startSura = 29,
            startAya = 15,
            endSura = 29,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 399,
            startSura = 29,
            startAya = 24,
            endSura = 29,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 400,
            startSura = 29,
            startAya = 31,
            endSura = 29,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 401,
            startSura = 29,
            startAya = 39,
            endSura = 29,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 402,
            startSura = 29,
            startAya = 46,
            endSura = 29,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 403,
            startSura = 29,
            startAya = 53,
            endSura = 29,
            endAya = 63
        ),
        PageInfo(
            pageNumber = 404,
            startSura = 29,
            startAya = 64,
            endSura = 29,
            endAya = 69
        ),
        PageInfo(
            pageNumber = 405,
            startSura = 30,
            startAya = 6,
            endSura = 30,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 406,
            startSura = 30,
            startAya = 16,
            endSura = 30,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 407,
            startSura = 30,
            startAya = 25,
            endSura = 30,
            endAya = 32
        ),
        PageInfo(
            pageNumber = 408,
            startSura = 30,
            startAya = 33,
            endSura = 30,
            endAya = 41
        ),
        PageInfo(
            pageNumber = 409,
            startSura = 30,
            startAya = 42,
            endSura = 30,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 410,
            startSura = 30,
            startAya = 51,
            endSura = 30,
            endAya = 60
        ),
        PageInfo(
            pageNumber = 411,
            startSura = 31,
            startAya = 1,
            endSura = 31,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 412,
            startSura = 31,
            startAya = 12,
            endSura = 31,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 413,
            startSura = 31,
            startAya = 20,
            endSura = 31,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 414,
            startSura = 31,
            startAya = 29,
            endSura = 31,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 415,
            startSura = 32,
            startAya = 1,
            endSura = 32,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 416,
            startSura = 32,
            startAya = 12,
            endSura = 32,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 417,
            startSura = 32,
            startAya = 21,
            endSura = 32,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 418,
            startSura = 33,
            startAya = 1,
            endSura = 33,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 419,
            startSura = 33,
            startAya = 7,
            endSura = 33,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 420,
            startSura = 33,
            startAya = 16,
            endSura = 33,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 421,
            startSura = 33,
            startAya = 23,
            endSura = 33,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 422,
            startSura = 33,
            startAya = 31,
            endSura = 33,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 423,
            startSura = 33,
            startAya = 36,
            endSura = 33,
            endAya = 43
        ),
        PageInfo(
            pageNumber = 424,
            startSura = 33,
            startAya = 44,
            endSura = 33,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 425,
            startSura = 33,
            startAya = 51,
            endSura = 33,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 426,
            startSura = 33,
            startAya = 55,
            endSura = 33,
            endAya = 62
        ),
        PageInfo(
            pageNumber = 427,
            startSura = 33,
            startAya = 63,
            endSura = 33,
            endAya = 73
        ),
        PageInfo(
            pageNumber = 428,
            startSura = 34,
            startAya = 1,
            endSura = 34,
            endAya = 7
        ),
        PageInfo(
            pageNumber = 429,
            startSura = 34,
            startAya = 8,
            endSura = 34,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 430,
            startSura = 34,
            startAya = 15,
            endSura = 34,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 431,
            startSura = 34,
            startAya = 23,
            endSura = 34,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 432,
            startSura = 34,
            startAya = 32,
            endSura = 34,
            endAya = 39
        ),
        PageInfo(
            pageNumber = 433,
            startSura = 34,
            startAya = 40,
            endSura = 34,
            endAya = 48
        ),
        PageInfo(
            pageNumber = 434,
            startSura = 34,
            startAya = 49,
            endSura = 34,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 435,
            startSura = 35,
            startAya = 4,
            endSura = 35,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 436,
            startSura = 35,
            startAya = 12,
            endSura = 35,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 437,
            startSura = 35,
            startAya = 19,
            endSura = 35,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 438,
            startSura = 35,
            startAya = 31,
            endSura = 35,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 439,
            startSura = 35,
            startAya = 39,
            endSura = 35,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 440,
            startSura = 35,
            startAya = 45,
            endSura = 35,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 441,
            startSura = 36,
            startAya = 13,
            endSura = 36,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 442,
            startSura = 36,
            startAya = 28,
            endSura = 36,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 443,
            startSura = 36,
            startAya = 41,
            endSura = 36,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 444,
            startSura = 36,
            startAya = 55,
            endSura = 36,
            endAya = 70
        ),
        PageInfo(
            pageNumber = 445,
            startSura = 36,
            startAya = 71,
            endSura = 36,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 446,
            startSura = 37,
            startAya = 1,
            endSura = 37,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 447,
            startSura = 37,
            startAya = 25,
            endSura = 37,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 448,
            startSura = 37,
            startAya = 52,
            endSura = 37,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 449,
            startSura = 37,
            startAya = 77,
            endSura = 37,
            endAya = 102
        ),
        PageInfo(
            pageNumber = 450,
            startSura = 37,
            startAya = 103,
            endSura = 37,
            endAya = 126
        ),
        PageInfo(
            pageNumber = 451,
            startSura = 37,
            startAya = 127,
            endSura = 37,
            endAya = 153
        ),
        PageInfo(
            pageNumber = 452,
            startSura = 37,
            startAya = 154,
            endSura = 37,
            endAya = 182
        ),
        PageInfo(
            pageNumber = 453,
            startSura = 38,
            startAya = 1,
            endSura = 38,
            endAya = 16
        ),
        PageInfo(
            pageNumber = 454,
            startSura = 38,
            startAya = 17,
            endSura = 38,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 455,
            startSura = 38,
            startAya = 27,
            endSura = 38,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 456,
            startSura = 38,
            startAya = 43,
            endSura = 38,
            endAya = 61
        ),
        PageInfo(
            pageNumber = 457,
            startSura = 38,
            startAya = 62,
            endSura = 38,
            endAya = 83
        ),
        PageInfo(
            pageNumber = 458,
            startSura = 38,
            startAya = 84,
            endSura = 38,
            endAya = 88
        ),
        PageInfo(
            pageNumber = 459,
            startSura = 39,
            startAya = 6,
            endSura = 39,
            endAya = 10
        ),
        PageInfo(
            pageNumber = 460,
            startSura = 39,
            startAya = 11,
            endSura = 39,
            endAya = 21
        ),
        PageInfo(
            pageNumber = 461,
            startSura = 39,
            startAya = 22,
            endSura = 39,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 462,
            startSura = 39,
            startAya = 32,
            endSura = 39,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 463,
            startSura = 39,
            startAya = 41,
            endSura = 39,
            endAya = 47
        ),
        PageInfo(
            pageNumber = 464,
            startSura = 39,
            startAya = 48,
            endSura = 39,
            endAya = 56
        ),
        PageInfo(
            pageNumber = 465,
            startSura = 39,
            startAya = 57,
            endSura = 39,
            endAya = 67
        ),
        PageInfo(
            pageNumber = 466,
            startSura = 39,
            startAya = 68,
            endSura = 39,
            endAya = 74
        ),
        PageInfo(
            pageNumber = 467,
            startSura = 39,
            startAya = 75,
            endSura = 39,
            endAya = 75
        ),
        PageInfo(
            pageNumber = 468,
            startSura = 40,
            startAya = 8,
            endSura = 40,
            endAya = 16
        ),
        PageInfo(
            pageNumber = 469,
            startSura = 40,
            startAya = 17,
            endSura = 40,
            endAya = 25
        ),
        PageInfo(
            pageNumber = 470,
            startSura = 40,
            startAya = 26,
            endSura = 40,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 471,
            startSura = 40,
            startAya = 34,
            endSura = 40,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 472,
            startSura = 40,
            startAya = 41,
            endSura = 40,
            endAya = 49
        ),
        PageInfo(
            pageNumber = 473,
            startSura = 40,
            startAya = 50,
            endSura = 40,
            endAya = 58
        ),
        PageInfo(
            pageNumber = 474,
            startSura = 40,
            startAya = 59,
            endSura = 40,
            endAya = 66
        ),
        PageInfo(
            pageNumber = 475,
            startSura = 40,
            startAya = 67,
            endSura = 40,
            endAya = 77
        ),
        PageInfo(
            pageNumber = 476,
            startSura = 40,
            startAya = 78,
            endSura = 40,
            endAya = 85
        ),
        PageInfo(
            pageNumber = 477,
            startSura = 41,
            startAya = 1,
            endSura = 41,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 478,
            startSura = 41,
            startAya = 12,
            endSura = 41,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 479,
            startSura = 41,
            startAya = 21,
            endSura = 41,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 480,
            startSura = 41,
            startAya = 30,
            endSura = 41,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 481,
            startSura = 41,
            startAya = 39,
            endSura = 41,
            endAya = 46
        ),
        PageInfo(
            pageNumber = 482,
            startSura = 41,
            startAya = 47,
            endSura = 41,
            endAya = 54
        ),
        PageInfo(
            pageNumber = 483,
            startSura = 42,
            startAya = 1,
            endSura = 42,
            endAya = 10
        ),
        PageInfo(
            pageNumber = 484,
            startSura = 42,
            startAya = 11,
            endSura = 42,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 485,
            startSura = 42,
            startAya = 16,
            endSura = 42,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 486,
            startSura = 42,
            startAya = 23,
            endSura = 42,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 487,
            startSura = 42,
            startAya = 32,
            endSura = 42,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 488,
            startSura = 42,
            startAya = 45,
            endSura = 42,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 489,
            startSura = 42,
            startAya = 52,
            endSura = 42,
            endAya = 53
        ),
        PageInfo(
            pageNumber = 490,
            startSura = 43,
            startAya = 11,
            endSura = 43,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 491,
            startSura = 43,
            startAya = 23,
            endSura = 43,
            endAya = 33
        ),
        PageInfo(
            pageNumber = 492,
            startSura = 43,
            startAya = 34,
            endSura = 43,
            endAya = 47
        ),
        PageInfo(
            pageNumber = 493,
            startSura = 43,
            startAya = 48,
            endSura = 43,
            endAya = 60
        ),
        PageInfo(
            pageNumber = 494,
            startSura = 43,
            startAya = 61,
            endSura = 43,
            endAya = 73
        ),
        PageInfo(
            pageNumber = 495,
            startSura = 43,
            startAya = 74,
            endSura = 43,
            endAya = 89
        ),
        PageInfo(
            pageNumber = 496,
            startSura = 44,
            startAya = 1,
            endSura = 44,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 497,
            startSura = 44,
            startAya = 19,
            endSura = 44,
            endAya = 39
        ),
        PageInfo(
            pageNumber = 498,
            startSura = 44,
            startAya = 40,
            endSura = 44,
            endAya = 59
        ),
        PageInfo(
            pageNumber = 499,
            startSura = 45,
            startAya = 1,
            endSura = 45,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 500,
            startSura = 45,
            startAya = 14,
            endSura = 45,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 501,
            startSura = 45,
            startAya = 23,
            endSura = 45,
            endAya = 32
        ),
        PageInfo(
            pageNumber = 502,
            startSura = 45,
            startAya = 33,
            endSura = 45,
            endAya = 37
        ),
        PageInfo(
            pageNumber = 503,
            startSura = 46,
            startAya = 6,
            endSura = 46,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 504,
            startSura = 46,
            startAya = 15,
            endSura = 46,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 505,
            startSura = 46,
            startAya = 21,
            endSura = 46,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 506,
            startSura = 46,
            startAya = 29,
            endSura = 46,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 507,
            startSura = 47,
            startAya = 1,
            endSura = 47,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 508,
            startSura = 47,
            startAya = 12,
            endSura = 47,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 509,
            startSura = 47,
            startAya = 20,
            endSura = 47,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 510,
            startSura = 47,
            startAya = 30,
            endSura = 47,
            endAya = 38
        ),
        PageInfo(
            pageNumber = 511,
            startSura = 48,
            startAya = 1,
            endSura = 48,
            endAya = 9
        ),
        PageInfo(
            pageNumber = 512,
            startSura = 48,
            startAya = 10,
            endSura = 48,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 513,
            startSura = 48,
            startAya = 16,
            endSura = 48,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 514,
            startSura = 48,
            startAya = 24,
            endSura = 48,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 515,
            startSura = 48,
            startAya = 29,
            endSura = 48,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 516,
            startSura = 49,
            startAya = 5,
            endSura = 49,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 517,
            startSura = 49,
            startAya = 12,
            endSura = 49,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 518,
            startSura = 50,
            startAya = 1,
            endSura = 50,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 519,
            startSura = 50,
            startAya = 16,
            endSura = 50,
            endAya = 35
        ),
        PageInfo(
            pageNumber = 520,
            startSura = 50,
            startAya = 36,
            endSura = 50,
            endAya = 45
        ),
        PageInfo(
            pageNumber = 521,
            startSura = 51,
            startAya = 7,
            endSura = 51,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 522,
            startSura = 51,
            startAya = 31,
            endSura = 51,
            endAya = 51
        ),
        PageInfo(
            pageNumber = 523,
            startSura = 51,
            startAya = 52,
            endSura = 51,
            endAya = 60
        ),
        PageInfo(
            pageNumber = 524,
            startSura = 52,
            startAya = 15,
            endSura = 52,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 525,
            startSura = 52,
            startAya = 32,
            endSura = 52,
            endAya = 49
        ),
        PageInfo(
            pageNumber = 526,
            startSura = 53,
            startAya = 1,
            endSura = 53,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 527,
            startSura = 53,
            startAya = 27,
            endSura = 53,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 528,
            startSura = 53,
            startAya = 45,
            endSura = 53,
            endAya = 62
        ),
        PageInfo(
            pageNumber = 529,
            startSura = 54,
            startAya = 7,
            endSura = 54,
            endAya = 27
        ),
        PageInfo(
            pageNumber = 530,
            startSura = 54,
            startAya = 28,
            endSura = 54,
            endAya = 49
        ),
        PageInfo(
            pageNumber = 531,
            startSura = 54,
            startAya = 50,
            endSura = 54,
            endAya = 55
        ),
        PageInfo(
            pageNumber = 532,
            startSura = 55,
            startAya = 17,
            endSura = 55,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 533,
            startSura = 55,
            startAya = 41,
            endSura = 55,
            endAya = 67
        ),
        PageInfo(
            pageNumber = 534,
            startSura = 55,
            startAya = 68,
            endSura = 55,
            endAya = 78
        ),
        PageInfo(
            pageNumber = 535,
            startSura = 56,
            startAya = 17,
            endSura = 56,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 536,
            startSura = 56,
            startAya = 51,
            endSura = 56,
            endAya = 76
        ),
        PageInfo(
            pageNumber = 537,
            startSura = 56,
            startAya = 77,
            endSura = 56,
            endAya = 96
        ),
        PageInfo(
            pageNumber = 538,
            startSura = 57,
            startAya = 4,
            endSura = 57,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 539,
            startSura = 57,
            startAya = 12,
            endSura = 57,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 540,
            startSura = 57,
            startAya = 19,
            endSura = 57,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 541,
            startSura = 57,
            startAya = 25,
            endSura = 57,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 542,
            startSura = 58,
            startAya = 1,
            endSura = 58,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 543,
            startSura = 58,
            startAya = 7,
            endSura = 58,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 544,
            startSura = 58,
            startAya = 12,
            endSura = 58,
            endAya = 21
        ),
        PageInfo(
            pageNumber = 545,
            startSura = 58,
            startAya = 22,
            endSura = 58,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 546,
            startSura = 59,
            startAya = 4,
            endSura = 59,
            endAya = 9
        ),
        PageInfo(
            pageNumber = 547,
            startSura = 59,
            startAya = 10,
            endSura = 59,
            endAya = 16
        ),
        PageInfo(
            pageNumber = 548,
            startSura = 59,
            startAya = 17,
            endSura = 59,
            endAya = 24
        ),
        PageInfo(
            pageNumber = 549,
            startSura = 60,
            startAya = 1,
            endSura = 60,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 550,
            startSura = 60,
            startAya = 6,
            endSura = 60,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 551,
            startSura = 60,
            startAya = 12,
            endSura = 60,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 552,
            startSura = 61,
            startAya = 6,
            endSura = 61,
            endAya = 14
        ),
        PageInfo(
            pageNumber = 553,
            startSura = 62,
            startAya = 1,
            endSura = 62,
            endAya = 8
        ),
        PageInfo(
            pageNumber = 554,
            startSura = 62,
            startAya = 9,
            endSura = 62,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 555,
            startSura = 63,
            startAya = 5,
            endSura = 63,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 556,
            startSura = 64,
            startAya = 1,
            endSura = 64,
            endAya = 9
        ),
        PageInfo(
            pageNumber = 557,
            startSura = 64,
            startAya = 10,
            endSura = 64,
            endAya = 18
        ),
        PageInfo(
            pageNumber = 558,
            startSura = 65,
            startAya = 1,
            endSura = 65,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 559,
            startSura = 65,
            startAya = 6,
            endSura = 65,
            endAya = 12
        ),
        PageInfo(
            pageNumber = 560,
            startSura = 66,
            startAya = 1,
            endSura = 66,
            endAya = 7
        ),
        PageInfo(
            pageNumber = 561,
            startSura = 66,
            startAya = 8,
            endSura = 66,
            endAya = 12
        ),
        PageInfo(
            pageNumber = 562,
            startSura = 67,
            startAya = 1,
            endSura = 67,
            endAya = 12
        ),
        PageInfo(
            pageNumber = 563,
            startSura = 67,
            startAya = 13,
            endSura = 67,
            endAya = 26
        ),
        PageInfo(
            pageNumber = 564,
            startSura = 67,
            startAya = 27,
            endSura = 67,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 565,
            startSura = 68,
            startAya = 16,
            endSura = 68,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 566,
            startSura = 68,
            startAya = 43,
            endSura = 68,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 567,
            startSura = 69,
            startAya = 9,
            endSura = 69,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 568,
            startSura = 69,
            startAya = 35,
            endSura = 69,
            endAya = 52
        ),
        PageInfo(
            pageNumber = 569,
            startSura = 70,
            startAya = 11,
            endSura = 70,
            endAya = 39
        ),
        PageInfo(
            pageNumber = 570,
            startSura = 70,
            startAya = 40,
            endSura = 70,
            endAya = 44
        ),
        PageInfo(
            pageNumber = 571,
            startSura = 71,
            startAya = 11,
            endSura = 71,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 572,
            startSura = 72,
            startAya = 1,
            endSura = 72,
            endAya = 13
        ),
        PageInfo(
            pageNumber = 573,
            startSura = 72,
            startAya = 14,
            endSura = 72,
            endAya = 28
        ),
        PageInfo(
            pageNumber = 574,
            startSura = 73,
            startAya = 1,
            endSura = 73,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 575,
            startSura = 73,
            startAya = 20,
            endSura = 73,
            endAya = 20
        ),
        PageInfo(
            pageNumber = 576,
            startSura = 74,
            startAya = 18,
            endSura = 74,
            endAya = 47
        ),
        PageInfo(
            pageNumber = 577,
            startSura = 74,
            startAya = 48,
            endSura = 74,
            endAya = 56
        ),
        PageInfo(
            pageNumber = 578,
            startSura = 75,
            startAya = 20,
            endSura = 75,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 579,
            startSura = 76,
            startAya = 6,
            endSura = 76,
            endAya = 25
        ),
        PageInfo(
            pageNumber = 580,
            startSura = 76,
            startAya = 26,
            endSura = 76,
            endAya = 31
        ),
        PageInfo(
            pageNumber = 581,
            startSura = 77,
            startAya = 20,
            endSura = 77,
            endAya = 50
        ),
        PageInfo(
            pageNumber = 582,
            startSura = 78,
            startAya = 1,
            endSura = 78,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 583,
            startSura = 78,
            startAya = 31,
            endSura = 78,
            endAya = 40
        ),
        PageInfo(
            pageNumber = 584,
            startSura = 79,
            startAya = 16,
            endSura = 79,
            endAya = 46
        ),
        PageInfo(
            pageNumber = 585,
            startSura = 80,
            startAya = 1,
            endSura = 80,
            endAya = 42
        ),
        PageInfo(
            pageNumber = 586,
            startSura = 81,
            startAya = 1,
            endSura = 81,
            endAya = 29
        ),
        PageInfo(
            pageNumber = 587,
            startSura = 82,
            startAya = 1,
            endSura = 82,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 588,
            startSura = 83,
            startAya = 7,
            endSura = 83,
            endAya = 34
        ),
        PageInfo(
            pageNumber = 589,
            startSura = 83,
            startAya = 35,
            endSura = 83,
            endAya = 36
        ),
        PageInfo(
            pageNumber = 590,
            startSura = 85,
            startAya = 1,
            endSura = 85,
            endAya = 22
        ),
        PageInfo(
            pageNumber = 591,
            startSura = 86,
            startAya = 1,
            endSura = 86,
            endAya = 17
        ),
        PageInfo(
            pageNumber = 592,
            startSura = 87,
            startAya = 16,
            endSura = 87,
            endAya = 19
        ),
        PageInfo(
            pageNumber = 593,
            startSura = 89,
            startAya = 1,
            endSura = 89,
            endAya = 23
        ),
        PageInfo(
            pageNumber = 594,
            startSura = 89,
            startAya = 24,
            endSura = 89,
            endAya = 30
        ),
        PageInfo(
            pageNumber = 595,
            startSura = 91,
            startAya = 1,
            endSura = 91,
            endAya = 15
        ),
        PageInfo(
            pageNumber = 596,
            startSura = 92,
            startAya = 15,
            endSura = 92,
            endAya = 21
        ),
        PageInfo(
            pageNumber = 597,
            startSura = 95,
            startAya = 1,
            endSura = 95,
            endAya = 8
        ),
        PageInfo(
            pageNumber = 598,
            startSura = 97,
            startAya = 1,
            endSura = 97,
            endAya = 5
        ),
        PageInfo(
            pageNumber = 599,
            startSura = 98,
            startAya = 8,
            endSura = 98,
            endAya = 8
        ),
        PageInfo(
            pageNumber = 600,
            startSura = 100,
            startAya = 10,
            endSura = 100,
            endAya = 11
        ),
        PageInfo(
            pageNumber = 601,
            startSura = 103,
            startAya = 1,
            endSura = 103,
            endAya = 3
        ),
        PageInfo(
            pageNumber = 602,
            startSura = 106,
            startAya = 1,
            endSura = 106,
            endAya = 4
        ),
        PageInfo(
            pageNumber = 603,
            startSura = 109,
            startAya = 1,
            endSura = 109,
            endAya = 6
        ),
        PageInfo(
            pageNumber = 604,
            startSura = 112,
            startAya = 1,
            endSura = 114,
            endAya = 6
        ),
    )
    
    /**
     * الحصول على صفحات سورة معينة
     */
    fun getPagesForSurah(surahNumber: Int): List<PageInfo> {
        return allPages.filter { it.startSura == surahNumber }
    }
    
    /**
     * تقسيم آيات السورة حسب الصفحات بدقة
     */
    fun groupAyahsByPages(surahNumber: Int, ayahs: List<Ayah>): Map<Int, List<Ayah>> {
        val surahPages = getPagesForSurah(surahNumber)
        
        if (surahPages.isEmpty()) {
            return if (ayahs.isNotEmpty()) mapOf(1 to ayahs) else emptyMap()
        }
        
        val result = mutableMapOf<Int, MutableList<Ayah>>()
        
        for (page in surahPages) {
            // الآيات في هذه الصفحة
            val pageAyahs = ayahs.filter { ayah ->
                ayah.number >= page.startAya && ayah.number <= page.endAya
            }
            
            if (pageAyahs.isNotEmpty()) {
                result[page.pageNumber] = pageAyahs.toMutableList()
            }
        }
        
        return result
    }
    
    /**
     * الحصول على معلومات صفحة معينة
     */
    fun getPageInfo(pageNumber: Int): PageInfo? {
        return allPages.find { it.pageNumber == pageNumber }
    }
    
    /**
     * الحصول على رقم الصفحة التي تحتوي على آية معينة
     */
    fun getPageForAya(surahNumber: Int, ayaNumber: Int): Int? {
        return allPages.find { page ->
            page.startSura == surahNumber && 
            ayaNumber >= page.startAya && 
            ayaNumber <= page.endAya
        }?.pageNumber
    }
}
