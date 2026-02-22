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

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()

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
                    Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "فشل تسجيل الدخول: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "خلفية تسجيل الدخول",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        TwinklingStars()

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            QuranBookIcon()

            Text("تسميع القرآن", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B5744), modifier = Modifier.padding(bottom = 12.dp))
            Text("تحفّظوا من حفظك القرآن الكريم الذكّاء الكريم.", fontSize = 14.sp, color = Color(0xFF8B7355), textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text("الذكاء الصّطناعي.", fontSize = 14.sp, color = Color(0xFF8B7355), textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE6DC).copy(alpha = 0.75f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = email, onValueChange = { email = it; emailError = null },
                        placeholder = { Text("البريد الإلكتروني", color = Color(0xFFB5A590), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, "أيقونة البريد", tint = Color(0xFFB5A590), modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF5F2EA), unfocusedContainerColor = Color(0xFFF5F2EA)),
                        shape = RoundedCornerShape(28.dp)
                    )
                    if (emailError != null) Text(text = emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it; passwordError = null },
                        placeholder = { Text("••••••••", color = Color(0xFFB5A590), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, "أيقونة القفل", tint = Color(0xFFB5A590), modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "إظهار كلمة المرور", tint = Color(0xFFB5A590), modifier = Modifier.size(22.dp))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); performLogin() }),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF5F2EA), unfocusedContainerColor = Color(0xFFF5F2EA)),
                        shape = RoundedCornerShape(28.dp)
                    )
                    if (passwordError != null) Text(text = passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                    TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                        Text("• نسيت كلمة المرور؟", color = Color(0xFF9B8B7A), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().height(54.dp).background(brush = Brush.linearGradient(colors = listOf(Color(0xFFD4C3A8), Color(0xFFC9B897), Color(0xFFD4C3A8))), shape = RoundedCornerShape(27.dp)))
                        Button(
                            onClick = { performLogin() }, enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().padding(2.dp).height(50.dp).align(Alignment.Center),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D7B62)),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            else Text("تسجيل الدخول", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("أو تابع التسجيل بإستخدام", fontSize = 13.sp, color = Color(0xFF9B8B7A), modifier = Modifier.padding(vertical = 16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                // زر Google - مفعّل
                SocialButton(R.drawable.ic_google, "Google") {
                    signInWithGoogle(
                        context = context,
                        coroutineScope = coroutineScope,
                        onSuccess = { onLoginSuccess() },
                        onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // زر Apple - غير مفعّل
                SocialButton(R.drawable.ic_apple, "Apple") {
                    Toast.makeText(context, "تسجيل الدخول بـ Apple غير متاح حالياً", Toast.LENGTH_SHORT).show()
                }
                Spacer(modifier = Modifier.width(12.dp))
                // زر Facebook - غير مفعّل
                SocialButton(R.drawable.ic_facebook, "Facebook") {
                    Toast.makeText(context, "تسجيل الدخول بـ Facebook غير متاح حالياً", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("ليس لديك حساب؟ ", fontSize = 14.sp, color = Color(0xFF8B7355))
                Text("إنشاء حساب", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A7C59), modifier = Modifier.clickable { onNavigateToRegister() })
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SocialButton(iconRes: Int, name: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(52.dp).clickable { onClick() },
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
fun QuranBookIcon() {
    Canvas(modifier = Modifier.size(90.dp).padding(bottom = 16.dp)) {
        val color = Color(0xFF8B7355)
        val path = Path().apply {
            moveTo(size.width * 0.25f, size.height * 0.25f)
            lineTo(size.width * 0.5f, size.height * 0.2f)
            lineTo(size.width * 0.5f, size.height * 0.8f)
            lineTo(size.width * 0.25f, size.height * 0.75f)
            close()
            moveTo(size.width * 0.5f, size.height * 0.2f)
            lineTo(size.width * 0.75f, size.height * 0.25f)
            lineTo(size.width * 0.75f, size.height * 0.75f)
            lineTo(size.width * 0.5f, size.height * 0.8f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 2.5f))
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
