package awab.quran.ar.ui.screens.surah

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.graphics.Typeface
import android.media.ToneGenerator
import android.media.AudioManager
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import awab.quran.ar.R
import awab.quran.ar.data.QuranPageRepository
import awab.quran.ar.data.PageAyah
import awab.quran.ar.data.QuranPage
import awab.quran.ar.ui.screens.home.Surah
import awab.quran.ar.services.DeepgramService
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * تحويل الأرقام الإنجليزية إلى أرقام عربية
 */
fun convertToArabicNumerals(number: Int): String {
    val arabicNumerals = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
    return number.toString().map { digit ->
        if (digit.isDigit()) arabicNumerals[digit.toString().toInt()]
        else digit.toString()
    }.joinToString("")
}

/**
 * شريط اختيار الوضع (قراءة، تسميع، اختبار)
 */
@Composable
fun ModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    isDarkMode: Boolean = false
) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F0E8)
    val selectedBg = if (isDarkMode) Color(0xFF3A3A1E) else Color(0xFFD4AF37).copy(alpha = 0.9f)
    val unselectedText = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val modes = listOf(
        "اختبار" to "🧠",
        "تسميع" to "🎤",
        "قراءة" to "📖"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            modes.forEach { (mode, icon) ->
                ModeButton(mode = mode, icon = icon, isSelected = mode == selectedMode, onClick = { onModeSelected(mode) }, isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
fun RowScope.ModeButton(
    mode: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = Modifier.weight(1f).padding(4.dp),
        shape = RoundedCornerShape(40.dp),
        color = if (isSelected) Color(0xFFC4A962) else Color.Transparent,
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = mode,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744)
            )
        }
    }
}

/**
 * تحميل الخط العثماني من assets
 */
@Composable
fun rememberUthmanicFontFromAssets(): FontFamily? {
    val context = LocalContext.current
    return remember {
        try {
            // الطريقة الأولى: من assets
            val typeface = Typeface.createFromAsset(context.assets, "fonts/kfgqpc_uthman_taha.ttf")
            FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
        } catch (e1: Exception) {
            try {
                // الطريقة الثانية: من res/font مباشرة
                val typeface = androidx.core.content.res.ResourcesCompat.getFont(
                    context, awab.quran.ar.R.font.kfgqpc_uthman_taha
                )
                if (typeface != null)
                    FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
                else null
            } catch (e2: Exception) {
                null
            }
        }
    }
}

/**
 * شاشة عرض السورة - نظام الصفحات
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surah: Surah,
    onNavigateBack: () -> Unit,
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val repository = remember { QuranPageRepository(context) }
    val uthmanicFont = rememberUthmanicFontFromAssets()

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val topBarBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.Transparent
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF4A3F35)
    val iconColor = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF6B5744)
    
    // الوضع الحالي: قراءة، تسميع، اختبار
    var selectedMode by remember { mutableStateOf("قراءة") }
    var showDonationDialog by remember { mutableStateOf(false) }
    
    // البحث عن رقم الصفحة التي تبدأ بها السورة
    val initialPageNumber = remember(surah.number) {
        repository.findPageNumber(surah.number, 1) ?: 1
    }
    
    // Pager state - الصفحة الحالية
    val pagerState = rememberPagerState(initialPage = initialPageNumber - 1)
    val currentPage = pagerState.currentPage + 1
    
    // تحميل بيانات الصفحة الحالية
    var pageData by remember { mutableStateOf<QuranPage?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // تحميل الصفحة عند تغيير رقم الصفحة
    LaunchedEffect(currentPage) {
        isLoading = true
        pageData = repository.getPage(currentPage)
        isLoading = false
    }
    
    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.app_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            pageData?.ayahs?.firstOrNull()?.let { firstAyah ->
                                Text(text = firstAyah.suraName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = iconColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDonationDialog = true }) {
                            Icon(Icons.Default.Star, contentDescription = "تبرع", tint = Color(0xFFE53935))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarBg)
                )
            },
            bottomBar = {
                PageNavigationBar(currentPage = currentPage, totalPages = 604, isDarkMode = isDarkMode)
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                ModeSelector(selectedMode = selectedMode, onModeSelected = { selectedMode = it }, isDarkMode = isDarkMode)

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    HorizontalPager(count = 604, state = pagerState, modifier = Modifier.fillMaxSize(), reverseLayout = true) { page ->
                        val displayPage = page + 1
                        when {
                            isLoading && displayPage == currentPage -> LoadingPage()
                            pageData != null && displayPage == currentPage -> QuranPageContent(page = pageData!!, uthmanicFont = uthmanicFont, mode = selectedMode, isDarkMode = isDarkMode)
                            else -> LoadingPage()
                        }
                    }
                }
            }
        }
    }

    // نافذة التبرع
    if (showDonationDialog) {
        AlertDialog(
            onDismissRequest = { showDonationDialog = false },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFF8F0),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ادعم تطوير التطبيق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "جزاك الله خيرًا على دعمك لهذا المشروع القرآني. تبرعك يساعدنا على الاستمرار وخدمة المسلمين.",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val donationUrl = "https://zmmoly.github.io/Nadem/nadeem-website.html#contact"
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl))
                            context.startActivity(intent)
                            showDonationDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = Color.White)
                        Text("تبرع الآن", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

/**
 * محتوى صفحة القرآن
 */
