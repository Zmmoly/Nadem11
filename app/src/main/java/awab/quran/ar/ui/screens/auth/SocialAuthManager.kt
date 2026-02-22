package awab.quran.ar.ui.screens.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun signInWithGoogle(
    context: Context,
    coroutineScope: CoroutineScope,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val credentialManager = CredentialManager.create(context)
    val webClientId = context.getString(awab.quran.ar.R.string.google_web_client_id)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(
                    googleIdTokenCredential.idToken, null
                )
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        if (authResult.additionalUserInfo?.isNewUser == true) {
                            auth.currentUser?.let { user ->
                                saveUserToFirestore(user.uid, user.displayName ?: "مستخدم", user.email ?: "")
                            }
                        }
                        auth.currentUser?.let { onSuccess(it) }
                    }
                    .addOnFailureListener { e ->
                        onError("فشل تسجيل الدخول بـ Google: ${e.localizedMessage}")
                    }
            } else {
                onError("نوع بيانات الاعتماد غير مدعوم")
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "Error: ${e.message}")
            onError("فشل تسجيل الدخول بـ Google: ${e.localizedMessage}")
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
