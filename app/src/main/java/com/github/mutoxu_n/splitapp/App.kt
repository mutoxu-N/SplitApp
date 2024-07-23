package com.github.mutoxu_n.splitapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class App: Application() {
    companion object {
        private var _appContext: Context? = null
        val appContext: Context? get() = _appContext

        private const val SHARED_PREFERENCES_FILENAME = "SplitApp"
        // ルームIDを取得・設定
        private const val SHARED_PREFERENCES_KEY_ROOM_ID = "roomId"
        var roomId: String?
            get() {
                val sharedPref =
                    appContext?.getSharedPreferences(
                        SHARED_PREFERENCES_FILENAME,
                        MODE_PRIVATE
                    ) ?: return null
                return sharedPref.getString(SHARED_PREFERENCES_KEY_ROOM_ID, null)
            }
            set(value) {
                val sharedPref =
                    appContext?.getSharedPreferences(
                        SHARED_PREFERENCES_FILENAME,
                        MODE_PRIVATE
                    ) ?: return
                val editor = sharedPref.edit()

                // 値の保存
                if(value == null) {
                    editor.remove(SHARED_PREFERENCES_KEY_ROOM_ID).apply()

                } else {
                    editor.putString(SHARED_PREFERENCES_KEY_ROOM_ID, value).apply()
                }
            }

        // 表示名を取得・設定
        private const val SHARED_PREFERENCES_KEY_DISPLAY_NAME = "displayName"
        var displayName: String?
            get() {
                val sharedPref =
                    appContext?.getSharedPreferences(
                        SHARED_PREFERENCES_FILENAME,
                        MODE_PRIVATE
                    ) ?: return null
                return sharedPref.getString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, null)
            }
            set(value) {
                val sharedPref =
                    appContext?.getSharedPreferences(
                        SHARED_PREFERENCES_FILENAME,
                        MODE_PRIVATE
                    ) ?: return
                val editor = sharedPref.edit()

                // 値の保存
                if(value == null) {
                    editor.remove(SHARED_PREFERENCES_KEY_DISPLAY_NAME).apply()

                } else {
                    editor.putString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, value).apply()
                }
            }

        // ルームIDのバリデーション
        fun validateRoomID(roomId: String): Boolean {
            // 6文字
            if(roomId.length != 6) return false

            // アルファベット(大文字) or 数字
            for(c in roomId.toCharArray())
                if(!c.isLetterOrDigit() || c.isLowerCase()) return false

            return true
        }
    }

    override fun onCreate() {
        super.onCreate()
        _appContext = applicationContext
        FirebaseApp.initializeApp(applicationContext)
    }

}