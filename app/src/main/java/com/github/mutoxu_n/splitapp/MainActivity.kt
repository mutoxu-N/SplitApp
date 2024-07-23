package com.github.mutoxu_n.splitapp

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
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class MainActivity : ComponentActivity() {
    private var uid: String? by mutableStateOf(null)

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

                            if (Auth.isSignedIn) {
                                // サインイン済
                                Button(onClick = { startRoomJoinActivity() })
                                    { Text(text = "ルームに参加",) }


                                OutlinedButton(onClick = { startRoomCreateActivity() })
                                    { Text(text = "ルームを作る",) }

                                // サインアウト
                                TextButton(onClick = { signOut() })
                                { Text(text = "サインアウト") }

                            } else {
                                // 未サインイン
                                Button(onClick = { signIn() })
                                { Text(text = "サインイン") }
                            }

                            // デバッグモード
                            Spacer(modifier = Modifier.size(16.dp))
                            if (BuildConfig.DEBUG) {
                                Text(
                                    text = "UID: $uid",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

//                            // 動作テスト用
//                            Button(onClick = {
//                                lifecycleScope.launch {
//                                    API().editReceipt("AB12C3", "RJBFyzAxoBYQfonE2u1T", ReceiptModel(
//                                        stuff = "Kei-SuperComputer",
//                                        paid = "sample member",
//                                        buyers = listOf("sample member"),
//                                        payment = 120_000,
//                                    ))
//                                }
//                            }) {
//                                Text(
//                                    text = "test"
//                                )
//                            }
//                            // 動作テスト用 終了
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
                                    val intent = Intent(this@MainActivity, OssLicensesMenuActivity::class.java)
                                    startActivity(intent)
                                }) { Text(text = "ライセンス") }
                            Spacer(modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    init {
        val roomId = App.roomId
        if(roomId != null) {
            // ルームIDが設定されている場合
            startRoomJoinActivity(roomId = roomId)
        }
    }

    private fun signIn() {
        Auth.signIn()
    }

    private fun signOut() {
        Auth.signOut()
    }

    private fun startRoomJoinActivity(roomId: String? = null) {
        Log.i(TAG, "RoomJoinActivity launched")
        // TODO: RoomJoinActivityに遷移
    }

    private fun startRoomCreateActivity() {
        Log.i(TAG, "RoomCreateActivity launched")
        // TODO: RoomCreateActivityに遷移
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