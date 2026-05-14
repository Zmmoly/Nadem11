package awab.quran.ar.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // تأكد من إضافة هذا الاستيراد
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import awab.quran.ar.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        
        // التحقق من تسجيل الدخول
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            onNavigateToRegister()  // المستخدم الجديد يذهب لصفحة التسجيل
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // صورة الخلفية
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = stringResource(id = R.string.splash_bg_desc), // تعديل هنا
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // المحتوى فوق الخلفية
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value)
                .padding(horizontal = 40.dp)
        ) {
            // أيقونة التطبيق (يمكن إبقاؤها كنص خام لأنها مجرد رمز تعبيري)
            Text(
                text = "☪",
                fontSize = 80.sp,
                color = Color(0xFF8B7355),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // اسم التطبيق
            Text(
                text = stringResource(id = R.string.app_name_ar), // تعديل هنا
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B5744),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // الوصف
            Text(
                text = stringResource(id = R.string.app_description), // تعديل هنا
                fontSize = 18.sp,
                color = Color(0xFF8B7355),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // آية قرآنية
            Text(
                text = stringResource(id = R.string.splash_quran_verse), // تعديل هنا
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B5744),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Text(
                text = stringResource(id = R.string.splash_quran_verse_source), // تعديل هنا
                fontSize = 14.sp,
                color = Color(0xFF9B8B7A),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // البسملة في الأسفل
        Text(
            text = stringResource(id = R.string.basmalah), // تعديل هنا
            fontSize = 18.sp,
            color = Color(0xFF8B7355),
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alphaAnim.value)
        )
    }
}
