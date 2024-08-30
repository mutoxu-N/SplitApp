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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.components.misc.DisplayNameTextField
import com.github.mutoxu_n.splitapp.components.misc.OutRoomTopBar
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.Settings
import kotlinx.coroutines.launch

class RoomCreateActivity : ComponentActivity() {

    companion object {
        private const val TAG = "RoomCreateActivity"

        fun launch(
            context: Context,
            launcher: ActivityResultLauncher<Intent>? = null
        ) {
            // Intent作成
            val intent = Intent(context, RoomCreateActivity::class.java)
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
                val roomId: String? by App.roomId.collectAsState()

                Scaffold(
                    topBar = {
                        OutRoomTopBar(
                            title = stringResource(R.string.appbar_room_create),
                            onBackClicked = { finish() }
                        )
                     },
                    modifier = Modifier.fillMaxSize()

                ) { innerPadding ->
                    LaunchedEffect(key1 = roomId) {
                        if(roomId != null) startInRoomActivity()
                    }

                    Screen(
                        modifier = Modifier.padding(innerPadding),
                        onSettingsChange = {
                            lifecycleScope.launch {
                                onCreateRoom(it)
                            }
                        },
                        onDisplayNameChanged = { name, isError, saveName ->
                            if(!isError) {
                                App.updateDisplayName(name)
                                if(saveName)
                                    App.saveDisplayName(name)
                            }
                        },
                        initialDisplayName = App.displayName.value ?: ""
                    )
                }
            }
        }
    }

    private suspend fun onCreateRoom(settings: Settings) {
        Log.e(TAG, "onCreateRoom: $settings")
        API().createRoom(settings)
    }

    private fun startInRoomActivity() {
        InRoomActivity.launch(context = this)
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    onSettingsChange: (Settings) -> Unit,
    initialDisplayName: String = "",
    onDisplayNameChanged: (String, Boolean, Boolean) -> Unit,
) {
    var isDisplayNameError by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        DisplayNameTextField(initialDisplayName = initialDisplayName) { name, isError, saveName ->
            onDisplayNameChanged(name, isError, saveName)
            isDisplayNameError = isError
        }
        SettingsEditor(
            settings = Settings.Default,
            onSettingsChange = {
                onSettingsChange(it)
            },
            isReadOnly = false,
            isActive = !isDisplayNameError,
            saveButtonText = stringResource(R.string.button_create_room)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    SplitAppTheme {
        Scaffold(
            topBar = {
                OutRoomTopBar(
                    title = stringResource(R.string.appbar_room_create),
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Screen(
                modifier = Modifier.padding(innerPadding),
                onSettingsChange = {},
                onDisplayNameChanged = {_, _, _ ->}
            )
        }
    }
}