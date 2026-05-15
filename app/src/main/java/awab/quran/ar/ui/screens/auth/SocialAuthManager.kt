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
import awab.quran.ar.R

fun getGoogleSignInIntent(context: Context): Intent {
    val webClientId = context.getString(R.string.google_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    val client = GoogleSignIn.getClient(context, gso)
    client.signOut()
    return client.signInIntent
}

fun handleGoogleSignInResult(
    context: Context,
    data: Intent?,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken ?: run {
            onError(context.getString(R.string.google_token_error))
            return
        }
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance()
            .signInWithCredential(firebaseCredential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo?.isNewUser == true) {
                    authResult.user?.let { user ->
                        saveUserToFirestore(
                            uid = user.uid,
                            name = user.displayName ?: context.getString(R.string.default_user_name),
                            email = user.email ?: ""
                        )
                    }
                }
                authResult.user?.let { onSuccess(it) }
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Firebase error: ${e.message}")
                onError(context.getString(R.string.google_signin_firebase_error, e.localizedMessage))
            }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "ApiException: ${e.statusCode} - ${e.message}")
        if (e.statusCode != 12501) {
            onError(context.getString(R.string.google_signin_error, e.statusCode))
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
