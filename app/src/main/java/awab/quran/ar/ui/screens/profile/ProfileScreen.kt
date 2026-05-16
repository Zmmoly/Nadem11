package awab.quran.ar.ui.screens.profile

import android.app.Activity
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import awab.quran.ar.data.RecitationSettings
import awab.quran.ar.data.RecitationSettingsRepository
import awab.quran.ar.data.ThemeRepository
import awab.quran.ar.utils.LocaleHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import android.content.Intent
import awab.quran.ar.services.AudioRecordingManager
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val recordingManager = remember { AudioRecordingManager(context) }
    var showRecordingsList by remember { mutableStateOf(false) }
    var allRecordings by remember { mutableStateOf(listOf<File>()) }
    var recordingToDelete by remember { mutableStateOf<File?>(null) }
    var showDonationDialog by remember { mutableStateOf(false) }

    // ── Language selector state ──
    var showLanguageDialog by remember { mutableStateOf(false) }
    val languages = listOf(
        Pair("العربية", "ar"),
        Pair("Bahasa Indonesia", "in"),
        Pair("English", "en")
    )
    var selectedLanguage by remember {
        mutableStateOf(
            LocaleHelper.getSavedLanguage(context).let { saved ->
                languages.find { it.second == saved }?.first ?: "العربية"
            }
        )
    }

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
                contentDescription = stringResource(R.string.profile_background_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.profile),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = titleColor
                            )
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
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = stringResource(R.string.donate),
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                stringResource(R.string.donate),
                                fontSize = 10.sp,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold
                            )
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
                    colors = CardDefaults.cardColors(containerColor = avatarColor)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.user_avatar_desc),
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }

                // معلومات المستخدم
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = userName.ifEmpty { stringResource(R.string.user) },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = subColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = userEmail, fontSize = 14.sp, color = subColor)
                        }
                    }
                }

                // إحصائيات التسميع + اختيار اللغة
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.recitation_statistics),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )

                        StatRow(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.Mic,
                            label = stringResource(R.string.microphone),
                            value = totalRecitations.toString()
                        )

                        Divider(color = dividerColor)

                        // ── خيار اللغة ──
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLanguageDialog = true },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.language),
                                        fontSize = 16.sp,
                                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
                                    )
                                    Text(
                                        text = selectedLanguage,
                                        fontSize = 13.sp,
                                        color = subColor
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = subColor
                            )
                        }
                    }
                }

                // خيارات الحساب
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.account_settings),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // زر الوضع الليلي
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                                    text = if (isDarkMode) stringResource(R.string.dark_mode) else stringResource(R.string.light_mode),
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
                            title = stringResource(R.string.recitation_settings),
                            onClick = { showSettingsDialog = true }
                        )

                        Divider(color = dividerColor)

                        ProfileOption(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.FolderOpen,
                            title = stringResource(R.string.my_recordings),
                            onClick = {
                                allRecordings = recordingManager.getAllRecordings()
                                showRecordingsList = true
                            }
                        )

                        Divider(color = dividerColor)

                        ProfileOption(
                            isDarkMode = isDarkMode,
                            icon = Icons.Default.Info,
                            title = stringResource(R.string.about_app),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://zmmoly.github.io/Nadem/nadeem-website.html"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                // زر تسجيل الخروج
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.logout),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // ── نافذة اختيار اللغة ──
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = cardColor,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = titleColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.choose_language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = titleColor
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    languages.forEach { (name, code) ->
                        val isSelected = selectedLanguage == name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = name
                                    LocaleHelper.saveLanguage(context, code)
                                    showLanguageDialog = false
                                    val activity = context as Activity
                                    activity.recreate()
                                }
                                .background(
                                    if (isSelected) avatarColor.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                color = if (isSelected) avatarColor else titleColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = avatarColor, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (name != languages.last().first) Divider(color = dividerColor)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.close), color = subColor)
                }
            }
        )
    }

    // حوار تأكيد تسجيل الخروج
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = stringResource(R.string.logout), color = titleColor) },
            text = { Text(text = stringResource(R.string.logout_confirm), color = subColor) },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                ) {
                    Text(stringResource(R.string.logout), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel), color = titleColor)
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
                    text = stringResource(R.string.recitation_settings),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4A3F35)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.ignored_errors),
                        fontSize = 14.sp,
                        color = subColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SettingToggleRow(
                        title = stringResource(R.string.diacritics),
                        subtitle = stringResource(R.string.ignore_harakat_errors),
                        checked = settings.ignoreTashkeel,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreTashkeel = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                    Divider(color = Color(0xFFE0D5C5))
                    SettingToggleRow(
                        title = stringResource(R.string.letter_ha),
                        subtitle = stringResource(R.string.ignore_ha_he_confusion),
                        checked = settings.ignoreHaa,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreHaa = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                    Divider(color = Color(0xFFE0D5C5))
                    SettingToggleRow(
                        title = stringResource(R.string.letter_ain),
                        subtitle = stringResource(R.string.ignore_ain_hamza_confusion),
                        checked = settings.ignoreAyn,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreAyn = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                    Divider(color = Color(0xFFE0D5C5))
                    SettingToggleRow(
                        title = stringResource(R.string.madd),
                        subtitle = stringResource(R.string.ignore_madd_errors),
                        checked = settings.ignoreMadd,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreMadd = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                    Divider(color = Color(0xFFE0D5C5))
                    SettingToggleRow(
                        title = stringResource(R.string.waqf_positions),
                        subtitle = stringResource(R.string.ignore_waqf_wasl_errors),
                        checked = settings.ignoreWaqf,
                        onCheckedChange = {
                            val updated = settings.copy(ignoreWaqf = it)
                            settings = updated
                            scope.launch { settingsRepo.save(updated) }
                        }
                    )
                    Divider(color = Color(0xFFE0D5C5))
                    SettingToggleRow(
                        title = stringResource(R.string.contribute_to_ai),
                        subtitle = stringResource(R.string.allow_audio_storage),
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
                    Text(stringResource(R.string.save), color = Color.White)
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
                        stringResource(R.string.support_app_development),
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
                        stringResource(R.string.donation_message),
                        fontSize = 14.sp,
                        color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.choose_donation_method),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val donationUrl = "https://zmmoly.github.io/Nadem/nadeem-website.html"
                    val donationOptions = listOf(
                        Triple(stringResource(R.string.donation_5), donationUrl, Color(0xFF4CAF50)),
                        Triple(stringResource(R.string.donation_10), donationUrl, Color(0xFF2196F3)),
                        Triple(stringResource(R.string.donation_20), donationUrl, Color(0xFF9C27B0)),
                        Triple(stringResource(R.string.donation_other), donationUrl, Color(0xFFE53935))
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
                    Text(stringResource(R.string.close), color = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF6B5744))
                }
            }
        )
    }

    // ── نافذة قائمة التسجيلات ──
    if (showRecordingsList) {
        AlertDialog(
            onDismissRequest = { showRecordingsList = false },
            containerColor = Color(0xFFFFF8F0),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color(0xFF6B5744), modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.my_recordings),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4A3F35)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (allRecordings.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎙", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(stringResource(R.string.no_recordings_yet), color = Color(0xFF8B7355), fontSize = 14.sp)
                            }
                        }
                    } else {
                        allRecordings.forEach { file ->
                            val dateStr = SimpleDateFormat("dd/MM/yyyy  HH:mm", java.util.Locale.getDefault()).format(Date(file.lastModified()))
                            val sizeKb = file.length() / 1024
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9DF))
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(file.nameWithoutExtension, fontSize = 12.sp, color = Color(0xFF3D2B1F), fontWeight = FontWeight.Bold)
                                        Text("$dateStr  •  ${sizeKb} KB", fontSize = 10.sp, color = Color(0xFF8B7355))
                                    }
                                    IconButton(onClick = { recordingManager.playRecording(file) }) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.play_recording), tint = Color(0xFF6B5744))
                                    }
                                    IconButton(onClick = {
                                        val intent = recordingManager.shareRecording(file)
                                        val shareTitle = context.getString(R.string.share_recording)
                                        context.startActivity(Intent.createChooser(intent, shareTitle).also { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_recording), tint = Color(0xFF6B5744))
                                    }
                                    IconButton(onClick = { recordingToDelete = file }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_recording), tint = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRecordingsList = false }) {
                    Text(stringResource(R.string.close), color = Color(0xFF6B5744))
                }
            }
        )
    }

    // ── تأكيد الحذف ──
    recordingToDelete?.let { file ->
        AlertDialog(
            onDismissRequest = { recordingToDelete = null },
            containerColor = Color(0xFFFFF8F0),
            title = { Text(stringResource(R.string.delete_recording), fontWeight = FontWeight.Bold, color = Color(0xFF4A3F35)) },
            text = { Text(stringResource(R.string.delete_recording_confirm, file.nameWithoutExtension), color = Color(0xFF8B7355)) },
            confirmButton = {
                TextButton(onClick = {
                    recordingManager.deleteRecording(file)
                    allRecordings = recordingManager.getAllRecordings()
                    recordingToDelete = null
                }) { Text(stringResource(R.string.delete), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { recordingToDelete = null }) {
                    Text(stringResource(R.string.cancel), color = Color(0xFF8B7355))
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 16.sp, color = color)
        }
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 16.sp, color = tColor)
        }
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = stringResource(R.string.open), tint = sColor)
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4A3F35))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF9E8E7E))
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
