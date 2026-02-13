package awab.quran.ar.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.ui.theme.QuranGreen
import awab.quran.ar.ui.theme.QuranGold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    onNavigateToProfile: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("") }
    var totalRecitations by remember { mutableStateOf(0) }
    var completedSurahs by remember { mutableStateOf(0) }

    // جلب بيانات المستخدم
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: ""
                    totalRecitations = document.getLong("totalRecitations")?.toInt() ?: 0
                    completedSurahs = document.getLong("completedSurahs")?.toInt() ?: 0
                }
        }
    }

    // قائمة بعض السور للعرض
    val surahs = remember {
        listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية"),
            Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية"),
            Surah(3, "آل عمران", "Ali 'Imran", 200, "مدنية"),
            Surah(18, "الكهف", "Al-Kahf", 110, "مكية"),
            Surah(36, "يس", "Ya-Sin", 83, "مكية"),
            Surah(55, "الرحمن", "Ar-Rahman", 78, "مدنية"),
            Surah(67, "الملك", "Al-Mulk", 30, "مكية"),
            Surah(78, "النبأ", "An-Naba", 40, "مكية")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "نديم",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "رفيقك في حفظ القرآن",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuranGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بطاقة الترحيب
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = QuranGreen
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "السلام عليكم ${userName.ifEmpty { "ورحمة الله" }}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // بطاقات الإحصائيات
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // عدد التسميعات
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = QuranGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$totalRecitations",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuranGreen
                            )
                            Text(
                                text = "تسميع",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // السور المكتملة
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = QuranGold,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$completedSurahs",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuranGold
                            )
                            Text(
                                text = "سورة مكتملة",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // زر بدء التسميع
            item {
                Button(
                    onClick = onNavigateToRecitation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuranGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ابدأ التسميع الآن",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // عنوان قائمة السور
            item {
                Text(
                    text = "السور المتاحة للتسميع",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuranGreen,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // قائمة السور
            items(surahs) { surah ->
                SurahCard(surah = surah, onClick = onNavigateToRecitation)
            }

            // مساحة إضافية في الأسفل
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SurahCard(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // رقم السورة
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = QuranGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuranGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // معلومات السورة
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${surah.revelationType} • ${surah.verses} آية",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // أيقونة السهم
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = QuranGreen
            )
        }
    }
}
