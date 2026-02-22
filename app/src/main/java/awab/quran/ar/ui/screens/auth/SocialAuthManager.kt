package awab.quran.ar.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// =============================================
// Google Sign-In
// =============================================
fun signInWithGoogle(
    context: Context,
    coroutineScope: CoroutineScope,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val credentialManager = CredentialManager.create(context)

    // استبدل هذا بـ Web Client ID من Firebase Console > Authentication > Google > Web SDK configuration
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
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.let { onSuccess(it) }
                        } else {
                            onError("فشل تسجيل الدخول بـ Google: ${task.exception?.localizedMessage}")
                        }
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

// =============================================
// Apple Sign-In
// =============================================
fun signInWithApple(
    activity: Activity,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val provider = OAuthProvider.newBuilder("apple.com")
        .setScopes(listOf("email", "name"))
        .build()

    auth.startActivityForSignInWithProvider(activity, provider)
        .addOnSuccessListener { result ->
            result.user?.let { onSuccess(it) }
        }
        .addOnFailureListener { exception ->
            Log.e("AppleSignIn", "Error: ${exception.message}")
            onError("فشل تسجيل الدخول بـ Apple: ${exception.localizedMessage}")
        }
}

// =============================================
// Facebook Sign-In
// =============================================
fun signInWithFacebook(
    activity: Activity,
    callbackManager: CallbackManager,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    LoginManager.getInstance().registerCallback(callbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.let { onSuccess(it) }
                        } else {
                            onError("فشل تسجيل الدخول بـ Facebook: ${task.exception?.localizedMessage}")
                        }
                    }
            }

            override fun onCancel() {
                onError("تم إلغاء تسجيل الدخول بـ Facebook")
            }

            override fun onError(error: FacebookException) {
                Log.e("FacebookSignIn", "Error: ${error.message}")
                onError("فشل تسجيل الدخول بـ Facebook: ${error.localizedMessage}")
            }
        }
    )

    LoginManager.getInstance().logInWithReadPermissions(
        activity,
        callbackManager,
        listOf("email", "public_profile")
    )
}
