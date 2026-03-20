package awab.quran.ar.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

fun getGoogleSignInIntent(context: Context): Intent {
    val webClientId = context.getString(awab.quran.ar.R.string.google_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    val client = GoogleSignIn.getClient(context, gso)
    // تسجيل الخروج أولاً لضمان ظهور نافذة اختيار الحساب دائماً
    client.signOut()
    return client.signInIntent
}

fun handleGoogleSignInResult(
    data: Intent?,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken ?: run {
            onError("فشل الحصول على Token من Google")
            return
        }
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance()
            .signInWithCredential(firebaseCredential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo?.isNewUser == true) {
                    authResult.user?.let { user ->
                        saveUserToFirestore(user.uid, user.displayName ?: "مستخدم", user.email ?: "")
                    }
                }
                authResult.user?.let { onSuccess(it) }
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Firebase error: ${e.message}")
                onError("فشل تسجيل الدخول: ${e.localizedMessage}")
            }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "ApiException: ${e.statusCode} - ${e.message}")
        if (e.statusCode != 12501) { // 12501 = المستخدم ألغى
            onError("فشل تسجيل الدخول بـ Google (${e.statusCode})")
        }
    }
}

private fun saveUserToFirestore(uid: String, name: String, email: String) {
    val userData = hashMapOf(
        "fullName" to name,
        "email" to email,
        "createdAt" to System.currentTimeMillis(),
        "totalRecitations" to 0,
        "completedSurahs" to 0
    )
    FirebaseFirestore.getInstance().collection("users").document(uid).set(userData)
}