@Composable
fun QuranPageContent(
    page: QuranPage,
    uthmanicFont: FontFamily?,
    mode: String = "قراءة",
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    when (mode) {
        "تسميع" -> RecitationMode(page = page, context = context, isDarkMode = isDarkMode, uthmanicFont = uthmanicFont)
        "اختبار" -> ExamMode(page = page, context = context, uthmanicFont = uthmanicFont, isDarkMode = isDarkMode)
        else -> ReadingMode(page = page, uthmanicFont = uthmanicFont, isDarkMode = isDarkMode)
    }
}

/**
 * وضع القراءة العادي - يعرض النص كمصحف متدفق
 */
@Composable
fun ReadingMode(
    page: QuranPage,
    uthmanicFont: FontFamily?,
    isDarkMode: Boolean = false
) {
    val quranTextColor = if (isDarkMode) Color(0xFFE8D5B0) else Color(0xFF2C2416)
    val ayahNumColor = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF6B5744)
    // تجميع الآيات في مجموعات: كل مجموعة تبدأ برأس سورة أو هي استمرار
    data class AyahGroup(
        val surahHeader: String? = null,   // اسم السورة إذا كانت بداية سورة
        val surahNumber: Int = 0,
        val showBasmala: Boolean = false,
        val ayahs: List<PageAyah>
    )

    val groups = remember(page) {
        val result = mutableListOf<AyahGroup>()
        var currentGroup = mutableListOf<PageAyah>()
        var currentSura = -1

        for (ayah in page.ayahs) {
            if (ayah.isFirstInSura && ayah.isFirstInPage ||
                (ayah.isFirstInSura && ayah.suraNumber != currentSura)) {
                // حفظ المجموعة السابقة
                if (currentGroup.isNotEmpty()) {
                    result.add(AyahGroup(ayahs = currentGroup.toList()))
                    currentGroup = mutableListOf()
                }
                // بدء مجموعة جديدة برأس سورة - الآية الأولى تنضم لباقي الآيات
                currentSura = ayah.suraNumber
                currentGroup.add(ayah)
                result.add(AyahGroup(
                    surahHeader = ayah.suraName,
                    surahNumber = ayah.suraNumber,
                    showBasmala = ayah.suraNumber != 1 && ayah.suraNumber != 9,
                    ayahs = emptyList() // الآيات ستُضاف لاحقاً
                ))
            } else {
                currentSura = ayah.suraNumber
                currentGroup.add(ayah)
            }
        }
        if (currentGroup.isNotEmpty()) {
            result.add(AyahGroup(ayahs = currentGroup.toList()))
        }
        result
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(groups.size) { idx ->
            val group = groups[idx]

            Column {
                if (group.surahHeader != null) {
                    SuraHeader(suraName = group.surahHeader, suraNumber = group.surahNumber, isDarkMode = isDarkMode)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (group.showBasmala) {
                        BasmalaHeader(font = uthmanicFont, isDarkMode = isDarkMode)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (group.ayahs.isNotEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            group.ayahs.forEach { ayah ->
                                append(cleanQuranText(ayah.text))
                                append(" ")
                                withStyle(SpanStyle(fontSize = 18.sp, color = ayahNumColor)) {
                                    append("﴿")
                                    append(convertToArabicNumerals(ayah.ayaNumber))
                                    append("﴾")
                                }
                                append(" ")
                            }
                        },
                        fontSize = 22.sp,
                        fontFamily = uthmanicFont,
                        color = quranTextColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 58.sp,
                        softWrap = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // رقم الصفحة في الأسفل
        item {
            Spacer(modifier = Modifier.height(16.dp))
            PageNumberFooter(pageNumber = page.pageNumber)
        }
    }
}

/**
 * تنظيف نص القرآن من رموز التجويد غير المدعومة في الخط
 */
fun cleanQuranText(text: String): String {
    return text
        .replace(Regex("[\u06D6-\u06DC\u06DE-\u06ED]"), "") // علامات التجويد والوقف
        .replace("\u0640\u0654", "ء") // كشيدة + همزة superscript أولاً (الآخرة)
        .replace("\u0640\u0655", "")  // كشيدة + همزة subscript
        .replace("\u0654", "ء")       // همزة superscript منفردة → همزة
        .replace("\u0655", "")        // همزة subscript منفردة → تُحذف
        .replace("\u0640", "")        // كشيدة منفردة → تُحذف
        .replace("\u0671", "ا")       // همزة الوصل ٱ → ألف عادية
        .replace("\u200F", "")        // علامة RTL
        .replace("\u200E", "")        // علامة LTR
        .trim()
}

/**
 * تنظيف النص من التشكيل للمقارنة بناءً على الإعدادات
 */
fun normalizeArabic(text: String, settings: awab.quran.ar.data.RecitationSettings): String {
    var result = text

    // حذف أرقام الآيات والرموز الخاصة
    result = result.replace(Regex("\\(\\d+\\)"), "")
    result = result.replace("ـ", "")  // تطويل

    // توحيد جميع أشكال الألف → ا (هذا يحل مشكلة الكلمات التي فيها ألف)
    result = result.replace("ٱ", "ا")  // همزة الوصل
    result = result.replace("أ", "ا")  // همزة فوق
    result = result.replace("إ", "ا")  // همزة تحت
    result = result.replace("آ", "ا")  // مد
    result = result.replace("ٰ", "ا")  // ألف خنجرية (سبب المشكلة الرئيسي)

    // حذف التشكيل (الحركات)
    result = result.replace(Regex("[\u064B-\u065F]"), "")
    result = result.replace(Regex("[،؟!]"), "")

    // توحيد التاء المربوطة والياء
    result = result.replace("ة", "ه")
    result = result.replace("ى", "ي")

    // تجاهل حرف الحاء
    if (settings.ignoreHaa) {
        result = result.replace("ح", "ه")
    }

    // تجاهل حرف العين
    if (settings.ignoreAyn) {
        result = result.replace("ع", "ا").replace("ء", "ا").replace("ئ", "ا").replace("ؤ", "ا")
    }

    // تجاهل المدود
    if (settings.ignoreMadd) {
        result = result.replace(Regex("ا+"), "ا")
        result = result.replace(Regex("و+"), "و")
        result = result.replace(Regex("ي+"), "ي")
    }

    // تجاهل مواضع الوقف
    if (settings.ignoreWaqf) {
        result = result.trimEnd('ن', 'ا', 'ه', 'م')
    }

    return result.trim()
}

/**
 * إصدار صوت خطأ
 */
@SuppressLint("MissingPermission")
fun playErrorSound(context: Context? = null) {
    try {
        // صوت
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        toneGen.startTone(ToneGenerator.TONE_PROP_NACK, 300)
        // اهتزاز
        context?.let { ctx ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(300)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * مقارنة جملة منطوقة بالمرجع وإرجاع AnnotatedString
 * النص يُكتب كما نُطق مع تعليم الأخطاء
 */
fun buildColoredText(
    spokenWords: List<String>,
    referenceWords: List<String>,
    settings: awab.quran.ar.data.RecitationSettings = awab.quran.ar.data.RecitationSettings()
): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        spokenWords.forEachIndexed { index, word ->
            val refWord = referenceWords.getOrNull(index) ?: ""
            val isCorrect = normalizeArabic(word, settings) == normalizeArabic(refWord, settings)
            withStyle(
                SpanStyle(
                    color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFD32F2F),
                    background = if (isCorrect) Color.Transparent else Color(0x22FF0000)
                )
            ) {
                append("$word ")
            }
        }
    }
}

/**
 * وضع التسميع
 */
@Composable
fun RecitationMode(
    page: QuranPage,
    context: Context,
    isDarkMode: Boolean = false,
    uthmanicFont: FontFamily? = null
) {
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val quranTextColor = if (isDarkMode) Color(0xFFE8D5B0) else Color(0xFF2C2416)
    val deepgramService = remember { DeepgramService(context) }
    val settingsRepo = remember { awab.quran.ar.data.RecitationSettingsRepository(context) }
    var settings by remember { mutableStateOf(awab.quran.ar.data.RecitationSettings()) }

    // تحميل الإعدادات من DataStore

    // النص الكامل كـ AnnotatedString مع التلوين
    var coloredText by remember { mutableStateOf(buildAnnotatedString { }) }
    var interimText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var wordCount by remember { mutableStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var hintWords by remember { mutableStateOf("") }
    var correctWords by remember { mutableStateOf(0) }
    var errorWords by remember { mutableStateOf(0) }
    var showScore by remember { mutableStateOf(false) }

    // حساب عدد الكلمات لكل آية
    // نص الصفحة كمرجع - قائمة كلمات
    val referenceWords = remember(page) {
        page.ayahs
            .joinToString(" ") { cleanQuranText(it.text) }
            .replace(Regex("[﴿﴾]"), "")          // إزالة أقواس الآيات
            .replace(Regex("\\(\\d+\\)"), "")    // إزالة أرقام الآيات (1) (2)
            .replace(Regex("[١٢٣٤٥٦٧٨٩٠0-9]+"), "") // إزالة أرقام عربية وإنجليزية
            .replace("ٱ", "ا")
            .replace("ٰ", "")
            .replace("ـ", "")
            .replace(Regex("\\s+"), " ")
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }
    }

    // دالة اختيار آية عشوائية


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coloredText = buildAnnotatedString { }
            interimText = ""
            wordCount = 0
            errorMessage = null
            deepgramService.startRecitation()
        } else {
            errorMessage = "يجب السماح بصلاحية الميكروفون للتسميع"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                deepgramService.stopRecitation()
                // زيادة العداد عند الخروج من الصفحة أثناء التسميع
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                user?.let {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users").document(it.uid)
                        .update("totalRecitations", com.google.firebase.firestore.FieldValue.increment(1))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        // تحميل الإعدادات
        launch { settingsRepo.settingsFlow.collectLatest { settings = it } }

        // عند وصول نتيجة نهائية - مقارنة ذكية تكتشف الكلمات المنسية
        deepgramService.onTranscriptionReceived = { rawText ->
            val text = rawText
                .replace(Regex("[\\[\\]\"'،؟!]"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
            val newWords = text.split(" ").filter { it.isNotEmpty() }
            var hasError = false

            // إذا كانت أول مرة يتكلم المستخدم، ابحث عن أين بدأ في النص
            var currentPos = if (wordCount == 0 && newWords.isNotEmpty()) {
                val lookupWords = newWords.take(3).map { normalizeArabic(it, settings) }
                var bestMatch = 0
                var bestScore = 0
                // ابحث عن أفضل تطابق لأول 3 كلمات
                for (i in referenceWords.indices) {
                    var score = 0
                    lookupWords.forEachIndexed { j, word ->
                        val ref = referenceWords.getOrNull(i + j) ?: ""
                        if (normalizeArabic(ref, settings) == word) score++
                    }
                    if (score > bestScore) {
                        bestScore = score
                        bestMatch = i
                    }
                }
                bestMatch
            } else {
                wordCount
            }

            val newSegment = buildAnnotatedString {
                newWords.forEach { word ->
                    val normalizedWord = normalizeArabic(word, settings)

                    // هل الكلمة تطابق الكلمة الحالية في المرجع؟
                    val currentRef = referenceWords.getOrNull(currentPos) ?: ""
                    if (normalizeArabic(currentRef, settings) == normalizedWord) {
                        // تطابق مباشر ✅
                        withStyle(SpanStyle(color = Color(0xFF1B5E20))) {
                            append("$word ")
                        }
                        currentPos++
                        correctWords++
                    } else {
                        // لا تطابق — ابحث للأمام أولاً (كلمة منسية)
                        val lookAhead = 4
                        var foundAt = -1
                        for (j in 1..lookAhead) {
                            val ahead = referenceWords.getOrNull(currentPos + j) ?: break
                            if (normalizeArabic(ahead, settings) == normalizedWord) {
                                foundAt = j
                                break
                            }
                        }

                        if (foundAt > 0) {
                            // وجدنا الكلمة للأمام — أضف الكلمات المنسية باللون الأحمر
                            for (skip in 0 until foundAt) {
                                val skipped = referenceWords.getOrNull(currentPos + skip) ?: ""
                                withStyle(SpanStyle(
                                    color = Color(0xFFD32F2F),
                                    background = Color(0x22FF0000)
                                )) {
                                    append("[$skipped] ")
                                }
                                errorWords++
                            }
                            withStyle(SpanStyle(color = Color(0xFF1B5E20))) {
                                append("$word ")
                            }
                            currentPos += foundAt + 1
                            correctWords++
                            hasError = true
                        } else {
                            // ابحث للخلف (المستخدم أعاد من نقطة سابقة)
                            val lookBack = 10
                            var foundBefore = -1
                            for (j in 1..lookBack) {
                                val before = referenceWords.getOrNull(currentPos - j) ?: break
                                if (normalizeArabic(before, settings) == normalizedWord) {
                                    foundBefore = j
                                    break
                                }
                            }

                            if (foundBefore > 0) {
                                // وجدنا الكلمة للخلف — رجّع الموضع وأكمل
                                currentPos -= (foundBefore - 1)
                                withStyle(SpanStyle(color = Color(0xFF1B5E20))) {
                                    append("$word ")
                                }
                                currentPos++
                                correctWords++
                            } else {
                                // كلمة خاطئة ❌
                                withStyle(SpanStyle(
                                    color = Color(0xFFD32F2F),
                                    background = Color(0x22FF0000)
                                )) {
                                    append("$word ")
                                }
                                currentPos++
                                errorWords++
                                hasError = true
                            }
                        }
                    }
                }
            }

            val finalPos = currentPos
            CoroutineScope(Dispatchers.Main).launch {
                coloredText = buildAnnotatedString {
                    append(coloredText)
                    append(newSegment)
                }
                wordCount = finalPos
                interimText = ""
                if (hasError) CoroutineScope(Dispatchers.IO).launch { playErrorSound(context) }
            }
        }

        // النتيجة المؤقتة - تُعرض كنص عادي بدون تلوين
        deepgramService.onInterimTranscription = { text ->
            CoroutineScope(Dispatchers.Main).launch { interimText = text }
        }

        deepgramService.onError = { error ->
            CoroutineScope(Dispatchers.Main).launch {
                errorMessage = error
                isRecording = false
            }
        }

        deepgramService.onConnectionEstablished = {
            CoroutineScope(Dispatchers.Main).launch { isRecording = true }
        }
    }

    // Dialog نتيجة التسميع — درجة فقط
    if (showScore) {
        val score = if (correctWords + errorWords > 0)
            (correctWords.toFloat() / (correctWords + errorWords) * 100).toInt()
        else 0
        val scoreColor = when {
            score >= 90 -> Color(0xFF1B5E20)
            score >= 70 -> Color(0xFFF57F17)
            else -> Color(0xFFD32F2F)
        }
        val scoreEmoji = when {
            score >= 90 -> "🌟 ممتاز!"
            score >= 80 -> "👍 جيد جداً!"
            score >= 70 -> "😊 جيد!"
            score >= 50 -> "💪 تحتاج مراجعة"
            else -> "📖 راجع هذا الجزء"
        }
        val scoreBg = when {
            score >= 90 -> Color(0xFFE8F5E9)
            score >= 70 -> Color(0xFFFFFDE7)
            else -> Color(0xFFFFEBEE)
        }

        AlertDialog(
            onDismissRequest = { showScore = false },
            containerColor = scoreBg,
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "$score%",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                    Text(
                        text = scoreEmoji,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showScore = false },
                    colors = ButtonDefaults.buttonColors(containerColor = scoreColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حسناً", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // أيقونة الميكروفون
        Box(
            modifier = Modifier.size(140.dp).padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFD4AF37).copy(alpha = 0.25f), CircleShape)
                )
            }
            Icon(
                painter = painterResource(id = android.R.drawable.ic_btn_speak_now),
                contentDescription = "ميكروفون",
                modifier = Modifier.size(64.dp),
                tint = if (isRecording) Color(0xFFD4AF37) else Color(0xFF6B5744)
            )
        }

        // زر بدء/إيقاف
        Button(
            onClick = {
                if (isRecording) {
                    deepgramService.stopRecitation()
                    isRecording = false
                    if (wordCount > 0) {
                        showScore = true
                        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        user?.let {
                            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            val userDoc = firestore.collection("users").document(it.uid)
                            userDoc.update("totalRecitations", com.google.firebase.firestore.FieldValue.increment(1))
                        }
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        coloredText = buildAnnotatedString { }
                        interimText = ""
                        wordCount = 0
                        correctWords = 0
                        errorWords = 0
                        showScore = false
                        errorMessage = null
                        deepgramService.startRecitation()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color(0xFFD32F2F) else Color(0xFF6B5744)
            ),
            modifier = Modifier.fillMaxWidth(0.7f).height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text(
                text = if (isRecording) "إيقاف التسميع" else "بدء التسميع",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // زر التلميح — يكشف 10 كلمات تالية
        TextButton(
            onClick = {
                val nextWords = referenceWords
                    .drop(wordCount)
                    .take(10)
                    .joinToString(" ")
                hintWords = nextWords
                showHint = !showHint
            }
        ) {
            Text(
                text = if (showHint) "🙈 إخفاء التلميح" else "💡 تلميح — اكشف 10 كلمات",
                color = Color(0xFF8B7355),
                fontSize = 14.sp
            )
        }

        // بطاقة التلميح
        if (showHint && hintWords.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💡 التلميح:",
                        fontSize = 12.sp,
                        color = Color(0xFF8B6914),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = hintWords,
                        fontSize = 20.sp,
                        fontFamily = uthmanicFont,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Right,
                        lineHeight = 36.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // رسالة الخطأ
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = error,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // مفتاح الألوان
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text("● صحيح  ", color = Color(0xFF1B5E20), fontSize = 13.sp)
            Text("● خطأ", color = Color(0xFFD32F2F), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // النص مع التلوين
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE6)),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                item {
                    if (coloredText.text.isEmpty() && interimText.isEmpty()) {
                        Text(
                            text = "ابدأ التسميع...",
                            fontSize = 20.sp,
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Right,
                            lineHeight = 40.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // النص المُلوَّن (نهائي)
                        Text(
                            text = coloredText,
                            fontSize = 20.sp,
                            fontFamily = uthmanicFont,
                            textAlign = TextAlign.Right,
                            lineHeight = 44.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // النص المؤقت أثناء الكلام (رمادي)
                        if (interimText.isNotEmpty()) {
                            Text(
                                text = interimText,
                                fontSize = 20.sp,
                                fontFamily = uthmanicFont,
                                color = Color(0xFF9E7B5A),
                                textAlign = TextAlign.Right,
                                lineHeight = 44.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * رأس البسملة
 */
@Composable
fun BasmalaHeader(font: FontFamily?, isDarkMode: Boolean = false) {
    Text(
        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = font,
        color = if (isDarkMode) Color(0xFFE8D5B0) else Color(0xFF4A3F35),
        textAlign = TextAlign.Center,
        lineHeight = 45.sp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    )
}

/**
 * نص الآية مع رقمها
 */
@Composable
fun QuranAyahText(
    ayah: PageAyah,
    font: FontFamily?,
    showSuraHeader: Boolean
) {
    Column {
        // عنوان السورة إذا كانت أول السورة
        if (showSuraHeader) {
            SuraHeader(
                suraName = ayah.suraName,
                suraNumber = ayah.suraNumber
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // البسملة بعد رأس السورة (ما عدا سورة التوبة والفاتحة)
            if (ayah.suraNumber != 1 && ayah.suraNumber != 9) {
                BasmalaHeader(font = font)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // نص الآية مع رقمها
        Text(
            text = buildAnnotatedString {
                append(ayah.text)
                append(" ")
                withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B5744))) {
                    append("﴿")
                    append(convertToArabicNumerals(ayah.ayaNumber))
                    append("﴾")
                }
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = font,
            color = Color(0xFF2C2416),
            textAlign = TextAlign.Right,
            lineHeight = 45.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * رأس السورة
 */
@Composable
fun SuraHeader(suraName: String, suraNumber: Int, isDarkMode: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF2C2416) else Color(0xFF6B5744))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("۞", fontSize = 20.sp, color = Color(0xFFD4AF37))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "سورة $suraName", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37))
            Spacer(modifier = Modifier.width(12.dp))
            Text("۞", fontSize = 20.sp, color = Color(0xFFD4AF37))
        }
    }
}

/**
 * تذييل رقم الصفحة
 */
@Composable
fun PageNumberFooter(pageNumber: Int) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF6B5744), modifier = Modifier.padding(8.dp)) {
            Text(
                text = pageNumber.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4AF37),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * شريط التنقل السفلي
 */
@Composable
fun PageNavigationBar(
    currentPage: Int,
    totalPages: Int,
    isDarkMode: Boolean = false
) {
    Surface(
        color = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE8DDD0).copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF4A3F35)
            Text(text = "$currentPage من $totalPages", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
            LinearProgressIndicator(
                progress = currentPage.toFloat() / totalPages,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp).height(4.dp),
                color = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF6B5744),
                trackColor = Color(0xFFD4AF37).copy(alpha = 0.3f)
            )
            val juzNumber = ((currentPage - 1) / 20) + 1
            Text(text = "الجزء $juzNumber", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

/**
 * وضع الاختبار
 */
@Composable
fun ExamMode(
    page: QuranPage,
    context: Context,
    uthmanicFont: FontFamily?,
    isDarkMode: Boolean = false
) {
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val quranTextColor = if (isDarkMode) Color(0xFFE8D5B0) else Color(0xFF2C2416)
    val repository = remember { QuranPageRepository(context) }
    val deepgramService = remember { DeepgramService(context) }
    val settingsRepo = remember { awab.quran.ar.data.RecitationSettingsRepository(context) }
    var settings by remember { mutableStateOf(awab.quran.ar.data.RecitationSettings()) }

    // نطاق الصفحات
    var fromPage by remember { mutableStateOf("1") }
    var toPage by remember { mutableStateOf("604") }
    var questionCount by remember { mutableStateOf("10") }
    var questionLength by remember { mutableStateOf("متوسط") } // قصير=40، متوسط=60، طويل=80
    var targetWordCount by remember { mutableStateOf(60) }
    var totalQuestions by remember { mutableStateOf(10) }
    var currentQuestion by remember { mutableStateOf(0) }
    var showSetup by remember { mutableStateOf(true) }
    var showFinished by remember { mutableStateOf(false) }
    var shouldAdvance by remember { mutableStateOf(false) }
    var shouldStartRecording by remember { mutableStateOf(false) }

    // الآية العشوائية المختارة
    var randomAyah by remember { mutableStateOf<PageAyah?>(null) }
    var randomPageData by remember { mutableStateOf<QuranPage?>(null) }
    var ayahAudioUrl by remember { mutableStateOf("") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    // التسميع
    var coloredText by remember { mutableStateOf(buildAnnotatedString { }) }
    var interimText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var wordCount by remember { mutableStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var hintWords by remember { mutableStateOf("") }
    var correctWords by remember { mutableStateOf(0) }
    var errorWords by remember { mutableStateOf(0) }
    var showScore by remember { mutableStateOf(false) }
    var referenceWords by remember { mutableStateOf<List<String>>(emptyList()) }


    fun pickRandomAyah() {
        val from = fromPage.toIntOrNull()?.coerceIn(1, 604) ?: 1
        val to = toPage.toIntOrNull()?.coerceIn(from, 604) ?: 604
        val randomPageNum = (from..to).random()
        val pageData = repository.getPage(randomPageNum)
        val ayah = pageData?.ayahs?.randomOrNull()
        if (pageData == null || ayah == null) return

        randomAyah = ayah
        randomPageData = pageData

        // بناء المرجع من باقي الآيات بعد الآية المختارة
        // المرجع هو نص الآية العشوائية نفسها فقط
        referenceWords = ayah.text
            .replace(Regex("\\(\\d+\\)"), "")
            .replace("ٱ", "ا").replace("ٰ", "").replace("ـ", "")
            .replace(Regex("\\s+"), " ").trim()
            .split(" ").filter { it.isNotEmpty() }

        // رابط الصوت من everyayah.com
        val suraFormatted = ayah.suraNumber.toString().padStart(3, '0')
        val ayahFormatted = ayah.ayaNumber.toString().padStart(3, '0')
        ayahAudioUrl = "https://everyayah.com/data/Alafasy_128kbps/${suraFormatted}${ayahFormatted}.mp3"

        // إعادة ضبط التسميع
        coloredText = buildAnnotatedString { }
        interimText = ""
        wordCount = 0
        errorMessage = null
        isRecording = false
        isPlayingAudio = false
        mediaPlayer?.release()
        mediaPlayer = null

        currentQuestion += 1
        showSetup = false
        showFinished = false
    }

    // دالة تشغيل الصوت مع كشف الصمت
    fun playAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingAudio = true
        try {
            val player = MediaPlayer().apply {
                setDataSource(ayahAudioUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    // راقب مستوى الصوت كل 100ms لكشف الصمت
                    CoroutineScope(Dispatchers.Main).launch {
                        var silenceStart = 0L
                        var lastPosition = -1
                        val silenceThresholdMs = 600L // صمت لأكثر من 600ms = وقفة القارئ
                        val minPlayMs = 2000L          // على الأقل ثانيتين قبل الكشف
                        val startTime = System.currentTimeMillis()

                        while (isPlayingAudio && mp.isPlaying) {
                            val pos = mp.currentPosition
                            val elapsed = System.currentTimeMillis() - startTime

                            if (elapsed >= minPlayMs) {
                                if (pos == lastPosition) {
                                    // الموضع لم يتغير = صمت أو وقف
                                    if (silenceStart == 0L) silenceStart = System.currentTimeMillis()
                                    val silenceDuration = System.currentTimeMillis() - silenceStart
                                    if (silenceDuration >= silenceThresholdMs) {
                                        // وقفة مكتشفة → أوقف التشغيل وأصدر تنبيه للمستخدم
                                        mp.pause()
                                        isPlayingAudio = false
                                        // صوت تنبيه ثم ابدأ التسميع تلقائياً
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                                                kotlinx.coroutines.delay(250)
                                                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                                                kotlinx.coroutines.delay(300)
                                            } catch (e: Exception) { e.printStackTrace() }
                                        }
                                        kotlinx.coroutines.delay(750)
                                        shouldStartRecording = true
                                        break
                                    }
                                } else {
                                    silenceStart = 0L
                                    lastPosition = pos
                                }
                            } else {
                                lastPosition = pos
                            }
                            kotlinx.coroutines.delay(100)
                        }
                    }
                }
                setOnCompletionListener {
                    isPlayingAudio = false
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                            kotlinx.coroutines.delay(250)
                            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                            kotlinx.coroutines.delay(300)
                        } catch (e: Exception) { e.printStackTrace() }
                        shouldStartRecording = true
                    }
                }
                setOnErrorListener { _, _, _ -> isPlayingAudio = false; false }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            isPlayingAudio = false
        }
    }

    // تنظيف MediaPlayer عند الخروج
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            if (isRecording) deepgramService.stopRecitation()
        }
    }

    // مراقبة shouldAdvance للانتقال للسؤال التالي من composable scope
    LaunchedEffect(shouldAdvance) {
        if (shouldAdvance) {
            shouldAdvance = false
            pickRandomAyah()
        }
    }

    // فتح الميكروفون تلقائياً بعد الصفارة
    LaunchedEffect(shouldStartRecording) {
        if (shouldStartRecording) {
            shouldStartRecording = false
            coloredText = buildAnnotatedString { }
            interimText = ""
            wordCount = 0
            errorMessage = null
            deepgramService.startRecitation()
        }
    }

    // تشغيل الصوت تلقائياً عند اختيار آية جديدة
    LaunchedEffect(randomAyah) {
        if (randomAyah != null && ayahAudioUrl.isNotEmpty()) {
            kotlinx.coroutines.delay(300)
            playAudio()
        }
    }

    // تحميل إعدادات التسميع
    LaunchedEffect(Unit) {
        launch { settingsRepo.settingsFlow.collectLatest { settings = it } }

        deepgramService.onTranscriptionReceived = { text ->
            val newWords = text.trim().split(" ").filter { it.isNotEmpty() }
            var hasError = false
            val newSegment = buildAnnotatedString {
                newWords.forEachIndexed { i, word ->
                    val refWord = referenceWords.getOrNull(wordCount + i) ?: ""
                    val isCorrect = normalizeArabic(word, settings) == normalizeArabic(refWord, settings)
                    if (!isCorrect) hasError = true
                    withStyle(SpanStyle(
                        color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFD32F2F),
                        background = if (isCorrect) Color.Transparent else Color(0x22FF0000)
                    )) { append("$word ") }
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                coloredText = buildAnnotatedString { append(coloredText); append(newSegment) }
                wordCount += newWords.size
                interimText = ""
                if (hasError) CoroutineScope(Dispatchers.IO).launch { playErrorSound(context) }

                // أوقف التسميع تلقائياً عند بلوغ عدد الكلمات المطلوب
                if (wordCount >= targetWordCount) {
                    deepgramService.stopRecitation()
                    isRecording = false
                    // نغمة إنهاء تنبّه المستخدم بالتوقف
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 800)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    kotlinx.coroutines.delay(1200)
                    if (currentQuestion >= totalQuestions) {
                        showFinished = true
                    } else {
                        shouldAdvance = true
                    }
                }
            }
        }

        deepgramService.onInterimTranscription = { text ->
            CoroutineScope(Dispatchers.Main).launch { interimText = text }
        }

        deepgramService.onError = { error ->
            CoroutineScope(Dispatchers.Main).launch { errorMessage = error; isRecording = false }
        }

        deepgramService.onConnectionEstablished = {
            CoroutineScope(Dispatchers.Main).launch { isRecording = true }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coloredText = buildAnnotatedString { }
            interimText = ""
            wordCount = 0
            errorMessage = null
            deepgramService.startRecitation()
        } else {
            errorMessage = "يجب السماح بصلاحية الميكروفون للتسميع"
        }
    }




    if (showSetup) {
        // شاشة إعداد النطاق
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🧠 وضع الاختبار",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "حدد نطاق الصفحات للاختبار",
                fontSize = 15.sp,
                color = subColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("من صفحة", fontSize = 14.sp, color = titleColor, modifier = Modifier.padding(bottom = 8.dp))
                            OutlinedTextField(
                                value = fromPage,
                                onValueChange = { if (it.length <= 3) fromPage = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                                ),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD4AF37),
                                    unfocusedBorderColor = if (isDarkMode) Color(0xFF555555) else Color(0xFFB5A590),
                                    focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                                    focusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C),
                                    unfocusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Text("—", fontSize = 24.sp, color = subColor)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("إلى صفحة", fontSize = 14.sp, color = titleColor, modifier = Modifier.padding(bottom = 8.dp))
                            OutlinedTextField(
                                value = toPage,
                                onValueChange = { if (it.length <= 3) toPage = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                                ),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD4AF37),
                                    unfocusedBorderColor = if (isDarkMode) Color(0xFF555555) else Color(0xFFB5A590),
                                    focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                                    focusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C),
                                    unfocusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // عدد الأسئلة
                    Text("عدد الأسئلة", fontSize = 14.sp, color = titleColor, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = questionCount,
                        onValueChange = { if (it.length <= 3) questionCount = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.width(120.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = if (isDarkMode) Color(0xFF555555) else Color(0xFFB5A590),
                            focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                            unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White,
                            focusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C),
                            unfocusedTextColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF2C2C2C)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // طول السؤال
                    Text("طول السؤال", fontSize = 14.sp, color = titleColor, modifier = Modifier.padding(bottom = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Triple("قصير", "40 كلمة", Color(0xFF4A7C59)),
                            Triple("متوسط", "60 كلمة", Color(0xFF6B5744)),
                            Triple("طويل", "80 كلمة", Color(0xFF8B4513))
                        ).forEach { (label, sub, color) ->
                            val isSelected = questionLength == label
                            Surface(
                                onClick = { questionLength = label },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) color else if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFEDE8DF),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744)
                                    )
                                    Text(
                                        text = sub,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else if (isDarkMode) Color(0xFF888888) else Color(0xFF9B8B7A)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            totalQuestions = questionCount.toIntOrNull()?.coerceIn(1, 100) ?: 10
                            targetWordCount = when (questionLength) {
                                "قصير" -> 40
                                "طويل" -> 80
                                else -> 60
                            }
                            currentQuestion = 0
                            showFinished = false
                            pickRandomAyah()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B5744)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("ابدأ الاختبار 🎲", fontSize = 17.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else if (showFinished) {
        // شاشة انتهاء الاختبار
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎉", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("أحسنت! أكملت الاختبار", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = titleColor, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("لقد أجبت على $totalQuestions سؤال", fontSize = 16.sp, color = subColor)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    currentQuestion = 0
                    showFinished = false
                    showSetup = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B5744)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("اختبار جديد 🔄", fontSize = 17.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // شاشة الاختبار
        val ayah = randomAyah ?: return

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // شريط التقدم
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "السؤال $currentQuestion من $totalQuestions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Text(
                    text = "${((currentQuestion.toFloat() / totalQuestions) * 100).toInt()}%",
                    fontSize = 14.sp,
                    color = titleColor
                )
            }
            LinearProgressIndicator(
                progress = currentQuestion.toFloat() / totalQuestions,
                modifier = Modifier.fillMaxWidth().height(6.dp).padding(bottom = 12.dp),
                color = Color(0xFFD4AF37),
                trackColor = Color(0xFFD4AF37).copy(alpha = 0.2f)
            )

            // بطاقة الآية العشوائية
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6B5744)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "سورة ${ayah.suraName} — الآية ${convertToArabicNumerals(ayah.ayaNumber)}",
                        fontSize = 14.sp,
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = ayah.text + " ﴿${convertToArabicNumerals(ayah.ayaNumber)}﴾",
                        fontSize = 22.sp,
                        fontFamily = uthmanicFont,
                        color = Color.White,
                        textAlign = TextAlign.Right,
                        lineHeight = 42.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // زر تشغيل الصوت
            Button(
                onClick = { playAudio() },
                enabled = !isPlayingAudio,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text(
                    text = if (isPlayingAudio) "⏸ جارٍ التشغيل" else "▶ استمع للآية",
                    fontSize = 14.sp,
                    color = quranTextColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // زر تغيير النطاق
            TextButton(onClick = { showSetup = true }) {
                Text("⚙ تغيير نطاق الصفحات", color = subColor, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "واصل القراءة من بعد هذه الآية...",
                fontSize = 14.sp,
                color = subColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // مفتاح الألوان وزر التسميع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text("● صحيح  ", color = Color(0xFF1B5E20), fontSize = 13.sp)
                    Text("● خطأ", color = Color(0xFFD32F2F), fontSize = 13.sp)
                }
                Button(
                    onClick = {
                        if (isRecording) {
                            deepgramService.stopRecitation()
                            isRecording = false
                        } else {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                coloredText = buildAnnotatedString { }
                                interimText = ""
                                wordCount = 0
                                errorMessage = null
                                deepgramService.startRecitation()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color(0xFFD32F2F) else Color(0xFF6B5744)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = if (isRecording) "⏹ إيقاف" else "🎤 تسميع",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // منطقة النص المُلوَّن
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF3A1A1A) else Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = error, color = Color(0xFFD32F2F), modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5EFE6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    item {
                        if (coloredText.text.isEmpty() && interimText.isEmpty()) {
                            Text(
                                text = "اضغط تسميع وواصل القراءة...",
                                fontSize = 20.sp,
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Right,
                                lineHeight = 40.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = coloredText,
                                fontSize = 20.sp,
                                fontFamily = uthmanicFont,
                                textAlign = TextAlign.Right,
                                lineHeight = 40.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (interimText.isNotEmpty()) {
                                Text(
                                    text = interimText,
                                    fontSize = 20.sp,
                                    color = Color(0xFF9E7B5A),
                                    textAlign = TextAlign.Right,
                                    lineHeight = 40.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * شاشة التحميل
 */
@Composable
fun LoadingPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFD4AF37)
        )
    }
}
 
