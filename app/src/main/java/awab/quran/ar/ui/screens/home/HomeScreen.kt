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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
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
    var selectedTab by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    // جلب بيانات المستخدم
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: ""
                }
        }
    }

    // قائمة السور
    val surahs = remember {
        listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية"),
            Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية"),
            Surah(60, "الأغمران", "Al-Mumtahanah", 260, "مدنية"),
            Surah(36, "الكوف", "Al-Kahf", 110, "مكية"),
            Surah(55, "الرحمن", "Ar-Rahman", 78, "مدنية"),
            Surah(67, "الملك", "Al-Mulk", 30, "مكية"),
            Surah(78, "النبأ", "An-Naba", 40, "مكية")
        )
    }

    // تصفية السور
    val filteredSurahs = surahs.filter { surah ->
        val matchesSearch = searchQuery.isEmpty() || 
            surah.name.contains(searchQuery) || 
            surah.translatedName.contains(searchQuery, ignoreCase = true)
        
        val matchesTab = when (selectedTab) {
            "المفضلة" -> true
            "اخر قراءة" -> true
            else -> true
        }
        
        matchesSearch && matchesTab
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // الخلفية الخاصة بالصفحة الرئيسية
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = "خلفية الصفحة الرئيسية",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "نديم",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3F35)
                            )
                            Text(
                                text = "مرحباً ${userName.ifEmpty { "بك" }}",
                                fontSize = 14.sp,
                                color = Color(0xFF8B7355)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "الملف الشخصي",
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
                // عنوان "خير جليس لحفظ كتاب الله"
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "خير جليس لحفظ كتاب اللَّهِ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B5744),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // التبويبات
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TabButton(
                                text = "الكل",
                                isSelected = selectedTab == "الكل",
                                onClick = { selectedTab = "الكل" },
                                modifier = Modifier.weight(1f)
                            )
                            TabButton(
                                text = "المفضلة",
                                isSelected = selectedTab == "المفضلة",
                                onClick = { selectedTab = "المفضلة" },
                                modifier = Modifier.weight(1f)
                            )
                            TabButton(
                                text = "اخر قراءة",
                                isSelected = selectedTab == "اخر قراءة",
                                onClick = { selectedTab = "اخر قراءة" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // شريط البحث
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = Color(0xFF9B8B7A).copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "محمدين لا حجم يمين المسطلوة",
                                fontSize = 14.sp,
                                color = Color(0xFF9B8B7A).copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // قائمة السور
                items(filteredSurahs) { surah ->
                    GoldenCircleSurahCard(
                        surah = surah,
                        onClick = onNavigateToRecitation
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    // ذهبي داكن براق (مثل الذهب الحقيقي)
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFC9A961),
                            Color(0xFFB8941E),
                            Color(0xFFC9A961)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF9B8B7A)
        )
    }
}

@Composable
fun GoldenCircleSurahCard(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8DDD0).copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دائرة رقم السورة الذهبية
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // الإطار الذهبي الخارجي مع اللمعة الدائرية
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(
                                width = 3.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFFE8D7A8), // لمعة ذهبية فاتحة
                                        Color(0xFFD4AF37), // ذهبي رئيسي
                                        Color(0xFFC9A961), // ذهبي متوسط
                                        Color(0xFFB8941E), // ذهبي داكن
                                        Color(0xFFA67C00), // ذهبي أغمق
                                        Color(0xFFB8941E), // ذهبي داكن
                                        Color(0xFFC9A961), // ذهبي متوسط
                                        Color(0xFFD4AF37), // ذهبي رئيسي
                                        Color(0xFFE8D7A8)  // لمعة ذهبية فاتحة
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF3E342B), // بني داكن جداً (مركز)
                                        Color(0xFF2D2419), // بني أغمق
                                        Color(0xFF1F1811)  // بني داكن جداً (تقريباً أسود)
                                    )
                                )
                            )
                    )
                    
                    // رقم السورة بالذهبي الساطع
                    Text(
                        text = surah.number.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37) // ذهبي براق
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // معلومات السورة
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = surah.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3F35)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${surah.verses} آية",
                            fontSize = 13.sp,
                            color = Color(0xFF7A6B5A)
                        )
                        Text(
                            text = " • ",
                            fontSize = 13.sp,
                            color = Color(0xFF7A6B5A)
                        )
                        Text(
                            text = surah.revelationType,
                            fontSize = 13.sp,
                            color = Color(0xFF7A6B5A)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // أيقونة السهم
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "فتح",
                    tint = Color(0xFF9B8B7A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
