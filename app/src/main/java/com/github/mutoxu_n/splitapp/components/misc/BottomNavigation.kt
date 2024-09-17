package com.github.mutoxu_n.splitapp.components.misc

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme

sealed class InRoomNavItem(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    data object Receipt: InRoomNavItem("receipt", R.string.bottom_nav_item_receipts, Icons.AutoMirrored.Filled.ReceiptLong)
    data object Info: InRoomNavItem("info", R.string.bottom_nav_item_info, Icons.Outlined.Info)
    data object Setting: InRoomNavItem("setting", R.string.bottom_nav_item_settings, Icons.Default.Settings)
}

@Composable
fun BottomNavigation(
    controller: NavHostController
) {
    val items = listOf(
        InRoomNavItem.Receipt,
        InRoomNavItem.Info,
        InRoomNavItem.Setting,
    )

    NavigationBar {
        val navBackStackEntry by controller.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val selected = item.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = {
                    controller.navigate(item.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Column(
                        Modifier
                            .animateContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                        )
                        if(selected) {
                            Text(
                                text = stringResource(id = item.title),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BottomNavigationPreview() {
    SplitAppTheme {
        val controller = rememberNavController()
        Scaffold(
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
                composable(InRoomNavItem.Receipt.route) {}
                composable(InRoomNavItem.Info.route) {}
                composable(InRoomNavItem.Setting.route) {}
            }
        }
    }
}