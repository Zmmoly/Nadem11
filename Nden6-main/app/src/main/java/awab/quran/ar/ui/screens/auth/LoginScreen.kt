package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

    fun performLogin() {
        if (!validateInputs(email, password,
                onEmailError = { emailError = it },
                onPasswordError = { passwordError = it }
            )) {
            return
        }

        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(
                        context,
                        "فشل تسجيل الدخول: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // صورة الخلفية
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "خلفية تسجيل الدخول",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // طبقة النجوم المتلألئة
        TwinklingStars()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // أيقونة المصحف
            QuranBookIcon()

            // عنوان التطبيق
            Text(
                text = "تسميع القرآن",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B5744),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "تحفّظوا من حفظك القرآن الكريم بالذكاء الاصطناعي.",
                fontSize = 14.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // بطاقة تسجيل الدخول
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8E4DB).copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // حقل البريد الإلكتروني
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        placeholder = { Text("البريد الإلكتروني", color = Color(0xFF9B8B7A)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF8B7355)) },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB5A68F),
                            unfocusedBorderColor = Color(0xFFB5A68F),
                            focusedContainerColor = Color(0xFFF5F2EA),
                            unfocusedContainerColor = Color(0xFFF5F2EA)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(start = 16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // حقل كلمة المرور
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        placeholder = { Text("••••••••", color = Color(0xFF9B8B7A)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8B7355)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFFB5A68F)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); performLogin() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB5A68F),
                            unfocusedBorderColor = Color(0xFFB5A68F),
                            focusedContainerColor = Color(0xFFF5F2EA),
                            unfocusedContainerColor = Color(0xFFF5F2EA)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    passwordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(start = 16.dp))
                    }

                    TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                        Text("• نسيت كلمة المرور؟", color = Color(0xFF8B7355), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { performLogin() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B8A6F)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("تسجيل الدخول", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // زر الميكروفون للبدء السريع
            Surface(
                modifier = Modifier.size(72.dp).clickable { /* تفعيل التسميع المباشر */ },
                shape = CircleShape,
                color = Color(0xFF6B5744),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Mic, contentDescription = "تسميع", tint = Color(0xFFF5E6D3), modifier = Modifier.size(36.dp))
                }
            }
            Text("اضغط لبدء التسميع", fontSize = 14.sp, color = Color(0xFF8B7355), modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun QuranBookIcon() {
    Canvas(modifier = Modifier.size(90.dp).padding(bottom = 16.dp)) {
        val width = size.width
        val height = size.height
        val color = Color(0xFF8B7355)
        
        val path = Path().apply {
            moveTo(width * 0.25f, height * 0.25f)
            lineTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
            lineTo(width * 0.25f, height * 0.75f)
            close()
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.75f, height * 0.25f)
            lineTo(width * 0.75f, height * 0.75f)
            lineTo(width * 0.5f, height * 0.8f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = 2.5f))
    }
}

@Composable
fun TwinklingStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starPositions = remember { listOf(Offset(0.2f, 0.15f), Offset(0.8f, 0.2f), Offset(0.15f, 0.7f), Offset(0.85f, 0.75f)) }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        starPositions.forEachIndexed { index, pos ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1500 + index * 200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = ""
            )
            drawCircle(Color.White.copy(alpha = alpha), radius = 3f, center = Offset(size.width * pos.x, size.height * pos.y))
        }
    }
}

private fun validateInputs(email: String, password: String, onEmailError: (String?) -> Unit, onPasswordError: (String?) -> Unit): Boolean {
    var isValid = true
    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("البريد الإلكتروني غير صحيح")
        isValid = false
    } else onEmailError(null)

    if (password.length < 6) {
        onPasswordError("كلمة المرور قصيرة جداً")
        isValid = false
    } else onPasswordError(null)
    
    return isValid
}
