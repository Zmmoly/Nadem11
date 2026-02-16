package awab.quran.ar.ui.screens.surah

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import awab.quran.ar.data.QuranRepository
import awab.quran.ar.ui.screens.home.Surah

// الخط العثماني - بطريقة آمنة تماماً
@Composable
private fun getUthmanicFont(): FontFamily? {
    return try {
        FontFamily(Font(R.font.uthmanic_hafs, FontWeight.Normal))
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surah: Surah,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { QuranRepository(context) }
    
    var allAyahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(surah.number) {
        try {
            val loaded = repository.getSurahAyahs(surah.number)
            if (loaded.isEmpty()) {
                errorMessage = "لم يتم العثور على آيات"
            } else {
                allAyahs = loaded
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "خطأ: ${e.message}"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            }
        ) { paddingValues ->
            when {
                isLoading -> {
                    Box(
                        Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFD4AF37))
                    }
                }
                
                errorMessage != null -> {
                    Box(
                        Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚠️", fontSize = 48.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(errorMessage ?: "", color = Color(0xFF4A3F35))
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onNavigateBack) {
                                Text("رجوع")
                            }
                        }
                    }
                }
                
                else -> {
                    val basmala = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ"
                    val processedAyahs = try {
                        if (surah.number != 9 && allAyahs.isNotEmpty()) {
                            allAyahs.mapIndexed { index, ayah ->
                                if (index == 0 && ayah.text.contains(basmala)) {
                                    ayah.copy(text = ayah.text.replace(basmala, "").trim())
                                } else {
                                    ayah
                                }
                            }
                        } else {
                            allAyahs
                        }
                    } catch (e: Exception) {
                        allAyahs
                    }
                    
                    LazyColumn(
                        Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        if (surah.number != 9) {
                            item { BasmalaCard() }
                        }
                        
                        items(processedAyahs.filter { it.text.isNotEmpty() }) { ayah ->
                            AyahCard(ayah)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BasmalaCard() {
    val uthmanicFont = getUthmanicFont()
    
    Card(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.9f)
        )
    ) {
        Text(
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = uthmanicFont,
            color = Color(0xFF4A3F35),
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )
    }
}

@Composable
fun AyahCard(ayah: Ayah) {
    val uthmanicFont = getUthmanicFont()
    
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.85f)
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(32.dp).background(Color(0xFF6B5744), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    ayah.number.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37)
                )
            }
            
            Spacer(Modifier.width(12.dp))
            
            Text(
                ayah.text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = uthmanicFont,
                color = Color(0xFF4A3F35),
                textAlign = TextAlign.Right,
                lineHeight = 50.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
