package com.github.mutoxu_n.splitapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.misc.BottomNavigation
import com.github.mutoxu_n.splitapp.components.misc.InRoomNavItem
import com.github.mutoxu_n.splitapp.components.misc.InRoomTopBar

class InRoomActivity : ComponentActivity() {
    private var roomName by mutableStateOf("〇〇キャンプ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                Screen(roomName = roomName)
            }
        }
    }
}

@Composable
private fun Screen(
    roomName: String,
) {
    val controller = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InRoomTopBar(
                title = roomName,
            )
        },
        bottomBar = {
            BottomNavigation(
                controller = controller
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = controller,
            startDestination = InRoomNavItem.Receipt.route,
        ) {
            composable(InRoomNavItem.Receipt.route) {
                ReceiptScreen(
                )
            }
            composable(InRoomNavItem.Info.route) {
                InfoScreen(
                )
            }
            composable(InRoomNavItem.Setting.route) {
                SettingScreen(
                )
            }
        }
    }
}

@Composable
private fun ReceiptScreen(
    modifier: Modifier = Modifier,
) {
    Text("ReceiptScreen")
}

@Composable
private fun InfoScreen(
    modifier: Modifier = Modifier,
) {
    Text("InfoScreen")
}

@Composable
private fun SettingScreen(
    modifier: Modifier = Modifier,
) {
    Text("SettingScreen")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InRoomActivityPreview() {
    SplitAppTheme {
        SplitAppTheme {
            Screen(roomName = "〇〇キャンプ")
        }
    }
}