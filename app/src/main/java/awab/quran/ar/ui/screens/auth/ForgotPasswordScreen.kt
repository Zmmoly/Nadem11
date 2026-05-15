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
import androidx.compose.ui.res.stringResource
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

    // استخراج النصوص هنا لاستخدامها داخل اللامبدا
    val strEnterEmail = stringResource(R.string.enter_email)
    val strInvalidEmail = stringResource(R.string.invalid_email)
    val strNoAccount = stringResource(R.string.forgot_password_no_account)
    val strTooManyRequests = stringResource(R.string.forgot_password_too_many_requests)
    val strCheckInternet = stringResource(R.string.check_internet)
    val strErrorTryAgain = stringResource(R.string.error_try_again)

    fun sendResetEmail() {
        if (email.isBlank()) {
            emailError = strEnterEmail
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = strInvalidEmail
            return
        }

        isLoading = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    emailSent = true
                } else {
                    val errorMsg = task.exception?.message ?: ""
                    emailError = when {
                        errorMsg.contains("USER_NOT_FOUND") ||
                        errorMsg.contains("no user record") ||
                        errorMsg.contains("INVALID_EMAIL") ->
                            strNoAccount

                        errorMsg.contains("too many requests") ->
                            strTooManyRequests

                        errorMsg.contains("network") ||
                        errorMsg.contains("NETWORK") ->
                            strCheckInternet

                        else ->
                            strErrorTryAgain
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (!isDarkMode) {
            Image(
                painter = painterResource(id = R.drawable.app_background),
                contentDescription = stringResource(R.string.splash_background_desc),
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
                contentDescription = stringResource(R.string.back),
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
                text = stringResource(R.string.forgot_password),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.forgot_password_desc),
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
                            label = { Text(stringResource(R.string.email), color = subColor) },
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
                                    text = stringResource(R.string.send_reset_link),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).padding(bottom = 16.dp),
                            tint = titleColor
                        )
                        Text(
                            text = stringResource(R.string.forgot_password_email_sent),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.forgot_password_check_inbox),
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
                                text = stringResource(R.string.back_to_login),
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
