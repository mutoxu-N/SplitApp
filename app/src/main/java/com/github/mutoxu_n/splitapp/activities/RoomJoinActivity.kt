package com.github.mutoxu_n.splitapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.components.misc.DisplayNameTextField
import com.github.mutoxu_n.splitapp.components.misc.OutRoomTopBar
import kotlinx.coroutines.launch

class RoomJoinActivity : ComponentActivity() {
    private val displayName: String? = App.displayName.value

    companion object {
        private const val TAG = "RoomJoinActivity"

        fun launch(
            context: Context,
            launcher: ActivityResultLauncher<Intent>? = null
        ) {
            // Intent作成
            val intent = Intent(context, RoomJoinActivity::class.java)
            val args = Bundle()
            intent.putExtras(args)

            // launch
            if (launcher == null) context.startActivity(intent)
            else launcher.launch(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if(App.validateRoomID(App.roomId.value) && !displayName.isNullOrBlank()) {
//            lifecycleScope.launch {
//                joinRoom(
//                    roomId = App.roomId.value!!,
//                    displayName = displayName,
//                    saveDisplayName = false,
//                )
//            }
//        }


        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                val roomId: String? by App.roomId.collectAsState()
                var waitForInput by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(key1 = roomId, key2 = waitForInput) {
                    if(!waitForInput && roomId != null) {
                        startInRoomActivity()
                    }
                }

                Scaffold(
                    topBar = {
                        OutRoomTopBar(
                            title = "ルームに参加する",
                            onBackClicked = { finish() },
                        )
                    },
                    modifier = Modifier.fillMaxSize(),

                ) { innerPadding ->
                    Surface {
                        Screen(
                            modifier = Modifier
                                .padding(innerPadding),
                            initialDisplayName = displayName ?: "",
                            initialRoomId = roomId ?: "",
                            onJoinClicked = { roomId, displayName, saveDisplayName ->
                                waitForInput = false
                                lifecycleScope.launch {
                                    joinRoom(roomId, displayName, saveDisplayName)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private suspend fun joinRoom(roomId: String, displayName: String, saveDisplayName: Boolean) {
        App.updateDisplayName(displayName)
        if(saveDisplayName)
            App.saveDisplayName(displayName)

        App.updateRoomId(null)

        Log.e("API", "JOIN")
        API().joinRoom(roomId, displayName)
    }

    private fun startInRoomActivity() {
        InRoomActivity.launch(
            context = this@RoomJoinActivity,
        )
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    initialDisplayName: String,
    initialRoomId: String,
    onJoinClicked: (String, String, Boolean) -> Unit,
) {
    var displayName by rememberSaveable { mutableStateOf(initialDisplayName) }
    var isDisplayNameError by rememberSaveable { mutableStateOf(false) }
    var roomId by rememberSaveable { mutableStateOf(initialRoomId) }
    var isRoomIdError by rememberSaveable { mutableStateOf(!App.validateRoomID(roomId)) }
    var saveDisplayName by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(15.dp))
        DisplayNameTextField(initialDisplayName = initialDisplayName) { newDisplayName, isError, newSaveDisplayName ->
            displayName = newDisplayName
            isDisplayNameError = isError
            saveDisplayName = newSaveDisplayName
        }

        Spacer(modifier = Modifier.size(30.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = roomId,
            onValueChange = {
                if(it.length <= 6) {
                    roomId = it.uppercase()
                    isRoomIdError = !App.validateRoomID(roomId)
                }
            },
            isError = isRoomIdError,
            maxLines = 1,
            label = { Text(text = "ルームID") },
            supportingText = {
                if(isRoomIdError) Text(text = "ルームIDは6桁の大文字英数字です")
            },
        )

        // ルーム参加ボタン
        Button(
            onClick = { onJoinClicked(roomId, displayName, saveDisplayName) },
            enabled = !isDisplayNameError && !isRoomIdError && displayName.isNotBlank(),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 7.dp),
                text = "ルームに参加"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    SplitAppTheme {
        Scaffold(
            topBar = {
                OutRoomTopBar(
                    title = "ルームに参加する",
                    onBackClicked = {},
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface {
                Screen(
                    modifier = Modifier.padding(innerPadding),
                    initialDisplayName = "太郎",
                    initialRoomId = "AB12C3",
                    onJoinClicked = { _, _, _ -> },
                )
            }
        }
    }
}