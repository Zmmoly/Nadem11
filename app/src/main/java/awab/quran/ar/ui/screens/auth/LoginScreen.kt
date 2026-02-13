package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.focus.FocusDirection
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // صورة الخلفية
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "خلفية تسجيل الدخول",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // أيقونة المصحف
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 24.dp)
            ) {
                val width = size.width
                val height = size.height
                val color = Color(0xFF8B7355)
                
                // رسم أيقونة مصحف بسيطة
                val path = Path().apply {
                    // الصفحة اليمنى
                    moveTo(width * 0.3f, height * 0.2f)
                    lineTo(width * 0.5f, height * 0.15f)
                    lineTo(width * 0.5f, height * 0.85f)
                    lineTo(width * 0.3f, height * 0.8f)
                    close()
                    
                    // الصفحة اليسرى
                    moveTo(width * 0.5f, height * 0.15f)
                    lineTo(width * 0.7f, height * 0.2f)
                    lineTo(width * 0.7f, height * 0.8f)
                    lineTo(width * 0.5f, height * 0.85f)
                    close()
                }
                
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 3f)
                )
                
                // خطوط الصفحات
                for (i in 1..3) {
                    val y = height * (0.3f + i * 0.15f)
                    drawLine(
                        color = color,
                        start = Offset(width * 0.35f, y),
                        end = Offset(width * 0.48f, y),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = color,
                        start = Offset(width * 0.52f, y),
                        end = Offset(width * 0.65f, y),
                        strokeWidth = 1.5f
                    )
                }
            }

            // عنوان التطبيق
            Text(
                text = "تسميع القرآن",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B5744),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "تحفظوا حفظك القرآن الكريم الذكاء الكريم.",
                fontSize = 13.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            
            Text(
                text = "الذكاء الاصطناعي.",
                fontSize = 13.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // بطاقة تسجيل الدخول
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F3ED).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // حقل البريد الإلكتروني
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = { Text("البريد الإلكتروني", color = Color(0xFF9B8B7A)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF8B7355)
                            )
                        },
                        isError = emailError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4C5A9),
                            unfocusedBorderColor = Color(0xFFD4C5A9),
                            cursorColor = Color(0xFF8B7355),
                            focusedTextColor = Color(0xFF6B5744),
                            unfocusedTextColor = Color(0xFF6B5744),
                            focusedContainerColor = Color(0xFFFAF8F4),
                            unfocusedContainerColor = Color(0xFFFAF8F4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    // حقل كلمة المرور
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = { Text("كلمة المرور", color = Color(0xFF9B8B7A)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF8B7355)
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
                                    tint = Color(0xFF8B7355)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        isError = passwordError != null,
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
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4C5A9),
                            unfocusedBorderColor = Color(0xFFD4C5A9),
                            cursorColor = Color(0xFF8B7355),
                            focusedTextColor = Color(0xFF6B5744),
                            unfocusedTextColor = Color(0xFF6B5744),
                            focusedContainerColor = Color(0xFFFAF8F4),
                            unfocusedContainerColor = Color(0xFFFAF8F4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    // نسيت كلمة المرور
                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "• نسيت كلمة المرور؟",
                            color = Color(0xFF8B7355),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // زر تسجيل الدخول
                    Button(
                        onClick = { performLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6B5744)
                        ),
                        shape = RoundedCornerShape(20.dp)
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
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // فاصل
            Text(
                text = "أو تابع التسجيل بإستخدام",
                fontSize = 13.sp,
                color = Color(0xFF9B8B7A),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // أزرار تسجيل الدخول بوسائل التواصل الاجتماعي
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // زر Google
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(horizontal = 6.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B5744)
                        )
                    }
                }

                // زر Apple
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(horizontal = 6.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B5744)
                        )
                    }
                }

                // زر Facebook
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(horizontal = 6.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "f",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B5744)
                        )
                    }
                }
            }
        }
    }
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
