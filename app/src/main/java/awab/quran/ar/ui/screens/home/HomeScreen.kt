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
import androidx.compose.ui.res.stringResource
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
    val context = LocalContext.current
    val themeRepo = remember { ThemeRepository(context) }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var favoriteSurahs by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showDonationDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val tabAll = stringResource(R.string.all)
    val tabFavorites = stringResource(R.string.favorites)
    val tabRecent = stringResource(R.string.tab_recent)
    var selectedTab by remember { mutableStateOf(tabAll) }

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
                firestore.collection("users").document(userId)
                    .set(mapOf("favoriteSurahs" to newFavorites.toList()), com.google.firebase.firestore.SetOptions.merge())
            }
    }

    val surahs = remember {
        listOf(
            Surah(1, context.getString(R.string.surah_al_fatihah), "Al-Fatihah", 7, "مكية"),
            Surah(2, context.getString(R.string.surah_al_baqarah), "Al-Baqarah", 286, "مدنية"),
            Surah(3, context.getString(R.string.surah_ali_imran), "Ali 'Imran", 200, "مدنية"),
            Surah(4, context.getString(R.string.surah_an_nisa), "An-Nisa", 176, "مدنية"),
            Surah(5, context.getString(R.string.surah_al_maidah), "Al-Ma'idah", 120, "مدنية"),
            Surah(6, context.getString(R.string.surah_al_anam), "Al-An'am", 165, "مكية"),
            Surah(7, context.getString(R.string.surah_al_araf), "Al-A'raf", 206, "مكية"),
            Surah(8, context.getString(R.string.surah_al_anfal), "Al-Anfal", 75, "مدنية"),
            Surah(9, context.getString(R.string.surah_at_tawbah), "At-Tawbah", 129, "مدنية"),
            Surah(10, context.getString(R.string.surah_yunus), "Yunus", 109, "مكية"),
            Surah(11, context.getString(R.string.surah_hud), "Hud", 123, "مكية"),
            Surah(12, context.getString(R.string.surah_yusuf), "Yusuf", 111, "مكية"),
            Surah(13, context.getString(R.string.surah_ar_rad), "Ar-Ra'd", 43, "مدنية"),
            Surah(14, context.getString(R.string.surah_ibrahim), "Ibrahim", 52, "مكية"),
            Surah(15, context.getString(R.string.surah_al_hijr), "Al-Hijr", 99, "مكية"),
            Surah(16, context.getString(R.string.surah_an_nahl), "An-Nahl", 128, "مكية"),
            Surah(17, context.getString(R.string.surah_al_isra), "Al-Isra", 111, "مكية"),
            Surah(18, context.getString(R.string.surah_al_kahf), "Al-Kahf", 110, "مكية"),
            Surah(19, context.getString(R.string.surah_maryam), "Maryam", 98, "مكية"),
            Surah(20, context.getString(R.string.surah_ta_ha), "Taha", 135, "مكية"),
            Surah(21, context.getString(R.string.surah_al_anbiya), "Al-Anbya", 112, "مكية"),
            Surah(22, context.getString(R.string.surah_al_hajj), "Al-Hajj", 78, "مدنية"),
            Surah(23, context.getString(R.string.surah_al_muminun), "Al-Mu'minun", 118, "مكية"),
            Surah(24, context.getString(R.string.surah_an_nur), "An-Nur", 64, "مدنية"),
            Surah(25, context.getString(R.string.surah_al_furqan), "Al-Furqan", 77, "مكية"),
            Surah(26, context.getString(R.string.surah_ash_shuara), "Ash-Shu'ara", 227, "مكية"),
            Surah(27, context.getString(R.string.surah_an_naml), "An-Naml", 93, "مكية"),
            Surah(28, context.getString(R.string.surah_al_qasas), "Al-Qasas", 88, "مكية"),
            Surah(29, context.getString(R.string.surah_al_ankabut), "Al-'Ankabut", 69, "مكية"),
            Surah(30, context.getString(R.string.surah_ar_rum), "Ar-Rum", 60, "مكية"),
            Surah(31, context.getString(R.string.surah_luqman), "Luqman", 34, "مكية"),
            Surah(32, context.getString(R.string.surah_as_sajdah), "As-Sajdah", 30, "مكية"),
            Surah(33, context.getString(R.string.surah_al_ahzab), "Al-Ahzab", 73, "مدنية"),
            Surah(34, context.getString(R.string.surah_saba), "Saba", 54, "مكية"),
            Surah(35, context.getString(R.string.surah_fatir), "Fatir", 45, "مكية"),
            Surah(36, context.getString(R.string.surah_ya_sin), "Ya-Sin", 83, "مكية"),
            Surah(37, context.getString(R.string.surah_as_saffat), "As-Saffat", 182, "مكية"),
            Surah(38, context.getString(R.string.surah_sad), "Sad", 88, "مكية"),
            Surah(39, context.getString(R.string.surah_az_zumar), "Az-Zumar", 75, "مكية"),
            Surah(40, context.getString(R.string.surah_ghafir), "Ghafir", 85, "مكية"),
            Surah(41, context.getString(R.string.surah_fussilat), "Fussilat", 54, "مكية"),
            Surah(42, context.getString(R.string.surah_ash_shura), "Ash-Shuraa", 53, "مكية"),
            Surah(43, context.getString(R.string.surah_az_zukhruf), "Az-Zukhruf", 89, "مكية"),
            Surah(44, context.getString(R.string.surah_ad_dukhan), "Ad-Dukhan", 59, "مكية"),
            Surah(45, context.getString(R.string.surah_al_jathiyah), "Al-Jathiyah", 37, "مكية"),
            Surah(46, context.getString(R.string.surah_al_ahqaf), "Al-Ahqaf", 35, "مكية"),
            Surah(47, context.getString(R.string.surah_muhammad), "Muhammad", 38, "مدنية"),
            Surah(48, context.getString(R.string.surah_al_fath), "Al-Fath", 29, "مدنية"),
            Surah(49, context.getString(R.string.surah_al_hujurat), "Al-Hujurat", 18, "مدنية"),
            Surah(50, context.getString(R.string.surah_qaf), "Qaf", 45, "مكية"),
            Surah(51, context.getString(R.string.surah_adh_dhariyat), "Adh-Dhariyat", 60, "مكية"),
            Surah(52, context.getString(R.string.surah_at_tur), "At-Tur", 49, "مكية"),
            Surah(53, context.getString(R.string.surah_an_najm), "An-Najm", 62, "مكية"),
            Surah(54, context.getString(R.string.surah_al_qamar), "Al-Qamar", 55, "مكية"),
            Surah(55, context.getString(R.string.surah_ar_rahman), "Ar-Rahman", 78, "مدنية"),
            Surah(56, context.getString(R.string.surah_al_waqiah), "Al-Waqi'ah", 96, "مكية"),
            Surah(57, context.getString(R.string.surah_al_hadid), "Al-Hadid", 29, "مدنية"),
            Surah(58, context.getString(R.string.surah_al_mujadilah), "Al-Mujadila", 22, "مدنية"),
            Surah(59, context.getString(R.string.surah_al_hashr), "Al-Hashr", 24, "مدنية"),
            Surah(60, context.getString(R.string.surah_al_mumtahanah), "Al-Mumtahanah", 13, "مدنية"),
            Surah(61, context.getString(R.string.surah_as_saf), "As-Saf", 14, "مدنية"),
            Surah(62, context.getString(R.string.surah_al_jumuah), "Al-Jumu'ah", 11, "مدنية"),
            Surah(63, context.getString(R.string.surah_al_munafiqun), "Al-Munafiqun", 11, "مدنية"),
            Surah(64, context.getString(R.string.surah_at_taghabun), "At-Taghabun", 18, "مدنية"),
            Surah(65, context.getString(R.string.surah_at_talaq), "At-Talaq", 12, "مدنية"),
            Surah(66, context.getString(R.string.surah_at_tahrim), "At-Tahrim", 12, "مدنية"),
            Surah(67, context.getString(R.string.surah_al_mulk), "Al-Mulk", 30, "مكية"),
            Surah(68, context.getString(R.string.surah_al_qalam), "Al-Qalam", 52, "مكية"),
            Surah(69, context.getString(R.string.surah_al_haqqah), "Al-Haqqah", 52, "مكية"),
            Surah(70, context.getString(R.string.surah_al_maarij), "Al-Ma'arij", 44, "مكية"),
            Surah(71, context.getString(R.string.surah_nuh), "Nuh", 28, "مكية"),
            Surah(72, context.getString(R.string.surah_al_jinn), "Al-Jinn", 28, "مكية"),
            Surah(73, context.getString(R.string.surah_al_muzzammil), "Al-Muzzammil", 20, "مكية"),
            Surah(74, context.getString(R.string.surah_al_muddaththir), "Al-Muddaththir", 56, "مكية"),
            Surah(75, context.getString(R.string.surah_al_qiyamah), "Al-Qiyamah", 40, "مكية"),
            Surah(76, context.getString(R.string.surah_al_insan), "Al-Insan", 31, "مدنية"),
            Surah(77, context.getString(R.string.surah_al_mursalat), "Al-Mursalat", 50, "مكية"),
            Surah(78, context.getString(R.string.surah_an_naba), "An-Naba", 40, "مكية"),
            Surah(79, context.getString(R.string.surah_an_naziat), "An-Nazi'at", 46, "مكية"),
            Surah(80, context.getString(R.string.surah_abasa), "'Abasa", 42, "مكية"),
            Surah(81, context.getString(R.string.surah_at_takwir), "At-Takwir", 29, "مكية"),
            Surah(82, context.getString(R.string.surah_al_infitar), "Al-Infitar", 19, "مكية"),
            Surah(83, context.getString(R.string.surah_al_mutaffifin), "Al-Mutaffifin", 36, "مكية"),
            Surah(84, context.getString(R.string.surah_al_inshiqaq), "Al-Inshiqaq", 25, "مكية"),
            Surah(85, context.getString(R.string.surah_al_buruj), "Al-Buruj", 22, "مكية"),
            Surah(86, context.getString(R.string.surah_at_tariq), "At-Tariq", 17, "مكية"),
            Surah(87, context.getString(R.string.surah_al_ala), "Al-A'la", 19, "مكية"),
            Surah(88, context.getString(R.string.surah_al_ghashiyah), "Al-Ghashiyah", 26, "مكية"),
            Surah(89, context.getString(R.string.surah_al_fajr), "Al-Fajr", 30, "مكية"),
            Surah(90, context.getString(R.string.surah_al_balad), "Al-Balad", 20, "مكية"),
            Surah(91, context.getString(R.string.surah_ash_shams), "Ash-Shams", 15, "مكية"),
            Surah(92, context.getString(R.string.surah_al_layl), "Al-Layl", 21, "مكية"),
            Surah(93, context.getString(R.string.surah_ad_duha), "Ad-Duhaa", 11, "مكية"),
            Surah(94, context.getString(R.string.surah_ash_sharh), "Ash-Sharh", 8, "مكية"),
            Surah(95, context.getString(R.string.surah_at_tin), "At-Tin", 8, "مكية"),
            Surah(96, context.getString(R.string.surah_al_alaq), "Al-'Alaq", 19, "مكية"),
            Surah(97, context.getString(R.string.surah_al_qadr), "Al-Qadr", 5, "مكية"),
            Surah(98, context.getString(R.string.surah_al_bayyinah), "Al-Bayyinah", 8, "مدنية"),
            Surah(99, context.getString(R.string.surah_az_zalzalah), "Az-Zalzalah", 8, "مدنية"),
            Surah(100, context.getString(R.string.surah_al_adiyat), "Al-'Adiyat", 11, "مكية"),
            Surah(101, context.getString(R.string.surah_al_qariah), "Al-Qari'ah", 11, "مكية"),
            Surah(102, context.getString(R.string.surah_at_takathur), "At-Takathur", 8, "مكية"),
            Surah(103, context.getString(R.string.surah_al_asr), "Al-'Asr", 3, "مكية"),
            Surah(104, context.getString(R.string.surah_al_humazah), "Al-Humazah", 9, "مكية"),
            Surah(105, context.getString(R.string.surah_al_fil), "Al-Fil", 5, "مكية"),
            Surah(106, context.getString(R.string.surah_quraysh), "Quraysh", 4, "مكية"),
            Surah(107, context.getString(R.string.surah_al_maun), "Al-Ma'un", 7, "مكية"),
            Surah(108, context.getString(R.string.surah_al_kawthar), "Al-Kawthar", 3, "مكية"),
            Surah(109, context.getString(R.string.surah_al_kafirun), "Al-Kafirun", 6, "مكية"),
            Surah(110, context.getString(R.string.surah_an_nasr), "An-Nasr", 3, "مدنية"),
            Surah(111, context.getString(R.string.surah_al_masad), "Al-Masad", 5, "مكية"),
            Surah(112, context.getString(R.string.surah_al_ikhlas), "Al-Ikhlas", 4, "مكية"),
            Surah(113, context.getString(R.string.surah_al_falaq), "Al-Falaq", 5, "مكية"),
            Surah(114, context.getString(R.string.surah_an_nas), "An-Nas", 6, "مكية")
        )
    }

    val filteredSurahs = surahs.filter { surah ->
        val matchesSearch = searchQuery.isEmpty() ||
            surah.name.contains(searchQuery) ||
            surah.translatedName.contains(searchQuery, ignoreCase = true)

        val matchesTab = when (selectedTab) {
            tabFavorites -> surah.number in favoriteSurahs
            tabRecent -> surah.number in recentSurahs
            else -> true
        }

        matchesSearch && matchesTab
    }.let { list ->
        if (selectedTab == tabRecent) {
            list.sortedBy { recentSurahs.indexOf(it.number) }
        } else list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF121212) else Color.Transparent)
    ) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.home_background),
                contentDescription = stringResource(R.string.home_background_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF4A3F35)
        val subTextColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744)
        val tabBorderColor = if (isDarkMode) Color(0xFFD4AF37).copy(alpha = 0.5f) else Color(0xFFD4AF37).copy(alpha = 0.3f)
        val searchBg = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE8DDD0).copy(alpha = 0.5f)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkMode) Color(0xFF1E1E1E) else Color.Transparent)
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_icon_desc),
                            tint = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF6B5744)
                        )
                    }

                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { showDonationDialog = true }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResource(R.string.donate),
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                stringResource(R.string.donate),
                                fontSize = 10.sp,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                val newValue = !isDarkMode
                                onToggleDarkMode(newValue)
                                scope.launch { themeRepo.setDarkMode(newValue) }
                            }
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightlightRound,
                                contentDescription = if (isDarkMode) stringResource(R.string.light_mode) else stringResource(R.string.dark_mode),
                                tint = if (isDarkMode) Color(0xFFFFD700) else Color(0xFF6B5744)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = stringResource(R.string.home_app_subtitle),
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
                                text = tabAll,
                                isSelected = selectedTab == tabAll,
                                onClick = { selectedTab = tabAll },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                            TransparentTabButton(
                                text = tabFavorites,
                                isSelected = selectedTab == tabFavorites,
                                onClick = { selectedTab = tabFavorites },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                            TransparentTabButton(
                                text = tabRecent,
                                isSelected = selectedTab == tabRecent,
                                onClick = { selectedTab = tabRecent },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }

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
                                contentDescription = stringResource(R.string.search),
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
                                        text = stringResource(R.string.search_surah),
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

    if (showDonationDialog) {
        val donationTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
        val donationSubColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.8f)

        AlertDialog(
            onDismissRequest = { showDonationDialog = false },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFF8F0),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.support_app_development),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = donationTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.home_donation_message),
                        fontSize = 14.sp,
                        color = donationSubColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.choose_donation_method),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = donationTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val donationUrl = "https://zmmoly.github.io/Nadem/nadeem-website.html"
                    val donationOptions = listOf(
                        Triple(stringResource(R.string.donation_5), donationUrl, Color(0xFF4CAF50)),
                        Triple(stringResource(R.string.donation_10), donationUrl, Color(0xFF2196F3)),
                        Triple(stringResource(R.string.donation_20), donationUrl, Color(0xFF9C27B0)),
                        Triple(stringResource(R.string.donation_other), donationUrl, Color(0xFFE53935))
                    )

                    donationOptions.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { (label, url, color) ->
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                        showDonationDialog = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
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
                    Text(
                        stringResource(R.string.close),
                        color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744)
                    )
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
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { onFavoriteClick() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites)
                                        else stringResource(R.string.add_to_favorites),
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
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = stringResource(R.string.surah_open),
                    tint = arrowColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

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
                        Text(
                            text = stringResource(R.string.verse_count, surah.verses),
                            fontSize = 13.sp,
                            color = subColor
                        )
                        Text(
                            text = stringResource(R.string.surah_verse_separator),
                            fontSize = 13.sp,
                            color = subColor
                        )
                        Text(
                            text = surah.revelationType,
                            fontSize = 13.sp,
                            color = subColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
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
