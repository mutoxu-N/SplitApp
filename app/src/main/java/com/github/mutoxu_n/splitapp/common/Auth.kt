package com.github.mutoxu_n.splitapp.common

import com.github.mutoxu_n.splitapp.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object Auth {
    val auth = Firebase.auth

    private var _token: String? = null
        set(value) {
            field = value
            onTokenChangedListeners.forEach { it.onTokenChanged(token) }
        }

    val token: String? get() = _token
    val isLoggedIn: Boolean get() = _token != null


    init {
        // デバッグ時
//        if(BuildConfig.DEBUG) {
//            auth.useEmulator("10.0.2.2", 9099)
//            logoutIfInvalid()
//        }

        // トークンが変更されたとき
        auth.addIdTokenListener { a: FirebaseAuth ->
            if(a.currentUser == null) {
                _token = null
                return@addIdTokenListener
            }

            // トークンを取得できた場合はtokenを保存する
            a.currentUser!!.getIdToken(false).addOnCompleteListener {
                if(it.isSuccessful) {
                    _token = it.result.token
                }
            }
        }

        login()
    }

    // ログイン
    fun login() {
        auth.signInAnonymously()
    }

    // ログアウト
    fun logout() {
        auth.signOut()
        _token = null
    }

    // 端末のログイン状態が無効のときにログアウトする
    private fun logoutIfInvalid() {
        auth.currentUser?.getIdToken(false)?.addOnCompleteListener {
            // ローカルでログイン済 かつ Firebase Auth で認証情報を取得できないときはログアウト
            if(!it.isSuccessful && auth.uid != null) logout()
        }
    }

    // イベントリスナ
    fun interface OnTokenChangedListener { fun onTokenChanged(token: String?) }
    private val onTokenChangedListeners = mutableListOf<OnTokenChangedListener>()
    fun addOnTokenChangedListener(listener: OnTokenChangedListener) { onTokenChangedListeners.add(listener) }
    fun removeOnTokenChangedListener(listener: OnTokenChangedListener) { onTokenChangedListeners.remove(listener) }
}
