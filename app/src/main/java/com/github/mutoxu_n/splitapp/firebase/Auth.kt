package com.github.mutoxu_n.splitapp.firebase

import android.util.Log
import com.github.mutoxu_n.splitapp.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Auth {
    val auth = Firebase.auth

    private var _token: String? = null

    val token: String? get() = _token
    val isSignedIn: Boolean get() = _token != null

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

        // デバッグ時
        if(BuildConfig.DEBUG) {
            auth.useEmulator("10.0.2.2", 9099)
            signOutIfInvalid()
        }

        // トークンが変更されたとき
        auth.addIdTokenListener { a: FirebaseAuth ->
            Log.e(TAG, "addAuthStateListener: ${a.currentUser}")
            if(a.currentUser == null) {
                _token = null
                return@addIdTokenListener
            }

            // トークンを取得できた場合はtokenを保存する
            a.currentUser!!.getIdToken(false).addOnCompleteListener {
                Log.e(TAG, "getIdToken(${it.isSuccessful}): ${it.result.token}")
                if(it.isSuccessful) {
                    _token = it.result.token
                }
            }
        }



        signIn()

    }

    fun signIn() {
        auth.signInAnonymously()
    }

    fun signOut() {
        auth.signOut()
        _token = null
    }

    // 端末のログイン状態が無効のときにサインアウトする
    private fun signOutIfInvalid() {
        auth.currentUser?.getIdToken(false)?.addOnCompleteListener {
            // ローカルでログイン済 かつ Firebase Auth で認証情報を取得できないときはサインアウト
            if(!it.isSuccessful && auth.uid != null) signOut()
        }
    }

}
