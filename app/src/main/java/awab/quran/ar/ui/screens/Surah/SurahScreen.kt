package awab.quran.ar.ui.screens.surah

import android.graphics.Typeface
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
import awab.quran.ar.R
import awab.quran.ar.data.QuranPageRepository
import awab.quran.ar.data.PageAyah
import awab.quran.ar.data.QuranPage
import awab.quran.ar.ui.screens.home.Surah
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
    onModeSelected: (String) -> Unit
) {
    val modes = listOf(
        "اختبار" to "🧠",
        "تسميع" to "🎤",
        "قراءة" to "📖"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5EFE6)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            modes.forEach { (mode, icon) ->
                ModeButton(
                    mode = mode,
                    icon = icon,
                    isSelected = mode == selectedMode,
                    onClick = { onModeSelected(mode) }
                )
            }
        }
    }
}

/**
 * زر الوضع الواحد
 */
@Composable
fun RowScope.ModeButton(
    mode: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        shape = RoundedCornerShape(40.dp),
        color = if (isSelected) Color(0xFFC4A962) else Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = mode,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF6B5744)
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
            val typeface = Typeface.createFromAsset(context.assets, "fonts/uthmanic_hafs.otf")
            FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
        } catch (e: Exception) {
            null
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
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { QuranPageRepository(context) }
    val uthmanicFont = rememberUthmanicFontFromAssets()
    
    // الوضع الحالي: قراءة، تسميع، اختبار
    var selectedMode by remember { mutableStateOf("قراءة") }
    
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        // الخلفية
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "صفحة $currentPage",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3F35)
                            )
                            pageData?.ayahs?.firstOrNull()?.let { firstAyah ->
                                Text(
                                    text = firstAyah.suraName,
                                    fontSize = 13.sp,
                                    color = Color(0xFF8B7355)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color(0xFF6B5744)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                // شريط التنقل السفلي
                PageNavigationBar(
                    currentPage = currentPage,
                    totalPages = 604
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // شريط الأوضاع (قراءة، تسميع، اختبار)
                ModeSelector(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
                
                // ViewPager للتنقل بين الصفحات
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    HorizontalPager(
                        count = 604,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true // من اليمين لليسار
                    ) { page ->
                        val displayPage = page + 1
                        
                        when {
                            isLoading && displayPage == currentPage -> {
                                LoadingPage()
                            }
                            pageData != null && displayPage == currentPage -> {
                                QuranPageContent(
                                    page = pageData!!,
                                    uthmanicFont = uthmanicFont,
                                    mode = selectedMode
                                )
                            }
                            else -> {
                                LoadingPage()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * محتوى صفحة القرآن
 */
@Composable
fun QuranPageContent(
    page: QuranPage,
    uthmanicFont: FontFamily?,
    mode: String = "قراءة"
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // عرض الآيات
        items(page.ayahs) { ayah ->
            QuranAyahText(
                ayah = ayah,
                font = uthmanicFont,
                showSuraHeader = ayah.isFirstInPage && ayah.isFirstInSura
            )
        }
        
        // رقم الصفحة في الأسفل
        item {
            Spacer(modifier = Modifier.height(16.dp))
            PageNumberFooter(pageNumber = page.pageNumber)
        }
    }
}

/**
 * رأس البسملة
 */
@Composable
fun BasmalaHeader(font: FontFamily?) {
    Text(
        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = font,
        color = Color(0xFF4A3F35),
        textAlign = TextAlign.Center,
        lineHeight = 45.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
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
                // إضافة رقم الآية بشكل مزخرف
                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B5744)
                    )
                ) {
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
fun SuraHeader(suraName: String, suraNumber: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B5744)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // زخرفة يمين
            Text("۞", fontSize = 20.sp, color = Color(0xFFD4AF37))
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // اسم السورة
            Text(
                text = "سورة $suraName",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4AF37)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // زخرفة يسار
            Text("۞", fontSize = 20.sp, color = Color(0xFFD4AF37))
        }
    }
}

/**
 * تذييل رقم الصفحة
 */
@Composable
fun PageNumberFooter(pageNumber: Int) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF6B5744),
            modifier = Modifier.padding(8.dp)
        ) {
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
    totalPages: Int
) {
    Surface(
        color = Color(0xFFE8DDD0).copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // معلومات الصفحة
            Text(
                text = "$currentPage من $totalPages",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A3F35)
            )
            
            // شريط التقدم
            LinearProgressIndicator(
                progress = currentPage.toFloat() / totalPages,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(4.dp),
                color = Color(0xFF6B5744),
                trackColor = Color(0xFFD4AF37).copy(alpha = 0.3f)
            )
            
            // الجزء
            val juzNumber = ((currentPage - 1) / 20) + 1
            Text(
                text = "الجزء $juzNumber",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A3F35)
            )
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
 
