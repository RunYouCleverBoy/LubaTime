package com.playground.liquidtime

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepo {
    private lateinit var auth: FirebaseAuth

    fun create() {
        auth = Firebase.auth
    }

    suspend fun signUp(email: String, password: String): AuthenticationResult {
        auth = Firebase.auth
        auth.currentUser?.apply {
            reload()
            return AuthenticationResult.AlreadyLoggedIn
        }

        return suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        if (user != null) {
                            cont.resume(AuthenticationResult.NewUser(user))
                        } else {
                            cont.resume(AuthenticationResult.Failure(task.exception))
                        }
                    } else {
                        cont.resume(AuthenticationResult.Failure(task.exception))
                    }
                }
                .addOnCanceledListener { cont.resume(AuthenticationResult.Cancelled) }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    sealed class AuthenticationResult {
        data class NewUser(val newUser: FirebaseUser): AuthenticationResult()
        data class Failure(val reason: Exception?) : AuthenticationResult()
        object Cancelled : AuthenticationResult()
        object AlreadyLoggedIn : AuthenticationResult()
    }

    companion object {
        private const val TAG = "Auth"
    }
}