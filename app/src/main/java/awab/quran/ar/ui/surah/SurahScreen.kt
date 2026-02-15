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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import awab.quran.ar.data.Ayah
import awab.quran.ar.data.QuranRepository
import awab.quran.ar.ui.screens.home.Surah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surah: Surah,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { QuranRepository(context) }
    val ayahs = remember(surah.number) { 
        repository.getSurahAyahs(surah.number) 
    }

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
                // البسملة (إلا سورة التوبة)
                if (surah.number != 9 && surah.number != 1) {
                    item {
                        BasmalaCard()
                    }
                }
                
                // الآيات
                items(ayahs) { ayah ->
                    AyahCard(ayah)
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
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
fun AyahCard(ayah: Ayah) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // نص الآية
            Text(
                text = ayah.text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4A3F35),
                textAlign = TextAlign.Right,
                lineHeight = 45.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // رقم الآية في دائرة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFF6B5744),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ayah.number.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37)
                    )
                }
            }
        }
    }
}

