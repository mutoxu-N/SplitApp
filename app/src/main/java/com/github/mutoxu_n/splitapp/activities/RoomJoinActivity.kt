package com.github.mutoxu_n.splitapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.misc.OutRoomTopBar

class RoomJoinActivity : ComponentActivity() {
    private var roomId: String = App.roomId ?: ""
    private var displayName: String = App.displayName ?: ""

    init {
        if(App.validateRoomID(roomId) && displayName.isNotBlank()) {
            startInRoomActivity()
        }
    }

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
        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
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
                            initialDisplayName = displayName,
                            initialRoomId = roomId,
                            onJoinClicked = { roomId, displayName, saveDisplayName ->
                                joinRoom(roomId, displayName, saveDisplayName)
                            },
                        )
                    }
                }
            }
        }
    }

    private fun joinRoom(roomId: String, displayName: String, saveDisplayName: Boolean) {
        App.updateRoomId(roomId)
        if(saveDisplayName)
            App.updateDisplayName(displayName)
        startInRoomActivity()
    }

    private fun startInRoomActivity() {
        // TODO: InRoomActivityに遷移
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
        OutlinedTextField(
            modifier = Modifier
                .padding(7.dp, 0.dp)
                .fillMaxWidth(),
            value = displayName,
            onValueChange = {
                isDisplayNameError = it.isBlank()
                displayName = it
            },
            isError = isDisplayNameError,
            maxLines = 1,
            label = { Text(text = "表示名") },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Checkbox(
                checked = saveDisplayName,
                onCheckedChange = { saveDisplayName = it }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { saveDisplayName = !saveDisplayName },
                text = "この名前を記録する",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.size(30.dp))
        OutlinedTextField(
            modifier = Modifier
                .padding(7.dp, 0.dp)
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