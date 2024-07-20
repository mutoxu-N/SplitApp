package com.github.mutoxu_n.splitapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.github.mutoxu_n.splitapp.components.settings.RoomIdDisplay
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
                    Column(
//                        modifier = Modifier
//                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LogoDisplay(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "uid: $uid"
                        )


                        Button(onClick = { signIn() }) {
                            Text(
                                text = "Sign In"
                            )
                        }

                        Button(onClick = { Auth.signOut() }) {
                            Text(
                                text = "Sign Out"
                            )
                        }

                        Button(onClick = {
                            val intent = Intent(this@MainActivity, OssLicensesMenuActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text(
                                text = "licences"
                            )
                        }

//                        val writable = Settings(
//                            name = "writable",
//                            splitUnit = SplitUnit.TEN,
//                            permissionReceiptEdit = Role.OWNER,
//                            permissionReceiptCreate = Role.NORMAL,
//                            onNewMemberRequest = RequestType.ALWAYS,
//                            acceptRate = 30,
//                        )
//                        SettingsEditor(
//                            settings = writable,
//                            isReadOnly = false
//                        )
                        
//                        RoomIdDisplay(
//                            roomId = "AB12C3",
//                            onCopyClicked = {
//                                // Copy RoomId
//                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                                val clip = ClipData.newPlainText(getString(R.string.secret_clipboard_room_id), it)
//                                clipboard.setPrimaryClip(clip)
//                                Toast.makeText(this@MainActivity, "ルームID($it)をコピーしました", Toast.LENGTH_SHORT).show()
//                            },
//                            onShareClicked = {
//                                // Sharesheet 表示
//                                val intent = Intent().apply {
//                                    action = Intent.ACTION_SEND
//                                    putExtra(Intent.EXTRA_TEXT, it)
//                                    type = "text/plain"
//                                }
//                                val shareIntent  = Intent.createChooser(intent, null)
//                                startActivity(shareIntent)
//                            }
//                        )

                        // 動作テスト用
//                        Row(
//                            horizontalArrangement = Arrangement.Center,
//                        ) {
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
//
//                            Spacer(modifier = Modifier.size(16.dp))
//                            Button(onClick = {
//                                lifecycleScope.launch {
//                                    val res = API().reset()
//                                    Log.e(TAG, "reset: $res")
//                                }
//                            }) {
//                                Text(
//                                    text = "reset"
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.size(16.dp))
//                            Button(onClick = {
//                                lifecycleScope.launch {
//                                    val res = API().roomCreate(SettingsModel(
//                                        name = "sample room",
//                                        splitUnit = 10,
//                                        permissionReceiptEdit = Role.OWNER.toString(),
//                                        permissionReceiptCreate = Role.NORMAL.toString(),
//                                        onNewMemberRequest = "always", acceptRate = 50,
//                                    ))
//                                    Log.e(TAG, "create: $res")
//                                }
//                            }) {
//                                Text(
//                                    text = "create"
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.size(16.dp))
//                            Button(onClick = {
//                                lifecycleScope.launch {
//                                    val res = API().roomJoin("AB12C3")
//                                    Log.e(TAG, "join: ${res}")
//                                }
//                            }) {
//                                Text(
//                                    text = "join"
//                                )
//                            }
//                        }
                    }
                }
            }
        }
    }

    private fun signIn() {
        Auth.signIn()
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