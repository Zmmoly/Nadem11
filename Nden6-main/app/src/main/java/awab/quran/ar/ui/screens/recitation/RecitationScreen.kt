package awab.quran.ar.ui.screens.recitation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.ui.theme.QuranGreen
import awab.quran.ar.ui.theme.QuranGold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationScreen(
    surahName: String = "الفاتحة",
    totalVerses: Int = 7,
    onNavigateBack: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    // الآية الحالية التي يسمّعها الطالب
    var currentVerse by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // هل أكمل المستخدم آخر آية؟
    val isSurahCompleted = currentVerse > totalVerses

    // مؤقت التسجيل
    LaunchedEffect(isRecording, isPaused) {
        if (isRecording && !isPaused) {
            while (true) {
                delay(1000)
                recordingTime++
            }
        }
    }

    // دالة تحديث Firestore عند إيقاف التسميع
    fun saveRecitationToFirestore(surahCompleted: Boolean) {
        auth.currentUser?.let { user ->
            val userRef = firestore.collection("users").document(user.uid)
            val updates = mutableMapOf<String, Any>(
                "totalRecitations" to FieldValue.increment(1)
            )
            if (surahCompleted) {
                updates["completedSurahs"] = FieldValue.increment(1)
            }
            userRef.update(updates)
                .addOnFailureListener {
                    // إنشاء الوثيقة إذا لم تكن موجودة
                    userRef.set(
                        mapOf(
                            "totalRecitations" to 1,
                            "completedSurahs" to if (surahCompleted) 1 else 0
                        ),
                        SetOptions.merge()
                    )
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التسميع") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuranGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // بطاقة معلومات السورة
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📖", fontSize = 48.sp, modifier = Modifier.padding(bottom = 16.dp))
                        Text(
                            text = "سورة $surahName",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuranGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalVerses آيات",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // عداد الآيات
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الآية الحالية",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            // أزرار تغيير الآية
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (currentVerse > 1) currentVerse-- },
                                    enabled = currentVerse > 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "السابقة",
                                        tint = if (currentVerse > 1) QuranGreen else Color.LightGray
                                    )
                                }
                                Text(
                                    text = if (isSurahCompleted) "✅" else "$currentVerse / $totalVerses",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSurahCompleted) QuranGold else QuranGreen
                                )
                                IconButton(
                                    onClick = { if (currentVerse <= totalVerses) currentVerse++ },
                                    enabled = currentVerse <= totalVerses
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "التالية",
                                        tint = if (currentVerse <= totalVerses) QuranGreen else Color.LightGray
                                    )
                                }
                            }
                        }

                        // شريط تقدم الآيات
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (currentVerse - 1).toFloat() / totalVerses.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (isSurahCompleted) QuranGold else QuranGreen,
                            trackColor = Color.LightGray.copy(alpha = 0.4f)
                        )

                        if (isSurahCompleted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎉 أكملت السورة!",
                                fontSize = 14.sp,
                                color = QuranGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // دائرة التسجيل الكبيرة
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            color = if (isRecording && !isPaused)
                                QuranGreen.copy(alpha = 0.2f)
                            else
                                Color.Gray.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(
                                color = if (isRecording && !isPaused)
                                    QuranGreen
                                else
                                    Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording && !isPaused)
                                Icons.Default.Mic
                            else
                                Icons.Default.MicOff,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // عرض وقت التسجيل
                if (isRecording) {
                    Text(
                        text = formatTime(recordingTime),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuranGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isPaused) "متوقف مؤقتاً" else "جاري التسجيل...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "اضغط على الميكروفون للبدء",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            // أزرار التحكم
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isRecording) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // زر الإيقاف المؤقت / الاستئناف
                        FloatingActionButton(
                            onClick = { isPaused = !isPaused },
                            containerColor = QuranGold,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = if (isPaused)
                                    Icons.Default.PlayArrow
                                else
                                    Icons.Default.Pause,
                                contentDescription = if (isPaused) "Resume" else "Pause",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // زر إيقاف التسجيل
                        FloatingActionButton(
                            onClick = {
                                val surahCompleted = isSurahCompleted
                                isRecording = false
                                recordingTime = 0
                                isPaused = false

                                // حفظ في Firestore
                                saveRecitationToFirestore(surahCompleted)

                                val message = if (surahCompleted)
                                    "🎉 أحسنت! تم حفظ السورة المكتملة"
                                else
                                    "✓ تم حفظ التسميع (الآية $currentVerse من $totalVerses)"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            containerColor = Color.Red,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    // زر بدء التسجيل
                    Button(
                        onClick = {
                            isRecording = true
                            recordingTime = 0
                            Toast.makeText(context, "بدأ التسميع", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = QuranGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "ابدأ التسميع",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // نصائح
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = QuranGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = QuranGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "حرّك عداد الآيات عند الانتقال لكل آية جديدة",
                            fontSize = 14.sp,
                            color = QuranGreen
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
