package awab.quran.ar.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import awab.quran.ar.data.ThemeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

data class Surah(
    val number: Int,
    val name: String,
    val translatedName: String,
    val verses: Int,
    val revelationType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecitation: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSurahClick: (Surah) -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = androidx.compose.ui.platform.LocalContext.current
    val themeRepo = remember { ThemeRepository(context) }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var favoriteSurahs by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showDonationDialog by remember { mutableStateOf(false) }

    // آخر 10 سور تم فتحها
    val prefs = remember { context.getSharedPreferences("recent_surahs", android.content.Context.MODE_PRIVATE) }
    var recentSurahs by remember {
        val saved = prefs.getString("recent", "") ?: ""
        val list = if (saved.isEmpty()) emptyList()
                   else saved.split(",").mapNotNull { it.trim().toIntOrNull() }
        mutableStateOf(list)
    }

    fun addToRecent(surahNumber: Int) {
        val updated = (listOf(surahNumber) + recentSurahs.filter { it != surahNumber }).take(10)
        recentSurahs = updated
        prefs.edit().putString("recent", updated.joinToString(",")).apply()
    }

    // جلب بيانات المستخدم والمفضلة
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: ""
                    @Suppress("UNCHECKED_CAST")
                    val favList = document.get("favoriteSurahs") as? List<Long> ?: emptyList()
                    favoriteSurahs = favList.map { it.toInt() }.toSet()
                }
        }
    }

    // دالة تبديل المفضلة
    fun toggleFavorite(surahNumber: Int) {
        val userId = auth.currentUser?.uid ?: return
        val newFavorites = if (surahNumber in favoriteSurahs) {
            favoriteSurahs - surahNumber
        } else {
            favoriteSurahs + surahNumber
        }
        favoriteSurahs = newFavorites
        firestore.collection("users").document(userId)
            .update("favoriteSurahs", newFavorites.toList())
            .addOnFailureListener {
                // إذا لم يكن الحقل موجوداً، أنشئه
                firestore.collection("users").document(userId)
                    .set(mapOf("favoriteSurahs" to newFavorites.toList()), com.google.firebase.firestore.SetOptions.merge())
            }
    }

    // قائمة السور الكاملة (114 سورة)
    val surahs = remember {
        listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية"),
            Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية"),
            Surah(3, "آل عمران", "Ali 'Imran", 200, "مدنية"),
            Surah(4, "النساء", "An-Nisa", 176, "مدنية"),
            Surah(5, "المائدة", "Al-Ma'idah", 120, "مدنية"),
            Surah(6, "الأنعام", "Al-An'am", 165, "مكية"),
            Surah(7, "الأعراف", "Al-A'raf", 206, "مكية"),
            Surah(8, "الأنفال", "Al-Anfal", 75, "مدنية"),
            Surah(9, "التوبة", "At-Tawbah", 129, "مدنية"),
            Surah(10, "يونس", "Yunus", 109, "مكية"),
            Surah(11, "هود", "Hud", 123, "مكية"),
            Surah(12, "يوسف", "Yusuf", 111, "مكية"),
            Surah(13, "الرعد", "Ar-Ra'd", 43, "مدنية"),
            Surah(14, "إبراهيم", "Ibrahim", 52, "مكية"),
            Surah(15, "الحجر", "Al-Hijr", 99, "مكية"),
            Surah(16, "النحل", "An-Nahl", 128, "مكية"),
            Surah(17, "الإسراء", "Al-Isra", 111, "مكية"),
            Surah(18, "الكهف", "Al-Kahf", 110, "مكية"),
            Surah(19, "مريم", "Maryam", 98, "مكية"),
            Surah(20, "طه", "Taha", 135, "مكية"),
            Surah(21, "الأنبياء", "Al-Anbya", 112, "مكية"),
            Surah(22, "الحج", "Al-Hajj", 78, "مدنية"),
            Surah(23, "المؤمنون", "Al-Mu'minun", 118, "مكية"),
            Surah(24, "النور", "An-Nur", 64, "مدنية"),
            Surah(25, "الفرقان", "Al-Furqan", 77, "مكية"),
            Surah(26, "الشعراء", "Ash-Shu'ara", 227, "مكية"),
            Surah(27, "النمل", "An-Naml", 93, "مكية"),
            Surah(28, "القصص", "Al-Qasas", 88, "مكية"),
            Surah(29, "العنكبوت", "Al-'Ankabut", 69, "مكية"),
            Surah(30, "الروم", "Ar-Rum", 60, "مكية"),
            Surah(31, "لقمان", "Luqman", 34, "مكية"),
            Surah(32, "السجدة", "As-Sajdah", 30, "مكية"),
            Surah(33, "الأحزاب", "Al-Ahzab", 73, "مدنية"),
            Surah(34, "سبأ", "Saba", 54, "مكية"),
            Surah(35, "فاطر", "Fatir", 45, "مكية"),
            Surah(36, "يس", "Ya-Sin", 83, "مكية"),
            Surah(37, "الصافات", "As-Saffat", 182, "مكية"),
            Surah(38, "ص", "Sad", 88, "مكية"),
            Surah(39, "الزمر", "Az-Zumar", 75, "مكية"),
            Surah(40, "غافر", "Ghafir", 85, "مكية"),
            Surah(41, "فصلت", "Fussilat", 54, "مكية"),
            Surah(42, "الشورى", "Ash-Shuraa", 53, "مكية"),
            Surah(43, "الزخرف", "Az-Zukhruf", 89, "مكية"),
            Surah(44, "الدخان", "Ad-Dukhan", 59, "مكية"),
            Surah(45, "الجاثية", "Al-Jathiyah", 37, "مكية"),
            Surah(46, "الأحقاف", "Al-Ahqaf", 35, "مكية"),
            Surah(47, "محمد", "Muhammad", 38, "مدنية"),
            Surah(48, "الفتح", "Al-Fath", 29, "مدنية"),
            Surah(49, "الحجرات", "Al-Hujurat", 18, "مدنية"),
            Surah(50, "ق", "Qaf", 45, "مكية"),
            Surah(51, "الذاريات", "Adh-Dhariyat", 60, "مكية"),
            Surah(52, "الطور", "At-Tur", 49, "مكية"),
            Surah(53, "النجم", "An-Najm", 62, "مكية"),
            Surah(54, "القمر", "Al-Qamar", 55, "مكية"),
            Surah(55, "الرحمن", "Ar-Rahman", 78, "مدنية"),
            Surah(56, "الواقعة", "Al-Waqi'ah", 96, "مكية"),
            Surah(57, "الحديد", "Al-Hadid", 29, "مدنية"),
            Surah(58, "المجادلة", "Al-Mujadila", 22, "مدنية"),
            Surah(59, "الحشر", "Al-Hashr", 24, "مدنية"),
            Surah(60, "الممتحنة", "Al-Mumtahanah", 13, "مدنية"),
            Surah(61, "الصف", "As-Saf", 14, "مدنية"),
            Surah(62, "الجمعة", "Al-Jumu'ah", 11, "مدنية"),
            Surah(63, "المنافقون", "Al-Munafiqun", 11, "مدنية"),
            Surah(64, "التغابن", "At-Taghabun", 18, "مدنية"),
            Surah(65, "الطلاق", "At-Talaq", 12, "مدنية"),
            Surah(66, "التحريم", "At-Tahrim", 12, "مدنية"),
            Surah(67, "الملك", "Al-Mulk", 30, "مكية"),
            Surah(68, "القلم", "Al-Qalam", 52, "مكية"),
            Surah(69, "الحاقة", "Al-Haqqah", 52, "مكية"),
            Surah(70, "المعارج", "Al-Ma'arij", 44, "مكية"),
            Surah(71, "نوح", "Nuh", 28, "مكية"),
            Surah(72, "الجن", "Al-Jinn", 28, "مكية"),
            Surah(73, "المزمل", "Al-Muzzammil", 20, "مكية"),
            Surah(74, "المدثر", "Al-Muddaththir", 56, "مكية"),
            Surah(75, "القيامة", "Al-Qiyamah", 40, "مكية"),
            Surah(76, "الإنسان", "Al-Insan", 31, "مدنية"),
            Surah(77, "المرسلات", "Al-Mursalat", 50, "مكية"),
            Surah(78, "النبأ", "An-Naba", 40, "مكية"),
            Surah(79, "النازعات", "An-Nazi'at", 46, "مكية"),
            Surah(80, "عبس", "'Abasa", 42, "مكية"),
            Surah(81, "التكوير", "At-Takwir", 29, "مكية"),
            Surah(82, "الإنفطار", "Al-Infitar", 19, "مكية"),
            Surah(83, "المطففين", "Al-Mutaffifin", 36, "مكية"),
            Surah(84, "الإنشقاق", "Al-Inshiqaq", 25, "مكية"),
            Surah(85, "البروج", "Al-Buruj", 22, "مكية"),
            Surah(86, "الطارق", "At-Tariq", 17, "مكية"),
            Surah(87, "الأعلى", "Al-A'la", 19, "مكية"),
            Surah(88, "الغاشية", "Al-Ghashiyah", 26, "مكية"),
            Surah(89, "الفجر", "Al-Fajr", 30, "مكية"),
            Surah(90, "البلد", "Al-Balad", 20, "مكية"),
            Surah(91, "الشمس", "Ash-Shams", 15, "مكية"),
            Surah(92, "الليل", "Al-Layl", 21, "مكية"),
            Surah(93, "الضحى", "Ad-Duhaa", 11, "مكية"),
            Surah(94, "الشرح", "Ash-Sharh", 8, "مكية"),
            Surah(95, "التين", "At-Tin", 8, "مكية"),
            Surah(96, "العلق", "Al-'Alaq", 19, "مكية"),
            Surah(97, "القدر", "Al-Qadr", 5, "مكية"),
            Surah(98, "البينة", "Al-Bayyinah", 8, "مدنية"),
            Surah(99, "الزلزلة", "Az-Zalzalah", 8, "مدنية"),
            Surah(100, "العاديات", "Al-'Adiyat", 11, "مكية"),
            Surah(101, "القارعة", "Al-Qari'ah", 11, "مكية"),
            Surah(102, "التكاثر", "At-Takathur", 8, "مكية"),
            Surah(103, "العصر", "Al-'Asr", 3, "مكية"),
            Surah(104, "الهمزة", "Al-Humazah", 9, "مكية"),
            Surah(105, "الفيل", "Al-Fil", 5, "مكية"),
            Surah(106, "قريش", "Quraysh", 4, "مكية"),
            Surah(107, "الماعون", "Al-Ma'un", 7, "مكية"),
            Surah(108, "الكوثر", "Al-Kawthar", 3, "مكية"),
            Surah(109, "الكافرون", "Al-Kafirun", 6, "مكية"),
            Surah(110, "النصر", "An-Nasr", 3, "مدنية"),
            Surah(111, "المسد", "Al-Masad", 5, "مكية"),
            Surah(112, "الإخلاص", "Al-Ikhlas", 4, "مكية"),
            Surah(113, "الفلق", "Al-Falaq", 5, "مكية"),
            Surah(114, "الناس", "An-Nas", 6, "مكية")
        )
    }

    // تصفية السور حسب البحث والتبويب
    val filteredSurahs = surahs.filter { surah ->
        val matchesSearch = searchQuery.isEmpty() ||
            surah.name.contains(searchQuery) ||
            surah.translatedName.contains(searchQuery, ignoreCase = true)

        val matchesTab = when (selectedTab) {
            "المفضلة" -> surah.number in favoriteSurahs
            "اخر قراءة" -> surah.number in recentSurahs
            else -> true
        }

        matchesSearch && matchesTab
    }.let { list ->
        // ترتيب "اخر قراءة" حسب آخر فتح
        if (selectedTab == "اخر قراءة") {
            list.sortedBy { recentSurahs.indexOf(it.number) }
        } else list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) Color(0xFF121212) else Color.Transparent
            )
    ) {
        // الخلفية - تظهر فقط في الوضع النهاري
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.home_background),
                contentDescription = "خلفية الصفحة الرئيسية",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // ألوان تتغير حسب الوضع
        val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF4A3F35)
        val subTextColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744)
        val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE8DDD0).copy(alpha = 0.65f)
        val tabBorderColor = if (isDarkMode) Color(0xFFD4AF37).copy(alpha = 0.5f) else Color(0xFFD4AF37).copy(alpha = 0.3f)
        val searchBg = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE8DDD0).copy(alpha = 0.5f)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // نديم في المنتصف
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkMode) Color(0xFF1E1E1E) else Color.Transparent)
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // أيقونة الملف الشخصي على اليسار
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "الملف الشخصي",
                            tint = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF6B5744)
                        )
                    }

                    // زر التبرع + زر الوضع الليلي على اليمين
                    Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                        // زر التبرع
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { showDonationDialog = true }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "تبرع",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(22.dp)
                            )
                            Text("تبرع", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }

                        // زر الوضع الليلي
                        IconButton(
                            onClick = {
                                val newValue = !isDarkMode
                                onToggleDarkMode(newValue)
                                scope.launch { themeRepo.setDarkMode(newValue) }
                            }
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightlightRound,
                                contentDescription = if (isDarkMode) "الوضع النهاري" else "الوضع الليلي",
                                tint = if (isDarkMode) Color(0xFFFFD700) else Color(0xFF6B5744)
                            )
                        }
                    }

                    // نديم وخير جليس في المنتصف
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "نديم",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "خير جليس لحفظ كتاب اللَّهِ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = subTextColor
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // التبويبات
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = tabBorderColor,
                                shape = RoundedCornerShape(25.dp)
                            )
                            .background(
                                if (isDarkMode) Color(0xFF1E1E1E) else Color.Transparent,
                                RoundedCornerShape(25.dp)
                            )
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TransparentTabButton(
                                text = "الكل",
                                isSelected = selectedTab == "الكل",
                                onClick = { selectedTab = "الكل" },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                            TransparentTabButton(
                                text = "المفضلة",
                                isSelected = selectedTab == "المفضلة",
                                onClick = { selectedTab = "المفضلة" },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                            TransparentTabButton(
                                text = "اخر قراءة",
                                isSelected = selectedTab == "اخر قراءة",
                                onClick = { selectedTab = "اخر قراءة" },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }

                // شريط البحث
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = searchBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = subTextColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "ابحث عن سورة",
                                        fontSize = 14.sp,
                                        color = subTextColor.copy(alpha = 0.5f)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    cursorColor = Color(0xFFD4AF37)
                                ),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Right
                                )
                            )
                        }
                    }
                }

                // قائمة السور
                items(filteredSurahs) { surah ->
                    GoldenSurahCard(
                        surah = surah,
                        isFavorite = surah.number in favoriteSurahs,
                        onFavoriteClick = { toggleFavorite(surah.number) },
                        onClick = {
                            addToRecent(surah.number)
                            onSurahClick(surah)
                        },
                        isDarkMode = isDarkMode
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // نافذة التبرع
    if (showDonationDialog) {
        val context2 = androidx.compose.ui.platform.LocalContext.current
        AlertDialog(
            onDismissRequest = { showDonationDialog = false },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFF8F0),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ادعم تطوير التطبيق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "نسعى لخدمة كتاب الله بأفضل صورة. تبرعك يساعدنا على تحسين التطبيق وتقديم تجربة أفضل لك ولكل من يتلو كتاب الله.",
                        fontSize = 14.sp,
                        color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "اختر طريقة التبرع:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val donationUrl = "https://zmmoly.github.io/Nadem/nadeem-website.html#contact"
                    val donationOptions = listOf(
                        Triple("5 ريال", donationUrl, Color(0xFF4CAF50)),
                        Triple("10 ريال", donationUrl, Color(0xFF2196F3)),
                        Triple("20 ريال", donationUrl, Color(0xFF9C27B0)),
                        Triple("مبلغ آخر", donationUrl, Color(0xFFE53935))
                    )
                    donationOptions.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { (label, url, color) ->
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context2.startActivity(intent)
                                        showDonationDialog = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDonationDialog = false }) {
                    Text("إغلاق", color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744))
                }
            }
        )
    }
}

