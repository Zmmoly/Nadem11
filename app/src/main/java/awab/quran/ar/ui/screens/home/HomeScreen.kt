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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
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

    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: ""
                }
        }
    }

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
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = "خلفية الصفحة الرئيسية",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            tint = Color(0xFF6B5744)
                        )
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
                            color = Color(0xFF4A3F35)
                        )
                        Text(
                            text = "خير جليس لحفظ كتاب اللَّهِ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B5744)
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
                // عنوان "خير جليس لحفظ كتاب الله" في المنتصف
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

                // التبويبات - شفافة مع حدود خفيفة
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color(0xFFD4AF37).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(25.dp)
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
                                modifier = Modifier.weight(1f)
                            )
                            TransparentTabButton(
                                text = "المفضلة",
                                isSelected = selectedTab == "المفضلة",
                                onClick = { selectedTab = "المفضلة" },
                                modifier = Modifier.weight(1f)
                            )
                            TransparentTabButton(
                                text = "اخر قراءة",
                                isSelected = selectedTab == "اخر قراءة",
                                onClick = { selectedTab = "اخر قراءة" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

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
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = Color(0xFF9B8B7A).copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // حقل البحث
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "ابحث عن سورة",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9B8B7A).copy(alpha = 0.5f)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color(0xFF4A3F35),
                                    unfocusedTextColor = Color(0xFF4A3F35),
                                    cursorColor = Color(0xFFD4AF37)
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Right
                                )
                            )
                        }
                    }
                }

                items(filteredSurahs) { surah ->
                    DecoratedSurahCard(
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
fun TransparentTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            color = if (isSelected) Color.White else Color(0xFF9B8B7A)
        )
    }
}

@Composable
fun DecoratedSurahCard(
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
                // الدائرة الذهبية مع الزخرفة
                Box(
                    modifier = Modifier.size(80.dp), // حجم أكبر للزخرفة
                    contentAlignment = Alignment.Center
                ) {
                    // الزخرفة الإسلامية خلف الدائرة
                    // ملاحظة: يجب إضافة صورة الزخرفة في drawable
                    // مثال: R.drawable.islamic_decoration
                    
                    // إذا كانت صورة الزخرفة موجودة:
                    // Image(
                    //     painter = painterResource(id = R.drawable.islamic_decoration),
                    //     contentDescription = "زخرفة إسلامية",
                    //     modifier = Modifier
                    //         .size(80.dp)
                    //         .alpha(0.4f),
                    //     contentScale = ContentScale.Fit
                    // )
                    
                    // بديل مؤقت: دوائر متداخلة للإيحاء بالزخرفة
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .border(
                                width = 0.5.dp,
                                color = Color(0xFFD4AF37).copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .border(
                                width = 0.5.dp,
                                color = Color(0xFFD4AF37).copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    )
                    
                    // الدائرة الذهبية الرئيسية
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

                Spacer(modifier = Modifier.width(8.dp))

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
 
