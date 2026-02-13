package awab.quran.ar.ui.screens.recitation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // صورة الخلفية
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = "خلفية التسميع",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "تسميع القرآن",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B5744)
                        )
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
                        containerColor = Color(0xFFF5F3ED).copy(alpha = 0.95f)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // بطاقة اختيار السورة
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F3ED).copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "اختر السورة للتسميع",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B5744),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // قائمة منسدلة للسور (مبسطة)
                        Button(
                            onClick = {
                                Toast.makeText(context, "اختر السورة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B5744)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "اختر سورة",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // بطاقة التسجيل
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F3ED).copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // أيقونة الميكروفون
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .padding(bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(60.dp),
                                color = if (isRecording) 
                                    Color(0xFFDC3545).copy(alpha = 0.2f)
                                else 
                                    Color(0xFF6B5744).copy(alpha = 0.1f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "ميكروفون",
                                        modifier = Modifier.size(60.dp),
                                        tint = if (isRecording) 
                                            Color(0xFFDC3545)
                                        else 
                                            Color(0xFF6B5744)
                                    )
                                }
                            }
                        }

                        // وقت التسجيل
                        if (isRecording) {
                            Text(
                                text = String.format("%02d:%02d", recordingTime / 60, recordingTime % 60),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B5744),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // زر بدء/إيقاف التسجيل
                        Button(
                            onClick = {
                                isRecording = !isRecording
                                if (isRecording) {
                                    Toast.makeText(context, "بدأ التسجيل", Toast.LENGTH_SHORT).show()
                                } else {
                                    recordingTime = 0
                                    Toast.makeText(context, "تم إيقاف التسجيل", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) 
                                    Color(0xFFDC3545)
                                else 
                                    Color(0xFF6B5744)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = if (isRecording) 
                                    Icons.Default.Stop
                                else 
                                    Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp),
                                tint = Color.White
                            )
                            Text(
                                text = if (isRecording) "إيقاف التسجيل" else "ابدأ التسميع",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // نصيحة
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE5DFCF).copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF6B5744),
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = "تأكد من وجودك في مكان هادئ للحصول على أفضل نتيجة",
                            fontSize = 14.sp,
                            color = Color(0xFF6B5744),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
