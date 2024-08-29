package com.github.mutoxu_n.splitapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.components.dialogs.AttentionDialog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var uid: String? by mutableStateOf(null)
    private var isLogoutDialogShown: Boolean by mutableStateOf(false)

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Auth.addOnTokenChangedListener {
            uid = Auth.auth.uid
        }

        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            LogoDisplay(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                            )

                            if (Auth.isLoggedIn) {
                                // ログイン済
                                Button(onClick = { startRoomJoinActivity() })
                                { Text(text = stringResource(R.string.button_join_room)) }


                                OutlinedButton(onClick = { startRoomCreateActivity() })
                                { Text(text = stringResource(R.string.button_create_room)) }

                                // ログアウト
                                TextButton(onClick = { isLogoutDialogShown = true })
                                { Text(text = stringResource(R.string.term_logout)) }

                            } else {
                                // 未ログイン
                                Button(onClick = { login() })
                                { Text(text = stringResource(R.string.term_login)) }
                            }

                            // デバッグモード
                            if (BuildConfig.DEBUG) {
                                Spacer(modifier = Modifier.size(16.dp))
                                Text(
                                    text = "UID: $uid",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
//                                // リセットAPI
//                                Button(onClick = { lifecycleScope.launch { API().reset() } }) { Text(text = "リセット") }
                            }
                        }

                        // ライセンス表示
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.BottomCenter),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            TextButton(
                                onClick = {
                                    val intent = Intent(
                                        this@MainActivity,
                                        OssLicensesMenuActivity::class.java
                                    )
                                    startActivity(intent)
                                }) { Text(text = "ライセンス") }
                            Spacer(modifier = Modifier.size(20.dp))
                        }
                    }

                    if (isLogoutDialogShown) {
                        // ログアウト確認ダイアログ
                        AttentionDialog(
                            title = stringResource(R.string.logout_dialog_title),
                            message = stringResource(R.string.logout_dialog_message),
                            onDismiss = { isLogoutDialogShown = false },
                            onConfirm = {
                                isLogoutDialogShown = false
                                logout()
                            },
                            confirmText = stringResource(R.string.logout_dialog_confirm_text),
                        )
                    }
                }
            }
        }
    }

    init {
        val roomId = App.roomId.value
        if(Auth.isLoggedIn && roomId != null) {
            // ルームIDが設定されている場合
            startRoomJoinActivity()
        }
    }

    private fun login() {
        Auth.login()
    }

    private fun logout() {
        Auth.logout()
    }

    private fun startRoomJoinActivity() {
        Log.i(TAG, "RoomJoinActivity launched")
        App.loadDisplayName()
        RoomJoinActivity.launch(context = this)
    }

    private fun startRoomCreateActivity() {
        Log.i(TAG, "RoomCreateActivity launched")
        RoomCreateActivity.launch(context = this)
    }
}

@Composable
private fun LogoDisplay(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(LocalConfiguration.current.screenWidthDp.dp*0.8f),
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = null
    )
}