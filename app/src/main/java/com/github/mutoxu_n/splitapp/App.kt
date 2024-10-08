package com.github.mutoxu_n.splitapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class App: Application() {
    companion object {
        private var _appContext: Context? = null
        val appContext: Context? get() = _appContext

        private const val SHARED_PREFERENCES_FILENAME = "SplitApp"
        // ルームIDを取得・設定
        private const val SHARED_PREFERENCES_KEY_ROOM_ID = "roomId"
        private var _roomId: MutableStateFlow<String?> = MutableStateFlow(null)
        val roomId: StateFlow<String?> get() = _roomId

        // 表示名を取得・設定
        private const val SHARED_PREFERENCES_KEY_DISPLAY_NAME = "displayName"
        private var _displayName: MutableStateFlow<String?> = MutableStateFlow(null)
        val displayName: StateFlow<String?> get() = _displayName

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

        fun updateRoomId(roomId: String?): Boolean {
            if(validateRoomID(roomId)) {
                this._roomId.update { roomId }
                val sharedPref =
                    appContext?.getSharedPreferences(
                        SHARED_PREFERENCES_FILENAME,
                        MODE_PRIVATE,
                    ) ?: return false

                val editor = sharedPref.edit()
                if(_roomId.value == null) editor.remove(SHARED_PREFERENCES_KEY_ROOM_ID).apply()
                else editor.putString(SHARED_PREFERENCES_KEY_ROOM_ID, _roomId.value).apply()
                editor.apply()
                return true
            } else return false
        }

        fun updateDisplayName(displayName: String) {
            this._displayName.update { displayName }
        }


        fun saveDisplayName(name: String){
            val sharedPref =
                appContext?.getSharedPreferences(
                    SHARED_PREFERENCES_FILENAME,
                    MODE_PRIVATE,
                ) ?: return
            val editor = sharedPref.edit()
            if(_displayName.value == null) editor.remove(SHARED_PREFERENCES_KEY_DISPLAY_NAME).apply()
            else editor.putString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, _displayName.value).apply()
            editor.apply()

            updateDisplayName(name)
        }
    }

    override fun onCreate() {
        super.onCreate()
        _appContext = applicationContext
        FirebaseApp.initializeApp(applicationContext)


        val sharedPref =
            appContext?.getSharedPreferences(
                SHARED_PREFERENCES_FILENAME,
                MODE_PRIVATE
            )

        if(sharedPref == null) {
            _roomId.update { null }
            _displayName.update { null }

        } else {
            _roomId.update { sharedPref.getString(SHARED_PREFERENCES_KEY_ROOM_ID, null) }
            _displayName.update { sharedPref.getString(SHARED_PREFERENCES_KEY_DISPLAY_NAME, null) }
        }
    }
}