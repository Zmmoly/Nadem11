package awab.quran.ar.ui.screens.recitation

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
import awab.quran.ar.services.DeepgramService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }
    var transcribedLines by remember { mutableStateOf(listOf<String>()) }
    var statusMessage by remember { mutableStateOf("") }

    // إنشاء الخدمة
    val service = remember {
        DeepgramService(context).apply {

            onConnectionEstablished = {
                statusMessage = "جاري الاستماع..."
            }

            onTranscriptionReceived = { text ->
                // إضافة كل آية في سطر جديد
                transcribedLines = transcribedLines + text
                isAnalyzing = false
                statusMessage = "جاري الاستماع..."
            }

            onInterimTranscription = {
                isAnalyzing = true
                statusMessage = "جاري التحليل..."
            }

            onError = { error ->
                isAnalyzing = false
                statusMessage = "خطأ: $error"
            }
        }
    }

    // مؤقت التسجيل
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingSeconds++
            }
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
                        Text(
                            "تسميع القرآن",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B5744)
                        )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ── بطاقة الميكروفون ──
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
                        Surface(
                            shape = RoundedCornerShape(60.dp),
                            modifier = Modifier.size(120.dp),
                            color = if (isRecording)
                                Color(0xFFDC3545).copy(alpha = 0.15f)
                            else
                                Color(0xFF6B5744).copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAnalyzing)
                                        Icons.Default.HourglassEmpty
                                    else
                                        Icons.Default.Mic,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = if (isRecording)
                                        Color(0xFFDC3545)
                                    else
                                        Color(0xFF6B5744)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // وقت التسجيل
                        if (isRecording) {
                            Text(
                                text = String.format(
                                    "%02d:%02d",
                                    recordingSeconds / 60,
                                    recordingSeconds % 60
                                ),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B5744)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // حالة النظام
                        if (statusMessage.isNotEmpty()) {
                            Text(
                                text = statusMessage,
                                fontSize = 14.sp,
                                color = Color(0xFF6B5744).copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // زر البدء/الإيقاف
                        Button(
                            onClick = {
                                if (isRecording) {
                                    service.stopRecitation()
                                    isRecording = false
                                    statusMessage = ""
                                } else {
                                    transcribedLines = listOf()
                                    service.startRecitation()
                                    isRecording = true
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
                                text = if (isRecording) "إيقاف التسميع" else "ابدأ التسميع",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // ── بطاقة النص المُخرَج ──
                if (transcribedLines.isNotEmpty()) {
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
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "النص المُسمَّع",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B5744),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            transcribedLines.forEach { line ->
                                Text(
                                    text = line,
                                    fontSize = 22.sp,
                                    color = Color(0xFF3D2B1F),
                                    textAlign = TextAlign.Right,
                                    lineHeight = 36.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                                Divider(color = Color(0xFF6B5744).copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                // ── نصيحة ──
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
                            Icons.Default.Lightbulb,
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
