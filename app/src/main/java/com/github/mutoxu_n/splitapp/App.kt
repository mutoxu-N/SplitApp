package com.github.mutoxu_n.splitapp

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class App: Application(), AutoCloseable {
    companion object {
        private var _appContext: Context? = null
        val appContext: Context? get() = _appContext

        private const val SHARED_PREFERENCES_FILENAME = "SplitApp"
        // ルームIDを取得・設定
        private const val SHARED_PREFERENCES_KEY_ROOM_ID = "roomId"
        var roomId: MutableStateFlow<String?> = MutableStateFlow(null)
            private set

        // 表示名を取得・設定
        private const val SHARED_PREFERENCES_KEY_DISPLAY_NAME = "displayName"
        var displayName: MutableStateFlow<String?> = MutableStateFlow(null)
            private set

        // ルームIDのバリデーション
        fun validateRoomID(roomId: String?): Boolean {
            // null
            if(roomId == null) return false

            // 6文字
            if(roomId.length != 6) return false

            // アルファベット(大文字) or 数字
            for(c in roomId.toCharArray())
                if(!c.isLetterOrDigit() || c.isLowerCase()) return false

            return true
        }

        fun updateRoomId(roomId: String): Boolean {
            if(validateRoomID(roomId)) {
                this.roomId.update { roomId }
                return true
            } else return false
        }

        fun updateDisplayName(displayName: String) {
            this.displayName.update { displayName }
        }


        fun saveDisplayName(name: String){
            val sharedPref =
                appContext?.getSharedPreferences(
                    SHARED_PREFERENCES_FILENAME,
                    MODE_PRIVATE,
                ) ?: return
            val editor = sharedPref.edit()
            if(displayName.value == null) editor.remove(SHARED_PREFERENCES_KEY_DISPLAY_NAME).apply()
            else editor.putString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, displayName.value).apply()

            updateDisplayName(name)
        }

        fun loadDisplayName() {
            val sharedPref =
                appContext?.getSharedPreferences(
                    SHARED_PREFERENCES_FILENAME,
                    MODE_PRIVATE
                )
            displayName.update { sharedPref?.getString(SHARED_PREFERENCES_KEY_ROOM_ID, null) }
        }
    }

    init {
        val sharedPref =
            appContext?.getSharedPreferences(
                SHARED_PREFERENCES_FILENAME,
                MODE_PRIVATE
            )

        if(sharedPref == null) {
            roomId.update { null }
            displayName.update { null }

        } else {
            roomId.update { sharedPref.getString(SHARED_PREFERENCES_KEY_ROOM_ID, null) }
            displayName.update { sharedPref.getString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, null) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        _appContext = applicationContext
        FirebaseApp.initializeApp(applicationContext)
    }

    override fun close() {
        // 値の保存
        val sharedPref =
            appContext?.getSharedPreferences(
                SHARED_PREFERENCES_FILENAME,
                MODE_PRIVATE,
            ) ?: return
        val editor = sharedPref.edit()

        if(roomId.value == null) editor.remove(SHARED_PREFERENCES_KEY_DISPLAY_NAME).apply()
        else editor.putString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, roomId.value).apply()
    }
}