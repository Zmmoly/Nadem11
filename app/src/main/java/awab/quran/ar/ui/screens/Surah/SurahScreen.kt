package awab.quran.ar.ui.screens.surah

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import awab.quran.ar.data.Ayah
import awab.quran.ar.data.QuranPages
import awab.quran.ar.data.QuranRepository
import awab.quran.ar.ui.screens.home.Surah

// الخط العثماني للقرآن الكريم
private val UthmanicFont = FontFamily(
    Font(R.font.uthmanic_hafs, FontWeight.Normal)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surah: Surah,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { QuranRepository(context) }
    
    // حالة التحميل والأخطاء
    var allAyahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var pagesByNumber by remember { mutableStateOf<Map<Int, List<Ayah>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // الصفحة الحالية (من أرقام الصفحات الحقيقية)
    var currentPageIndex by remember { mutableStateOf(0) }
    
    // تحميل الآيات وتقسيمها حسب صفحات المصحف
    LaunchedEffect(surah.number) {
        try {
            allAyahs = repository.getSurahAyahs(surah.number)
            
            if (allAyahs.isEmpty()) {
                errorMessage = "لم يتم العثور على آيات لهذه السورة"
            } else {
                // تقسيم الآيات حسب صفحات المصحف الحقيقية
                pagesByNumber = QuranPages.groupAyahsByPages(surah.number, allAyahs)
                if (pagesByNumber.isEmpty()) {
                    // إذا لم تكن هناك بيانات صفحات، قسّم يدوياً
                    val ayahsPerPage = 15
                    val manualPages = allAyahs.chunked(ayahsPerPage)
                    pagesByNumber = manualPages.mapIndexed { index, ayahs -> 
                        index + 1 to ayahs 
                    }.toMap()
                }
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "خطأ في تحميل السورة: ${e.message}"
        }
    }
    
    // قائمة أرقام الصفحات
    val pageNumbers = pagesByNumber.keys.sorted()
    val totalPages = pageNumbers.size
    
    // الصفحة الحالية
    val currentPageNumber = if (pageNumbers.isNotEmpty() && currentPageIndex < pageNumbers.size) {
        pageNumbers[currentPageIndex]
    } else {
        1
    }
    
    val currentPageAyahs = pagesByNumber[currentPageNumber] ?: emptyList()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // الخلفية
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = "خلفية",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = surah.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3F35)
                            )
                            Text(
                                text = "${surah.verses} آية • ${surah.revelationType}",
                                fontSize = 13.sp,
                                color = Color(0xFF8B7355)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
                // شريط التنقل بين الصفحات
                if (!isLoading && errorMessage == null && totalPages > 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // زر الصفحة التالية
                            IconButton(
                                onClick = {
                                    if (currentPageIndex < totalPages - 1) {
                                        currentPageIndex++
                                    }
                                },
                                enabled = currentPageIndex < totalPages - 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "الصفحة التالية",
                                    tint = if (currentPageIndex < totalPages - 1) 
                                        Color(0xFFD4AF37) 
                                    else 
                                        Color(0xFF9B8B7A).copy(alpha = 0.3f)
                                )
                            }
                            
                            // رقم الصفحة (من المصحف)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "صفحة $currentPageNumber",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3F35)
                                )
                                Text(
                                    text = "${currentPageIndex + 1} من $totalPages",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8B7355)
                                )
                            }
                            
                            // زر الصفحة السابقة
                            IconButton(
                                onClick = {
                                    if (currentPageIndex > 0) {
                                        currentPageIndex--
                                    }
                                },
                                enabled = currentPageIndex > 0
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "الصفحة السابقة",
                                    tint = if (currentPageIndex > 0) 
                                        Color(0xFFD4AF37) 
                                    else 
                                        Color(0xFF9B8B7A).copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            when {
                // حالة التحميل
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFD4AF37)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "جاري تحميل السورة...",
                                fontSize = 16.sp,
                                color = Color(0xFF6B5744)
                            )
                        }
                    }
                }
                
                // حالة الخطأ
                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE8DDD0).copy(alpha = 0.9f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "عذراً، حدث خطأ",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3F35)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage ?: "خطأ غير معروف",
                                    fontSize = 14.sp,
                                    color = Color(0xFF8B7355),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onNavigateBack,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD4AF37)
                                    )
                                ) {
                                    Text("العودة للرئيسية")
                                }
                            }
                        }
                    }
                }
                
                // حالة النجاح - عرض صفحة الآيات
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // البسملة (في الصفحة الأولى فقط، إلا سورة التوبة)
                        if (currentPageIndex == 0 && surah.number != 9 && surah.number != 1) {
                            BasmalaCard()
                        }
                        
                        // صفحة الآيات
                        PageCard(
                            ayahs = currentPageAyahs,
                            pageNumber = currentPageNumber
                        )
                        
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BasmalaCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Text(
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = UthmanicFont,
            color = Color(0xFF4A3F35),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            lineHeight = 45.sp
        )
    }
}

@Composable
fun PageCard(
    ayahs: List<Ayah>,
    pageNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // الآيات
            ayahs.forEach { ayah ->
                AyahText(ayah)
            }
        }
    }
}

@Composable
fun AyahText(ayah: Ayah) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // رقم الآية في دائرة صغيرة
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = Color(0xFF6B5744),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ayah.number.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4AF37)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // نص الآية
        Text(
            text = ayah.text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = UthmanicFont,
            color = Color(0xFF4A3F35),
            textAlign = TextAlign.Right,
            lineHeight = 50.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
