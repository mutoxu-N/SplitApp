package com.github.mutoxu_n.splitapp.firebase

import android.util.Log
import com.github.mutoxu_n.splitapp.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Auth {
    val auth = Firebase.auth

    private var _isSignedIn = false
    val isSignedIn: Boolean get() = _isSignedIn


    companion object {
        private const val TAG = "Auth"
        private var _instance: Auth? = null

        fun get(): Auth {
            if(_instance == null) _instance = Auth()
            return _instance!!
        }
    }

    init {
        if(_instance != null)
            throw RuntimeException("Use get() method to get the single instance of this class.")

        if(BuildConfig.DEBUG)
            auth.useEmulator("10.0.2.2", 9099)

    }

    fun signIn() {
        auth
            .signInAnonymously()
            .addOnCompleteListener { task ->
                Log.e(TAG, "signInAnonymously: ${task.isSuccessful}")
                Log.e(TAG, "signInAnonymously: ${auth.uid}")

            }
    }

}
