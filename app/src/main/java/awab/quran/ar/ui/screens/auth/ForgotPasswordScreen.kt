package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    isDarkMode: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var emailSent by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val borderFocused = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF8B7355)
    val borderUnfocused = if (isDarkMode) Color(0xFF444444) else Color(0xFFD4C5A9)
    val fieldText = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val btnColor = if (isDarkMode) Color(0xFF4A7C59) else Color(0xFF6B5744)

    fun sendResetEmail() {
        if (email.isBlank()) {
            emailError = "الرجاء إدخال البريد الإلكتروني"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "البريد الإلكتروني غير صحيح"
            return
        }

        isLoading = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    // ✅ الإيميل مسجل وتم الإرسال
                    emailSent = true
                } else {
                    // ✅ ترجمة الأخطاء لرسائل عربية واضحة
                    val errorMsg = task.exception?.message ?: ""
                    emailError = when {
                        errorMsg.contains("USER_NOT_FOUND") ||
                        errorMsg.contains("no user record") ||
                        errorMsg.contains("INVALID_EMAIL") ->
                            "لا يوجد حساب مسجل بهذا البريد الإلكتروني"

                        errorMsg.contains("too many requests") ->
                            "محاولات كثيرة جداً، حاول بعد قليل"

                        errorMsg.contains("network") ||
                        errorMsg.contains("NETWORK") ->
                            "تحقق من اتصالك بالإنترنت"

                        else ->
                            "حدث خطأ، حاول مرة أخرى"
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.app_background),
                contentDescription = "خلفية",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "رجوع",
                tint = titleColor
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🔐", fontSize = 80.sp, modifier = Modifier.padding(bottom = 24.dp))

            Text(
                text = "نسيت كلمة المرور؟",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "أدخل بريدك الإلكتروني المسجل وسنرسل لك رابط إعادة تعيين كلمة المرور",
                fontSize = 14.sp,
                color = subColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!emailSent) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; emailError = null },
                            label = { Text("البريد الإلكتروني", color = subColor) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = subColor
                                )
                            },
                            isError = emailError != null,
                            supportingText = {
                                emailError?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus(); sendResetEmail() }
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = borderFocused,
                                unfocusedBorderColor = borderUnfocused,
                                focusedLabelColor = borderFocused,
                                unfocusedLabelColor = subColor,
                                cursorColor = borderFocused,
                                focusedTextColor = fieldText,
                                unfocusedTextColor = fieldText,
                                focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.Unspecified,
                                unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.Unspecified
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Button(
                            onClick = { sendResetEmail() },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "إرسال رابط إعادة التعيين",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // ✅ شاشة النجاح بعد الإرسال
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).padding(bottom = 16.dp),
                            tint = titleColor
                        )
                        Text(
                            text = "تم إرسال البريد!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني. يرجى التحقق من صندوق الوارد والبريد المزعج.",
                            fontSize = 14.sp,
                            color = subColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = titleColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "العودة لتسجيل الدخول",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
