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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationScreen(
    onNavigateBack: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // مؤقت التسجيل
    LaunchedEffect(isRecording, isPaused) {
        if (isRecording && !isPaused) {
            while (true) {
                delay(1000)
                recordingTime++
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
                        .padding(top = 0.dp, bottom = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📖",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(top = 0.dp, bottom = 16.dp)
                        )
                        Text(
                            text = "سورة الفاتحة",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuranGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "7 آيات • مكية",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 20.sp,
                            color = QuranGreen,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
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
                                isRecording = false
                                recordingTime = 0
                                isPaused = false
                                Toast.makeText(context, "تم حفظ التسميع", Toast.LENGTH_SHORT).show()
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QuranGreen
                        ),
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
                            text = "اقرأ بصوت واضح ومسموع للحصول على أفضل النتائج",
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
