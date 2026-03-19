package awab.quran.ar.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// ✅ استخراج Activity بشكل صحيح من Compose Context
fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    error("لم يتم العثور على Activity")
}

fun signInWithGoogle(
    context: Context,
    coroutineScope: CoroutineScope,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val credentialManager = CredentialManager.create(context)
    val webClientId = context.getString(awab.quran.ar.R.string.google_web_client_id)
    val activity = context.findActivity()

    // ✅ الخطوة 1: جرب الحسابات المخولة مسبقاً أولاً
    val authorizedOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(true)
        .build()

    // ✅ الخطوة 2: fallback لجميع الحسابات
    val allAccountsOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(false)
        .build()

    coroutineScope.launch {
        val result = try {
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(authorizedOption)
                .build()
            credentialManager.getCredential(activity, request)
        } catch (e: NoCredentialException) {
            Log.d("GoogleSignIn", "No authorized account, trying all accounts")
            try {
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(allAccountsOption)
                    .build()
                credentialManager.getCredential(activity, request)
            } catch (e2: GetCredentialCancellationException) {
                Log.d("GoogleSignIn", "User cancelled")
                return@launch
            } catch (e2: GetCredentialException) {
                Log.e("GoogleSignIn", "Fallback error: ${e2::class.simpleName} - ${e2.message}")
                onError("فشل تسجيل الدخول بـ Google: ${e2.localizedMessage}")
                return@launch
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("GoogleSignIn", "User cancelled")
            return@launch
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "Error: ${e::class.simpleName} - ${e.message}")
            onError("فشل تسجيل الدخول بـ Google: ${e.localizedMessage}")
            return@launch
        }

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(
                    googleIdTokenCredential.idToken, null
                )
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        if (authResult.additionalUserInfo?.isNewUser == true) {
                            auth.currentUser?.let { user ->
                                saveUserToFirestore(
                                    user.uid,
                                    user.displayName ?: "مستخدم",
                                    user.email ?: ""
                                )
                            }
                        }
                        auth.currentUser?.let { onSuccess(it) }
                    }
                    .addOnFailureListener { e ->
                        Log.e("GoogleSignIn", "Firebase error: ${e.message}")
                        onError("فشل تسجيل الدخول: ${e.localizedMessage}")
                    }
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Token parse error: ${e.message}")
                onError("خطأ في معالجة بيانات Google")
            }
        } else {
            onError("نوع بيانات الاعتماد غير مدعوم")
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
