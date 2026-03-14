package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    isDarkMode: Boolean = false
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // ألوان
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.Transparent
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F3ED).copy(alpha = 0.95f)
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF6B5744)
    val subColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF8B7355)
    val borderFocused = if (isDarkMode) Color(0xFFD4AF37) else Color(0xFF8B7355)
    val borderUnfocused = if (isDarkMode) Color(0xFF444444) else Color(0xFFD4C5A9)
    val fieldBg = if (isDarkMode) Color(0xFF2C2C2C) else Color.Unspecified
    val fieldText = if (isDarkMode) Color(0xFFE0E0E0) else Color.Unspecified

    fun performRegister() {
        // التحقق من المدخلات
        var isValid = true
        
        if (fullName.isBlank()) {
            nameError = "الرجاء إدخال الاسم الكامل"
            isValid = false
        } else {
            nameError = null
        }
        
        if (email.isBlank()) {
            emailError = "الرجاء إدخال البريد الإلكتروني"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "البريد الإلكتروني غير صحيح"
            isValid = false
        } else {
            emailError = null
        }
        
        if (password.isBlank()) {
            passwordError = "الرجاء إدخال كلمة المرور"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            isValid = false
        } else {
            passwordError = null
        }
        
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "الرجاء تأكيد كلمة المرور"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "كلمة المرور غير متطابقة"
            isValid = false
        } else {
            confirmPasswordError = null
        }
        
        if (!isValid) return

        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis(),
                        "totalRecitations" to 0,
                        "completedSurahs" to 0
                    )
                    
                    userId?.let {
                        firestore.collection("users").document(it)
                            .set(userData)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess()
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "فشل حفظ البيانات: ${exception.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                } else {
                    isLoading = false
                    Toast.makeText(
                        context,
                        "فشل إنشاء الحساب: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor)
    ) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.app_background),
                contentDescription = "خلفية التسجيل",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "إنشاء حساب جديد", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = titleColor, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "انضم إلينا في رحلة حفظ القرآن الكريم", fontSize = 16.sp, color = subColor, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 32.dp))

            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderFocused, unfocusedBorderColor = borderUnfocused,
                        focusedLabelColor = borderFocused, cursorColor = borderFocused,
                        focusedContainerColor = fieldBg, unfocusedContainerColor = fieldBg,
                        focusedTextColor = fieldText, unfocusedTextColor = fieldText
                    )

                    OutlinedTextField(value = fullName, onValueChange = { fullName = it; nameError = null }, label = { Text("الاسم الكامل") }, leadingIcon = { Icon(Icons.Default.Person, null) }, isError = nameError != null, supportingText = { nameError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = fieldColors)

                    OutlinedTextField(value = email, onValueChange = { email = it; emailError = null }, label = { Text("البريد الإلكتروني") }, leadingIcon = { Icon(Icons.Default.Email, null) }, isError = emailError != null, supportingText = { emailError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = fieldColors)

                    OutlinedTextField(value = password, onValueChange = { password = it; passwordError = null }, label = { Text("كلمة المرور") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), isError = passwordError != null, supportingText = { passwordError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = fieldColors)

                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = null }, label = { Text("تأكيد كلمة المرور") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } }, visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), isError = confirmPasswordError != null, supportingText = { confirmPasswordError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); performRegister() }), singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), colors = fieldColors)

                    Button(onClick = { performRegister() }, enabled = !isLoading, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0xFF4A7C59) else Color(0xFF8B7355)), shape = RoundedCornerShape(12.dp)) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text(text = "إنشاء الحساب", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "أو تابع التسجيل بإستخدام", fontSize = 13.sp, color = subColor, modifier = Modifier.padding(vertical = 8.dp))

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        SocialButton(R.drawable.ic_google, "Google", isDarkMode) { signInWithGoogle(context = context, coroutineScope = coroutineScope, onSuccess = { onRegisterSuccess() }, onError = { msg -> android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show() }) }
                        Spacer(modifier = Modifier.width(12.dp))
                        SocialButton(R.drawable.ic_apple, "Apple", isDarkMode) { android.widget.Toast.makeText(context, "تسجيل الدخول بـ Apple غير متاح حالياً", android.widget.Toast.LENGTH_SHORT).show() }
                        Spacer(modifier = Modifier.width(12.dp))
                        SocialButton(R.drawable.ic_facebook, "Facebook", isDarkMode) { android.widget.Toast.makeText(context, "تسجيل الدخول بـ Facebook غير متاح حالياً", android.widget.Toast.LENGTH_SHORT).show() }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onNavigateBack) { Text(text = "لديك حساب بالفعل؟ سجل الدخول", color = subColor, fontSize = 16.sp) }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = if (isDarkMode) Color(0xFFE0E0E0) else Color.White)
        }
    }
}
