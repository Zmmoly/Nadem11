package awab.quran.ar.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import awab.quran.ar.data.RecitationSettings
import awab.quran.ar.data.RecitationSettingsRepository
import awab.quran.ar.data.ThemeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()
    
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var totalRecitations by remember { mutableStateOf(0) }
    var completedSurahs by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDonationDialog by remember { mutableStateOf(false) }

    val settingsRepo = remember { RecitationSettingsRepository(context) }
    val themeRepo = remember { ThemeRepository(context) }
    val scope = rememberCoroutineScope()
    var settings by remember { mutableStateOf(RecitationSettings()) }

    LaunchedEffect(Unit) {
        settingsRepo.settingsFlow.collect { settings = it }
    }

    LaunchedEffect(Unit) {
        auth.currentUser?.let { user ->
            userEmail = user.email ?: ""
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: ""
                    totalRecitations = document.getLong("totalRecitations")?.toInt() ?: 0
                    completedSurahs = document.getLong("completedSurahs")?.toInt() ?: 0
                }
        }
    }

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val topBarColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val dividerColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFD4C5A9)
    val avatarColor = if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFF6B5744)

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor)
    ) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.app_background),
                contentDescription = "خلفية الملف الشخصي",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(text = "الملف الشخصي", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = titleColor)
                        }
                    },
                    actions = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { showDonationDialog = true }
                                .padding(4.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "تبرع", tint = Color(0xFFE53935), modifier = Modifier.size(22.dp))
                            Text("تبرع", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // صورة المستخدم
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(60.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = avatarColor
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "صورة المستخدم",
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }

                // معلومات المستخدم
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = userName.ifEmpty { "المستخدم" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = subColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = userEmail,
                                fontSize = 14.sp,
                                color = subColor
                            )
                        }
                    }
                }

                // إحصائيات التسميع
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "إحصائيات التسميع",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        
                        StatRow(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.Mic,
                            label = "عدد التسميعات",
                            value = totalRecitations.toString()
                        )
                        
                        Divider(color = dividerColor)
                        
                        StatRow(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.CheckCircle,
                            label = "السور المكتملة",
                            value = completedSurahs.toString()
                        )
                    }
                }

                // خيارات الحساب
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "إعدادات الحساب",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // زر الوضع الليلي
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.NightlightRound else Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFFFFD700) else Color(0xFF6B5744),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isDarkMode) "الوضع الليلي" else "الوضع النهاري",
                                    fontSize = 16.sp,
                                    color = titleColor
                                )
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { enabled ->
                                    onToggleDarkMode(enabled)
                                    scope.launch { themeRepo.setDarkMode(enabled) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFFD700),
                                    checkedTrackColor = Color(0xFF3A3A3A),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD4C5A9)
                                )
                            )
                        }

                        Divider(color = dividerColor)

                        ProfileOption(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.Settings,
                            title = "الإعدادات",
                            onClick = { showSettingsDialog = true }
                        )
                        
                        Divider(color = dividerColor)
                        
                        ProfileOption(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.Info,
                            title = "عن التطبيق",
                            onClick = {
                                Toast.makeText(context, "نديم - تطبيق تسميع القرآن الكريم", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // زر تسجيل الخروج
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC3545)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "تسجيل الخروج",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // حوار تأكيد تسجيل الخروج
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "تسجيل الخروج",
                    color = titleColor
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من تسجيل الخروج؟",
                    color = subColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC3545)
                    )
                ) {
                    Text("تسجيل الخروج", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("إلغاء", color = titleColor)
                }
            },
            containerColor = cardColor
        )
    }

    // Dialog إعدادات التسميع
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "إعدادات التسميع",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4A3F35)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "الأخطاء التي يتم تجاهلها:",
                        fontSize = 14.sp,
                        color = subColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingToggleRow(
                        title = "التشكيل",
                        subtitle = "تجاهل الحركات والتنوين",
                        checked = settings.ignoreTashkeel,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreTashkeel = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )

                    Divider(color = Color(0xFFE0D5C5))

                    SettingToggleRow(
                        title = "حرف الحاء",
                        subtitle = "تجاهل الخلط بين ح و ه",
                        checked = settings.ignoreHaa,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreHaa = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )

                    Divider(color = Color(0xFFE0D5C5))

                    SettingToggleRow(
                        title = "حرف العين",
                        subtitle = "تجاهل الخلط بين ع و أ و ء",
                        checked = settings.ignoreAyn,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreAyn = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )

                    Divider(color = Color(0xFFE0D5C5))

                    SettingToggleRow(
                        title = "المدود",
                        subtitle = "تجاهل أخطاء المد والقصر",
                        checked = settings.ignoreMadd,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreMadd = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )

                    Divider(color = Color(0xFFE0D5C5))

                    SettingToggleRow(
                        title = "مواضع الوقف",
                        subtitle = "تجاهل كلمات الوقف والوصل",
                        checked = settings.ignoreWaqf,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreWaqf = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )

                    Divider(color = Color(0xFFE0D5C5))

                    SettingToggleRow(
                        title = "المساهمة في تحسين الذكاء الاصطناعي",
                        subtitle = "السماح بتخزين تسجيلاتك الصوتية للمساعدة في تدريب نموذج الذكاء الاصطناعي",
                        checked = settings.allowAudioStorage,
                        onCheckedChange = {
                            val updated = settings.copy(allowAudioStorage = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = avatarColor)
                ) {
                    Text("حفظ", color = Color.White)
                }
            },
            containerColor = cardColor
        )
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
                    Text("اختر طريقة التبرع:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744))
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
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                        showDonationDialog = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                                    border = BorderStroke(1.5.dp, color)
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

@Composable
fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDarkMode: Boolean = false
) {
    val color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = color
            )
        }
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val tColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val sColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = tColor
            )
        }
        
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "فتح",
                tint = sColor
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A3F35)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF9E8E7E)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6B5744),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD4C5A9)
            )
        )
    }
}
