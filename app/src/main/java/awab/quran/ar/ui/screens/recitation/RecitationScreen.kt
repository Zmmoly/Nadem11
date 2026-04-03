package awab.quran.ar.ui.screens.recitation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.background
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
fun RecitationScreen(onNavigateBack: () -> Unit, isDarkMode: Boolean = false) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }
    var transcribedLines by remember { mutableStateOf(listOf<String>()) }
    var statusMessage by remember { mutableStateOf("") }
    var showDonationDialog by remember { mutableStateOf(false) }
    var activeModel by remember { mutableStateOf("") } // "modal" أو "deepgram"
    var isConnecting by remember { mutableStateOf(false) } // حالة الاتصال الأولية

    // ألوان
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val topBarColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.7f)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF3D2B1F)
    val hintCardColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE5DFCF).copy(alpha = 0.7f)
    val micIdleColor = if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFF6B5744).copy(alpha = 0.1f)

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

            onModelChanged = { model ->
                activeModel = model
                isConnecting = false
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

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (!isDarkMode) {
            Image(painter = painterResource(id = R.drawable.app_background), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("تسميع القرآن", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor) },
                    navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = titleColor) } },
                    actions = {
                        IconButton(onClick = { showDonationDialog = true }) {
                            Icon(Icons.Default.Favorite, contentDescription = "تبرع", tint = Color(0xFFE53935))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // بطاقة الميكروفون
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(shape = RoundedCornerShape(60.dp), modifier = Modifier.size(120.dp), color = if (isRecording) Color(0xFFDC3545).copy(alpha = 0.15f) else micIdleColor) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAnalyzing) Icons.Default.HourglassEmpty else Icons.Default.Mic,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = if (isRecording) Color(0xFFDC3545) else titleColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isRecording) {
                            Text(text = String.format("%02d:%02d", recordingSeconds / 60, recordingSeconds % 60), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = titleColor)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (statusMessage.isNotEmpty()) {
                            Text(text = statusMessage, fontSize = 14.sp, color = subColor, modifier = Modifier.padding(bottom = 16.dp))
                        }

                        Button(
                            onClick = {
                                if (isRecording) {
                                    service.stopRecitation(); isRecording = false; statusMessage = ""; activeModel = ""; isConnecting = false
                                } else {
                                    transcribedLines = listOf(); isConnecting = true; service.startRecitation(); isRecording = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color(0xFFDC3545) else if (isDarkMode) Color(0xFF4A7C59) else Color(0xFF6B5744)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = Color.White)
                            Text(text = if (isRecording) "إيقاف التسميع" else "ابدأ التسميع", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // مؤشر النموذج النشط
                        if (activeModel.isNotEmpty() || isConnecting) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    isConnecting -> Color(0xFF757575).copy(alpha = 0.15f)
                                    activeModel == "modal" -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    else -> Color(0xFF1565C0).copy(alpha = 0.15f)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = when {
                                                    isConnecting -> Color(0xFF9E9E9E)
                                                    activeModel == "modal" -> Color(0xFF43A047)
                                                    else -> Color(0xFF1E88E5)
                                                },
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            isConnecting -> "جاري الاتصال بالنموذج..."
                                            activeModel == "modal" -> "نموذج القرآن (Modal)"
                                            else -> "Deepgram Nova-3"
                                        },
                                        fontSize = 12.sp,
                                        color = when {
                                            isConnecting -> Color(0xFF9E9E9E)
                                            activeModel == "modal" -> Color(0xFF43A047)
                                            else -> Color(0xFF1E88E5)
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // بطاقة النص
                if (transcribedLines.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Text(text = "النص المُسمَّع", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = titleColor, modifier = Modifier.padding(bottom = 12.dp))
                            transcribedLines.forEach { line ->
                                Text(text = line, fontSize = 22.sp, color = textColor, textAlign = TextAlign.Right, lineHeight = 36.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                                Divider(color = titleColor.copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                // نصيحة
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = hintCardColor)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = titleColor, modifier = Modifier.size(32.dp).padding(end = 12.dp))
                        Text(text = "تأكد من وجودك في مكان هادئ للحصول على أفضل نتيجة", fontSize = 14.sp, color = titleColor, lineHeight = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // نافذة التبرع
    if (showDonationDialog) {
        AlertDialog(
            onDismissRequest = { showDonationDialog = false },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFFF8F0),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ادعم تطوير التطبيق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "جزاك الله خيرًا على اهتمامك بدعم هذا المشروع القرآني الكريم. تبرعك يساعدنا على تطوير التطبيق وخدمة أكبر عدد من المسلمين.",
                        fontSize = 14.sp,
                        color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "اختر طريقة التبرع:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val donationUrl = "https://zmmoly.github.io/Nadem/nadeem-website.html#contact"
                    val donationOptions = listOf(
                        Triple("5 ريال", donationUrl, Color(0xFF4CAF50)),
                        Triple("10 ريال", donationUrl, Color(0xFF2196F3)),
                        Triple("20 ريال", donationUrl, Color(0xFF9C27B0)),
                        Triple("مبلغ آخر", donationUrl, Color(0xFFE53935))
                    )
                    donationOptions.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { (label, url, color) ->
                                val ctx = context
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        ctx.startActivity(intent)
                                        showDonationDialog = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDonationDialog = false }) {
                    Text("إغلاق", color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744))
                }
            }
        )
    }
}
