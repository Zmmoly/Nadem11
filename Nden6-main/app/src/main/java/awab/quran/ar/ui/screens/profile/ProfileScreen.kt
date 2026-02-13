package awab.quran.ar.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.ui.theme.QuranGreen
import awab.quran.ar.ui.theme.QuranGold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
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

    // جلب بيانات المستخدم
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

    // حوار تأكيد تسجيل الخروج
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تسجيل الخروج") },
            text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        showLogoutDialog = false
                        onLogout()
                        Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("نعم", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("إلغاء", color = QuranGreen)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الملف الشخصي") },
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
                .verticalScroll(scrollState)
        ) {
            // القسم العلوي - معلومات المستخدم
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = QuranGreen
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // صورة المستخدم
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "👤",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuranGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userName.ifEmpty { "مستخدم" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // بطاقات الإحصائيات
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "التسميعات",
                    value = "$totalRecitations",
                    icon = Icons.Default.CheckCircle,
                    color = QuranGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "السور المكتملة",
                    value = "$completedSurahs",
                    icon = Icons.Default.Star,
                    color = QuranGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // إعدادات الحساب
            Text(
                text = "إعدادات الحساب",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = QuranGreen,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "تعديل الملف الشخصي",
                        onClick = {
                            Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        title = "تغيير كلمة المرور",
                        onClick = {
                            Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "الإشعارات",
                        onClick = {
                            Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // المزيد
            Text(
                text = "المزيد",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = QuranGreen,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "عن التطبيق",
                        onClick = {
                            Toast.makeText(context, "نديم - تطبيق تسميع القرآن الكريم", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Share,
                        title = "مشاركة التطبيق",
                        onClick = {
                            Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "الإعدادات",
                        onClick = {
                            Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر تسجيل الخروج
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسجيل الخروج",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // معلومات التطبيق
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "نديم v1.0",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QuranGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
