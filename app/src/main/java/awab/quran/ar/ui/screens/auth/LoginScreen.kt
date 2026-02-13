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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // صورة الخلفية - تأكد من وجود ملف باسم login_background في الـ drawables
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "خلفية تسجيل الدخول",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        TwinklingStars()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            QuranBookIcon()

            Text(
                text = "تسميع القرآن",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B5744),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "تحفّظوا من حفظك القرآن الكريم الذكّاء الكريم.",
                fontSize = 14.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "الذكاء الصّطناعي.",
                fontSize = 14.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8E4DB).copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = { Text("البريد الإلكتروني", color = Color(0xFF9B8B7A)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF8B7355))
                        },
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

                    if (emailError != null) {
                        Text(text = emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = { Text("••••••••", color = Color(0xFF9B8B7A)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8B7355))
                        },
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
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            performLogin()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB5A68F),
                            unfocusedBorderColor = Color(0xFFB5A68F),
                            focusedContainerColor = Color(0xFFF5F2EA),
                            unfocusedContainerColor = Color(0xFFF5F2EA)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "• نسيت كلمة المرور؟", color = Color(0xFF8B7355), fontSize = 13.sp)
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
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("تسجيل الدخول", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Text(
                text = "أو تابع التسجيل بإستخدام",
                fontSize = 13.sp,
                color = Color(0xFF9B8B7A),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialButton(R.drawable.ic_google, "Google") { 
                    Toast.makeText(context, "تسجيل الدخول عبر Google", Toast.LENGTH_SHORT).show() 
                }
                SocialButton(R.drawable.ic_apple, "Apple") { 
                    Toast.makeText(context, "تسجيل الدخول عبر Apple", Toast.LENGTH_SHORT).show() 
                }
                SocialButton(R.drawable.ic_facebook, "Facebook") { 
                    Toast.makeText(context, "تسجيل الدخول عبر Facebook", Toast.LENGTH_SHORT).show() 
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = Color(0xFF6B5744),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Mic, contentDescription = "الميكروفون", tint = Color(0xFFF5E6D3), modifier = Modifier.size(36.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "أضغط لبدء التسميع", fontSize = 14.sp, color = Color(0xFF8B7355))
        }
    }
}

@Composable
fun SocialButton(iconRes: Int, contentDescription: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(60.dp).padding(horizontal = 8.dp).clickable { onClick() },
        shape = CircleShape,
        color = Color(0xFFFAF8F4),
        shadowElevation = 6.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = iconRes), contentDescription = contentDescription, modifier = Modifier.size(24.dp))
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
        
        for (i in 1..4) {
            val y = height * (0.3f + i * 0.12f)
            drawLine(color, Offset(width * 0.3f, y), Offset(width * 0.48f, y), 1.2f)
            drawLine(color, Offset(width * 0.52f, y), Offset(width * 0.7f, y), 1.2f)
        }
        
        val standPath = Path().apply {
            moveTo(width * 0.2f, height * 0.85f)
            lineTo(width * 0.35f, height * 0.75f)
            moveTo(width * 0.8f, height * 0.85f)
            lineTo(width * 0.65f, height * 0.75f)
        }
        drawPath(path = standPath, color = color, style = Stroke(width = 2f))
    }
}

@Composable
fun TwinklingStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "star twinkle")
    val starPositions = remember { listOf(Offset(0.2f, 0.15f), Offset(0.8f, 0.2f), Offset(0.15f, 0.7f), Offset(0.85f, 0.75f)) }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        starPositions.forEachIndexed { index, position ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1500 + index * 200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = ""
            )
            drawCircle(Color.White.copy(alpha = alpha), 3f, Offset(size.width * position.x, size.height * position.y))
        }
    }
}

private fun validateInputs(email: String, password: String, onEmailError: (String?) -> Unit, onPasswordError: (String?) -> Unit): Boolean {
    var isValid = true
    if (email.isBlank()) { onEmailError("الرجاء إدخال البريد الإلكتروني"); isValid = false }
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { onEmailError("البريد الإلكتروني غير صحيح"); isValid = false }
    else onEmailError(null)

    if (password.isBlank()) { onPasswordError("الرجاء إدخال كلمة المرور"); isValid = false }
    else if (password.length < 6) { onPasswordError("كلمة المرور يجب أن تكون 6 أحرف على الأقل"); isValid = false }
    else onPasswordError(null)
    return isValid
}
 
