package com.github.mutoxu_n.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.ReceiptModel
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SettingsModel
import com.github.mutoxu_n.splitapp.models.SplitUnit
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var uid: String? by mutableStateOf(null)

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Auth.get().addOnTokenChangedListener {
            uid = Auth.get().auth.uid
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

                        val writable = Settings(
                            name = "writable",
                            splitUnit = SplitUnit.TEN,
                            permissionReceiptEdit = Role.OWNER,
                            permissionReceiptCreate = Role.NORMAL,
                            onNewMemberRequest = RequestType.ALWAYS,
                            acceptRate = 30,
                        )

                        Button(onClick = { signIn() }) {
                            Text(
                                text = "Sign In"
                            )
                        }

                        Button(onClick = { Auth.get().signOut() }) {
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

                        SettingsEditor(
                            settings = writable,
                            isReadOnly = false
                        )

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
        val auth = Auth.get()
        auth.signIn()
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