@Composable
fun TransparentTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val unselectedTextColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF9B8B7A)
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC9A961).copy(alpha = 0.8f),
                                    Color(0xFFB8941E).copy(alpha = 0.9f),
                                    Color(0xFFC9A961).copy(alpha = 0.8f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE8D7A8),
                                    Color(0xFFD4AF37),
                                    Color(0xFFE8D7A8)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else unselectedTextColor
        )
    }
}

@Composable
fun GoldenSurahCard(
    surah: Surah,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val cardBg = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE8DDD0).copy(alpha = 0.65f)
    val nameColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF4A3F35)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF7A6B5A)
    val arrowColor = if (isDarkMode) Color(0xFF888888) else Color(0xFF9B8B7A)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // نجمة المفضلة في الزاوية العلوية اليسرى
            IconButton(
                onClick = { onFavoriteClick() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة للمفضلة",
                    tint = if (isFavorite) Color(0xFFD4AF37) else Color(0xFFBBAA99),
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // سهم التنقل (يمين)
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "فتح",
                    tint = arrowColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // اسم السورة ومعلوماتها
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = surah.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${surah.verses} آية", fontSize = 13.sp, color = subColor)
                        Text(text = " • ", fontSize = 13.sp, color = subColor)
                        Text(text = surah.revelationType, fontSize = 13.sp, color = subColor)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // دائرة ذهبية مع رقم السورة (يسار)
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // دوائر الزخرفة
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .border(
                                width = 0.5.dp,
                                color = Color(0xFFD4AF37).copy(alpha = if (isDarkMode) 0.4f else 0.2f),
                                shape = CircleShape
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .border(
                                width = 0.5.dp,
                                color = Color(0xFFD4AF37).copy(alpha = if (isDarkMode) 0.3f else 0.15f),
                                shape = CircleShape
                            )
                    )

                    // الدائرة الرئيسية
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(
                                    width = 3.dp,
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            Color(0xFFE8D7A8),
                                            Color(0xFFD4AF37),
                                            Color(0xFFC9A961),
                                            Color(0xFFB8941E),
                                            Color(0xFFA67C00),
                                            Color(0xFFB8941E),
                                            Color(0xFFC9A961),
                                            Color(0xFFD4AF37),
                                            Color(0xFFE8D7A8)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF3E342B),
                                            Color(0xFF2D2419),
                                            Color(0xFF1F1811)
                                        )
                                    )
                                )
                        )

                        Text(
                            text = surah.number.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                    }
                }
            }
        }
    }
}
