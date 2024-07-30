package com.github.mutoxu_n.splitapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.misc.OutRoomTopBar
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.Settings

class RoomCreateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
                        onSettingsChange = {}
                    )

                }
            }
        }
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    onSettingsChange: (Settings) -> Unit,
) {
    Box(modifier = modifier) {
        SettingsEditor(
            settings = Settings.Default,
            onSettingsChange = {
                onSettingsChange(it)
            },
            isReadOnly = false,
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
                onSettingsChange = {}
            )
        }
    }
}