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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
        // Background Image - استخدام الصورة الموجودة فعلاً
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "خلفية تسجيل الدخول",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Twinkling Stars overlay
        TwinklingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Book Icon with Stand
            QuranBookIcon()

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "تسميع القرآن",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A6B5D),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "تحفّظوا من حفظك القرآن الكريم الذكّاء الكريم.",
                fontSize = 14.sp,
                color = Color(0xFF9B8B7A),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Text(
                text = "الذكاء الصّطناعي.",
                fontSize = 14.sp,
                color = Color(0xFF9B8B7A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEBE6DC).copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = {
                            Text(
                                "البريد الإلكتروني",
                                color = Color(0xFF9B8B7A)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "أيقونة البريد",
                                tint = Color(0xFF9B8B7A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB5A68F),
                            unfocusedBorderColor = Color(0xFFD8CFC0),
                            cursorColor = Color(0xFF8B7355),
                            focusedTextColor = Color(0xFF6B5744),
                            unfocusedTextColor = Color(0xFF6B5744),
                            focusedContainerColor = Color(0xFFFAF8F4),
                            unfocusedContainerColor = Color(0xFFFAF8F4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = {
                            Text(
                                "••••••••",
                                color = Color(0xFF9B8B7A)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "أيقونة القفل",
                                tint = Color(0xFF9B8B7A)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "إخفاء كلمة المرور"
                                    else
                                        "إظهار كلمة المرور",
                                    tint = Color(0xFFB5A68F)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                performLogin()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB5A68F),
                            unfocusedBorderColor = Color(0xFFD8CFC0),
                            cursorColor = Color(0xFF8B7355),
                            focusedTextColor = Color(0xFF6B5744),
                            unfocusedTextColor = Color(0xFF6B5744),
                            focusedContainerColor = Color(0xFFFAF8F4),
                            unfocusedContainerColor = Color(0xFFFAF8F4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                        )
                    }

                    // Forgot Password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onNavigateToForgotPassword
                        ) {
                            Text(
                                text = "• نسيت كلمة المرور؟",
                                color = Color(0xFF9B8B7A),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login Button
                    Button(
                        onClick = { performLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B8A6F),
                            disabledContainerColor = Color(0xFF9B9B8A)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "تسجيل الدخول",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Divider
            Text(
                text = "أو تابع التسجيل بإستخدام",
                fontSize = 13.sp,
                color = Color(0xFF9B8B7A),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Social Login Buttons - باستخدام أيقونات مرسومة بـ Canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Google Button
                SocialLoginButton(
                    onClick = {
                        Toast.makeText(context, "تسجيل الدخول عبر Google", Toast.LENGTH_SHORT).show()
                    },
                    iconType = SocialIconType.GOOGLE
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Apple Button
                SocialLoginButton(
                    onClick = {
                        Toast.makeText(context, "تسجيل الدخول عبر Apple", Toast.LENGTH_SHORT).show()
                    },
                    iconType = SocialIconType.APPLE
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Facebook Button
                SocialLoginButton(
                    onClick = {
                        Toast.makeText(context, "تسجيل الدخول عبر Facebook", Toast.LENGTH_SHORT).show()
                    },
                    iconType = SocialIconType.FACEBOOK
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QuranBookIcon() {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val color = Color(0xFFB5A68F)
        
        // Draw open Quran book
        val bookPath = Path().apply {
            // Left page
            moveTo(width * 0.2f, height * 0.3f)
            quadraticBezierTo(
                width * 0.25f, height * 0.25f,
                width * 0.5f, height * 0.25f
            )
            lineTo(width * 0.5f, height * 0.7f)
            quadraticBezierTo(
                width * 0.25f, height * 0.72f,
                width * 0.2f, height * 0.68f
            )
            close()
            
            // Right page
            moveTo(width * 0.5f, height * 0.25f)
            quadraticBezierTo(
                width * 0.75f, height * 0.25f,
                width * 0.8f, height * 0.3f
            )
            lineTo(width * 0.8f, height * 0.68f)
            quadraticBezierTo(
                width * 0.75f, height * 0.72f,
                width * 0.5f, height * 0.7f
            )
            close()
        }
        
        drawPath(
            path = bookPath,
            color = color,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
        
        // Draw page lines
        for (i in 1..5) {
            val y = height * (0.32f + i * 0.07f)
            drawLine(
                color = color,
                start = Offset(width * 0.25f, y),
                end = Offset(width * 0.48f, y),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(width * 0.52f, y),
                end = Offset(width * 0.75f, y),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
        
        // Draw stand
        val standPath = Path().apply {
            moveTo(width * 0.15f, height * 0.8f)
            lineTo(width * 0.3f, height * 0.68f)
            
            moveTo(width * 0.85f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.68f)
        }
        
        drawPath(
            path = standPath,
            color = color,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun TwinklingStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "star twinkle")
    
    val starPositions = remember {
        listOf(
            Offset(0.2f, 0.15f),
            Offset(0.8f, 0.2f),
            Offset(0.15f, 0.7f),
            Offset(0.85f, 0.75f)
        )
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        starPositions.forEachIndexed { index, position ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500 + index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "star alpha $index"
            )
            
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 3f,
                center = Offset(size.width * position.x, size.height * position.y)
            )
        }
    }
}

enum class SocialIconType {
    GOOGLE, APPLE, FACEBOOK
}

@Composable
fun SocialLoginButton(
    onClick: () -> Unit,
    iconType: SocialIconType
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color(0xFFFAF8F4),
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    when (iconType) {
                        SocialIconType.GOOGLE -> drawGoogleIcon()
                        SocialIconType.APPLE -> drawAppleIcon()
                        SocialIconType.FACEBOOK -> drawFacebookIcon()
                    }
                },
            contentAlignment = Alignment.Center
        )
    }
}

// رسم أيقونة Google
fun DrawScope.drawGoogleIcon() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val iconSize = size.width * 0.45f
    
    // رسم G ملون
    val gPath = Path().apply {
        // الجزء الأزرق العلوي
        moveTo(centerX + iconSize * 0.3f, centerY)
        lineTo(centerX + iconSize * 0.5f, centerY)
        lineTo(centerX + iconSize * 0.5f, centerY - iconSize * 0.15f)
        lineTo(centerX, centerY - iconSize * 0.15f)
    }
    
    drawPath(gPath, Color(0xFF4285F4))
    
    // G الأحمر
    drawCircle(
        color = Color(0xFFEA4335),
        radius = iconSize * 0.15f,
        center = Offset(centerX - iconSize * 0.2f, centerY - iconSize * 0.2f)
    )
    
    // G الأصفر
    drawCircle(
        color = Color(0xFBBC05),
        radius = iconSize * 0.12f,
        center = Offset(centerX - iconSize * 0.3f, centerY + iconSize * 0.1f)
    )
    
    // G الأخضر
    drawCircle(
        color = Color(0xFF34A853),
        radius = iconSize * 0.12f,
        center = Offset(centerX + iconSize * 0.1f, centerY + iconSize * 0.3f)
    )
}

// رسم أيقونة Apple
fun DrawScope.drawAppleIcon() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val iconSize = size.width * 0.4f
    
    // رسم التفاحة
    val applePath = Path().apply {
        // الجسم
        moveTo(centerX, centerY - iconSize * 0.3f)
        cubicTo(
            centerX - iconSize * 0.4f, centerY - iconSize * 0.2f,
            centerX - iconSize * 0.4f, centerY + iconSize * 0.4f,
            centerX, centerY + iconSize * 0.5f
        )
        cubicTo(
            centerX + iconSize * 0.4f, centerY + iconSize * 0.4f,
            centerX + iconSize * 0.4f, centerY - iconSize * 0.2f,
            centerX, centerY - iconSize * 0.3f
        )
    }
    
    drawPath(applePath, Color(0xFF000000), style = Stroke(width = 3f))
    
    // الورقة
    drawLine(
        color = Color(0xFF000000),
        start = Offset(centerX, centerY - iconSize * 0.3f),
        end = Offset(centerX + iconSize * 0.15f, centerY - iconSize * 0.45f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}

// رسم أيقونة Facebook
fun DrawScope.drawFacebookIcon() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val iconSize = size.width * 0.35f
    
    // رسم حرف f
    val fPath = Path().apply {
        // العمودي
        moveTo(centerX + iconSize * 0.1f, centerY - iconSize * 0.5f)
        lineTo(centerX + iconSize * 0.1f, centerY + iconSize * 0.5f)
        
        // الأفقي العلوي
        moveTo(centerX - iconSize * 0.2f, centerY - iconSize * 0.3f)
        lineTo(centerX + iconSize * 0.4f, centerY - iconSize * 0.3f)
        
        // الأفقي الأوسط
        moveTo(centerX - iconSize * 0.1f, centerY)
        lineTo(centerX + iconSize * 0.3f, centerY)
    }
    
    drawPath(
        fPath,
        Color(0xFF1877F2),
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )
}

private fun validateInputs(
    email: String,
    password: String,
    onEmailError: (String?) -> Unit,
    onPasswordError: (String?) -> Unit
): Boolean {
    var isValid = true

    if (email.isBlank()) {
        onEmailError("الرجاء إدخال البريد الإلكتروني")
        isValid = false
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("البريد الإلكتروني غير صحيح")
        isValid = false
    } else {
        onEmailError(null)
    }

    if (password.isBlank()) {
        onPasswordError("الرجاء إدخال كلمة المرور")
        isValid = false
    } else if (password.length < 6) {
        onPasswordError("كلمة المرور يجب أن تكون 6 أحرف على الأقل")
        isValid = false
    } else {
        onPasswordError(null)
    }

    return isValid
}
 
