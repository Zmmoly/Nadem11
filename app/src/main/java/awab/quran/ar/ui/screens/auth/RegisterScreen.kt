package awab.quran.ar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import awab.quran.ar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit
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

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showVerificationSent by remember { mutableStateOf(false) }
    var resendLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(
            data = result.data,
            onSuccess = { onRegisterSuccess() },
            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
        )
    }

    fun performRegister() {
        var isValid = true
        if (fullName.isBlank()) { nameError = "الرجاء إدخال الاسم الكامل"; isValid = false } else nameError = null
        if (email.isBlank()) { emailError = "الرجاء إدخال البريد الإلكتروني"; isValid = false }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "البريد الإلكتروني غير صحيح"; isValid = false }
        else emailError = null
        if (password.isBlank()) { passwordError = "الرجاء إدخال كلمة المرور"; isValid = false }
        else if (password.length < 6) { passwordError = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"; isValid = false }
        else passwordError = null
        if (confirmPassword.isBlank()) { confirmPasswordError = "الرجاء تأكيد كلمة المرور"; isValid = false }
        else if (password != confirmPassword) { confirmPasswordError = "كلمة المرور غير متطابقة"; isValid = false }
        else confirmPasswordError = null
        if (!isValid) return

        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userData = hashMapOf(
                        "fullName" to fullName, "email" to email,
                        "createdAt" to System.currentTimeMillis(),
                        "totalRecitations" to 0, "completedSurahs" to 0
                    )
                    // ✅ احفظ في Firestore في الخلفية
                    if (userId != null) {
                        firestore.collection("users").document(userId).set(userData)
                    }
                    // ✅ أرسل إيميل التحقق واعرض شاشة التأكيد
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            isLoading = false
                            auth.signOut()
                            showVerificationSent = true
                        }
                        ?: run {
                            isLoading = false
                            auth.signOut()
                            showVerificationSent = true
                        }
                } else {
                    isLoading = false
                    val errorMsg = task.exception?.message ?: ""
                    val arabicError = when {
                        errorMsg.contains("email address is already in use") ||
                        errorMsg.contains("EMAIL_EXISTS") ->
                            "البريد الإلكتروني مسجل مسبقاً"

                        errorMsg.contains("badly formatted") ||
                        errorMsg.contains("INVALID_EMAIL") ->
                            "البريد الإلكتروني غير صحيح"

                        errorMsg.contains("password is invalid") ||
                        errorMsg.contains("WEAK_PASSWORD") ->
                            "كلمة المرور ضعيفة جداً، استخدم 6 أحرف على الأقل"

                        errorMsg.contains("network") ||
                        errorMsg.contains("NETWORK") ->
                            "تحقق من اتصالك بالإنترنت"

                        else ->
                            "حدث خطأ، حاول مرة أخرى"
                    }
                    Toast.makeText(context, arabicError, Toast.LENGTH_LONG).show()
                }
            }
    }

    // ===== نافذة سياسة الخصوصية =====
    if (showPrivacyDialog) {
        PolicyDialog(
            title = "سياسة الخصوصية",
            content = """
آخر تحديث: 17 مارس 2026

تلتزم إدارة تطبيق نديم بحماية خصوصية المستخدمين والحفاظ على سرية البيانات.

1. البيانات التي يتم جمعها

أ. البيانات التي يقدمها المستخدم:
الاسم الكامل والبريد الإلكتروني وكلمة المرور (مشفرة) عند التسجيل، أو الاسم والبريد ومعرّف المستخدم عند تسجيل الدخول عبر Google، والتسجيلات الصوتية للتلاوة القرآنية.

ب. البيانات التي يجمعها التطبيق تلقائيًا:
معلومات الجهاز مثل نوع الهاتف ونظام التشغيل وإصدار التطبيق، وبيانات الاستخدام مثل عدد جلسات التسميع والسور المكتملة.

2. الغرض من جمع البيانات

تُستخدم البيانات لتقديم خدمة التسميع وتحليل التلاوة، وتحسين التطبيق، والأمان والحماية، وتدريب نماذج الذكاء الاصطناعي للتعرف على الصوت القرآني (لمن لم يوقف هذا الخيار من الإعدادات).

3. مشاركة البيانات

لا يشارك التطبيق بيانات المستخدم لأغراض تجارية. يتم مشاركة البيانات فقط مع:
• Google Firebase: لتخزين بيانات الحساب.
• خادم المعالجة الصوتية (Modal): لتحويل التسجيلات إلى نص وتخزينها لأغراض التدريب لمن وافق على ذلك.

4. حفظ البيانات

تُخزَّن التسجيلات الصوتية بشكل مشفر لمن لم يوقف خيار جمع البيانات. أما من أوقف هذا الخيار فلا يتم الاحتفاظ بأي تسجيل بعد التحليل.

5. حقوق المستخدم

يحق لك: الاطلاع على بياناتك، تصحيح أي بيانات غير دقيقة، طلب حذف تسجيلاتك أو حسابك كاملًا، وإيقاف جمع التسجيلات للتدريب من الإعدادات في أي وقت.

6. الأذونات المطلوبة

• الميكروفون: لتسجيل التلاوة عند الضغط على زر التسميع فقط.
• الإنترنت: لإرسال التسجيل للمعالجة والتواصل مع Firebase.
• التخزين (Android 12 وأقل): لبعض البيانات المحلية.
• الاهتزاز: لإشعارات التغذية الراجعة.

7. خصوصية الأطفال

التطبيق موجه من عمر 4 سنوات فأكثر. يتحمل ولي الأمر المسؤولية الكاملة عن استخدام القاصرين.

8. الموافقة على تخزين الصوت للتدريب

استخدامك لميزة التسميع يُعدّ موافقة مبدئية على تخزين تسجيلاتك واستخدامها لتحسين نماذج الذكاء الاصطناعي. يمكنك إيقاف ذلك من الإعدادات في أي وقت.

9. تعديل سياسة الخصوصية

يحتفظ التطبيق بالحق في تعديل هذه السياسة في أي وقت. استمرار استخدام التطبيق بعد النشر يعني موافقتك على النسخة المحدثة.

تطبيق نديم — جميع الحقوق محفوظة © 2026
            """.trimIndent(),
            onDismiss = { showPrivacyDialog = false }
        )
    }

    // ===== نافذة شروط الاستخدام =====
    if (showTermsDialog) {
        PolicyDialog(
            title = "شروط الاستخدام",
            content = """
آخر تحديث: 17 مارس 2026

باستخدامك التطبيق أو تثبيته، فإنك توافق على الالتزام الكامل بهذه الشروط.

1. تعريف الخدمة

تطبيق نديم هو تطبيق تعليمي لتسميع القرآن الكريم ومراجعة الحفظ باستخدام الذكاء الاصطناعي. جميع الملاحظات تعليمية وإرشادية فقط ولا تُعد بديلًا عن التعلم مع معلم مختص.

2. الترخيص المحدود

يُمنح ترخيص محدود وشخصي للاستخدام التعليمي فقط. لا يجوز نسخ أو توزيع أو بيع التطبيق أو تعديله أو تحليل شيفرته.

3. الملكية الفكرية

جميع حقوق التطبيق ملك للمطور. نموذج الذكاء الاصطناعي ملك لشركة NVIDIA. نصوص القرآن الكريم مصدرها مجمع الملك فهد. خط KFGQPC Uthman Taha Naskh ملك لمجمع الملك فهد © 2008–2010.

التسجيلات الصوتية للتلاوة القرآنية المستخدمة في وضع الاختبار مصدرها موقع EveryAyah.com وهي ليست ملكاً للتطبيق. جميع حقوق هذه التسجيلات محفوظة لأصحابها الأصليين.

4. استخدام الذكاء الاصطناعي

يعتمد التطبيق على نموذج stt_ar_fastconformer من NVIDIA. عند التسميع يُرسل الصوت لخادم خارجي للمعالجة. لا يمكن ضمان دقة النتائج بالكامل.

5. السلوك المحظور

يُحظر: خرق القوانين، اختراق التطبيق، إساءة استخدام الخدمة، أو استخدام التطبيق بطريقة مسيئة للنصوص الدينية.

6. إخلاء المسؤولية

يُستخدم التطبيق على مسؤوليتك الكاملة. لا يتحمل المطور أي مسؤولية عن أضرار أو خسائر ناتجة عن استخدام التطبيق.

7. القانون الحاكم

تخضع هذه الشروط لقوانين المملكة العربية السعودية.

8. الفئة العمرية

التطبيق موجه من عمر 4 سنوات فأكثر. يتحمل ولي الأمر المسؤولية عن استخدام القاصرين.

9. التحكيم والنزاعات

أي نزاع يُحل عبر التحكيم الفردي. لا يحق رفع دعاوى جماعية ضد المطور.

10. تحديث الشروط

يحق للمطور تعديل الشروط في أي وقت. استمرارك في الاستخدام يعني قبولك للشروط المحدثة.

تطبيق نديم — جميع الحقوق محفوظة © 2026
            """.trimIndent(),
            onDismiss = { showTermsDialog = false }
        )
    }

    // ✅ شاشة "تحقق من بريدك" بعد التسجيل
    if (showVerificationSent) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("✉️", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text("تحقق من بريدك الإلكتروني", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "تم إرسال رابط التفعيل إلى\n$email\nافتح بريدك وفعّل حسابك ثم سجّل الدخول",
                    fontSize = 14.sp, color = Color(0xFFAAAAAA), textAlign = TextAlign.Center, lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onNavigateToLogin() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("الذهاب لتسجيل الدخول", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        resendLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                auth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener {
                                        auth.signOut()
                                        resendLoading = false
                                        Toast.makeText(context, "تم إعادة إرسال الرابط ✉️", Toast.LENGTH_LONG).show()
                                    }
                            }
                            .addOnFailureListener {
                                resendLoading = false
                                Toast.makeText(context, "حدث خطأ، حاول مرة أخرى", Toast.LENGTH_SHORT).show()
                            }
                    }
                ) {
                    if (resendLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFFD4AF37), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("إعادة إرسال رابط التفعيل", color = Color(0xFFD4AF37), fontSize = 13.sp)
                }
            }
        }
        return
    }

    // ألوان الوضع الليلي
    val bgColor     = Color(0xFF121212)
    val cardColor   = Color(0xFF1E1E1E)
    val fieldBg     = Color(0xFF2C2C2C)
    val titleColor  = Color(0xFFE0E0E0)
    val subColor    = Color(0xFFAAAAAA)
    val accentColor = Color(0xFFD4AF37)
    val hintColor   = Color(0xFF888888)

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Text("إنشاء حساب جديد", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = accentColor, modifier = Modifier.padding(bottom = 8.dp))
            Text("انضم إلينا في رحلة حفظ القرآن الكريم", fontSize = 16.sp, color = subColor, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 32.dp))

            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = hintColor,
                        cursorColor = accentColor,
                        focusedLeadingIconColor = accentColor,
                        unfocusedLeadingIconColor = hintColor,
                        focusedTrailingIconColor = accentColor,
                        unfocusedTrailingIconColor = hintColor
                    )

                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it; nameError = null },
                        label = { Text("الاسم الكامل") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        isError = nameError != null, supportingText = { nameError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = email, onValueChange = { email = it; emailError = null },
                        label = { Text("البريد الإلكتروني") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = emailError != null, supportingText = { emailError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = password, onValueChange = { password = it; passwordError = null },
                        label = { Text("كلمة المرور") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null, supportingText = { passwordError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = { Text("تأكيد كلمة المرور") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError != null, supportingText = { confirmPasswordError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); performRegister() }),
                        singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = fieldColors
                    )

                    val annotatedText = buildAnnotatedString {
                        append("بإنشاء حساب، فإنك توافق على ")
                        pushStringAnnotation(tag = "TERMS", annotation = "terms")
                        withStyle(style = SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) { append("شروط الاستخدام") }
                        pop()
                        append(" و")
                        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                        withStyle(style = SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) { append("سياسة الخصوصية") }
                        pop()
                    }

                    androidx.compose.foundation.text.ClickableText(
                        text = annotatedText,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = subColor, textAlign = TextAlign.Center),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations("TERMS", offset, offset).firstOrNull()?.let { showTermsDialog = true }
                            annotatedText.getStringAnnotations("PRIVACY", offset, offset).firstOrNull()?.let { showPrivacyDialog = true }
                        }
                    )

                    Button(
                        onClick = { performRegister() }, enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF121212), strokeWidth = 2.dp)
                        else Text("إنشاء الحساب", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("أو تابع التسجيل بإستخدام", fontSize = 13.sp, color = subColor, modifier = Modifier.padding(vertical = 8.dp))

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        SocialButton(R.drawable.ic_google, "Google") {
                            googleSignInLauncher.launch(getGoogleSignInIntent(context))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateToLogin) {
                        Text("لديك حساب بالفعل؟ سجل الدخول", color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = accentColor)
        }
    }
}

@Composable
fun PolicyDialog(title: String, content: String, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37), modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally))
                Divider(color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
                    Text(content, fontSize = 14.sp, color = Color(0xFFCCCCCC), lineHeight = 22.sp, textAlign = TextAlign.Right)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
                ) {
                    Text("إغلاق", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                }
            }
        }
    }
}
