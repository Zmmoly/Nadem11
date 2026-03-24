package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import awab.quran.ar.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showResendButton by remember { mutableStateOf(false) }
    var resendLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(
            data = result.data,
            onSuccess = { onLoginSuccess() },
            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
        )
    }

    fun performLogin() {
        if (!validateInputs(email, password,
                onEmailError = { emailError = it },
                onPasswordError = { passwordError = it }
            )) return

        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && !user.isEmailVerified) {
                        // ✅ الحساب غير مُفعَّل — أخرج المستخدم وأخبره
                        auth.signOut()
                        passwordError = "يرجى تفعيل حسابك عبر البريد الإلكتروني أولاً"
                        showResendButton = true
                        Toast.makeText(
                            context,
                            "لم يتم تفعيل حسابك بعد، تحقق من بريدك",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                } else {
                    val errorMsg = task.exception?.message ?: ""
                    val arabicError = when {
                        errorMsg.contains("credential is incorrect") ||
                        errorMsg.contains("INVALID_LOGIN_CREDENTIALS") ||
                        errorMsg.contains("INVALID_PASSWORD") ||
                        errorMsg.contains("wrong password") ->
                            "كلمة المرور غير صحيحة"

                        errorMsg.contains("no user record") ||
                        errorMsg.contains("USER_NOT_FOUND") ->
                            "لا يوجد حساب بهذا البريد الإلكتروني"

                        errorMsg.contains("user has been disabled") ->
                            "هذا الحساب موقوف، تواصل مع الدعم"

                        errorMsg.contains("too many requests") ->
                            "محاولات كثيرة جداً، حاول بعد قليل"

                        errorMsg.contains("network") ||
                        errorMsg.contains("NETWORK") ->
                            "تحقق من اتصالك بالإنترنت"

                        else ->
                            "حدث خطأ، حاول مرة أخرى"
                    }
                    passwordError = arabicError
                }
            }
    }

    // ألوان الوضع الليلي
    val bgColor       = Color(0xFF121212)
    val cardColor     = Color(0xFF1E1E1E)
    val fieldBg       = Color(0xFF2C2C2C)
    val titleColor    = Color(0xFFE0E0E0)
    val subColor      = Color(0xFFAAAAAA)
    val iconColor     = Color(0xFFD4AF37)
    val accentColor   = Color(0xFFD4AF37)
    val hintColor     = Color(0xFF888888)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        TwinklingStars()

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "لوغو نديم",
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "نديم",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "تحقق من حفظك للقرآن الكريم",
                fontSize = 14.sp,
                color = subColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = email, onValueChange = { email = it; emailError = null },
                        placeholder = { Text("البريد الإلكتروني", color = hintColor, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = iconColor, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color(0xFF444444),
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedTextColor = titleColor,
                            unfocusedTextColor = titleColor,
                            cursorColor = accentColor
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it; passwordError = null },
                        placeholder = { Text("••••••••", color = hintColor, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = iconColor, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = iconColor, modifier = Modifier.size(22.dp))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); performLogin() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color(0xFF444444),
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedTextColor = titleColor,
                            unfocusedTextColor = titleColor,
                            cursorColor = accentColor
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    if (passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                    TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                        Text("• نسيت كلمة المرور؟", color = accentColor, fontSize = 13.sp)
                    }

                    if (showResendButton) {
                        TextButton(
                            onClick = {
                                resendLoading = true
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        auth.currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener {
                                                auth.signOut()
                                                resendLoading = false
                                                Toast.makeText(context, "تم إرسال رابط التفعيل إلى بريدك ✉️", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        resendLoading = false
                                        Toast.makeText(context, "تأكد من صحة البيانات أولاً", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            if (resendLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = accentColor, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("• إعادة إرسال رابط التفعيل", color = accentColor, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { performLogin() }, enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF121212))
                        else Text("تسجيل الدخول", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("أو تابع التسجيل بإستخدام", fontSize = 13.sp, color = subColor, modifier = Modifier.padding(vertical = 16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                SocialButton(R.drawable.ic_google, "Google") {
                    googleSignInLauncher.launch(getGoogleSignInIntent(context))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("ليس لديك حساب؟ ", fontSize = 14.sp, color = subColor)
                Text("إنشاء حساب", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accentColor, modifier = Modifier.clickable { onNavigateToRegister() })
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SocialButton(iconRes: Int, name: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        color = Color(0xFFF5F2EA),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = iconRes), contentDescription = name, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun MicButton() {
    Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = Color(0xFF6B5744), shadowElevation = 8.dp) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Mic, "الميكروفون", tint = Color(0xFFF5E6D3), modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun TwinklingStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = ""
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(Color.White.copy(alpha = alpha), radius = 3f, center = Offset(size.width * 0.2f, size.height * 0.15f))
        drawCircle(Color.White.copy(alpha = alpha), radius = 3f, center = Offset(size.width * 0.8f, size.height * 0.2f))
    }
}

private fun validateInputs(email: String, password: String, onEmailError: (String?) -> Unit, onPasswordError: (String?) -> Unit): Boolean {
    var isValid = true
    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("البريد الإلكتروني غير صحيح"); isValid = false
    } else onEmailError(null)
    if (password.length < 6) {
        onPasswordError("يجب أن تكون 6 أحرف على الأقل"); isValid = false
    } else onPasswordError(null)
    return isValid
}
