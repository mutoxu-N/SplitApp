package com.github.mutoxu_n.splitapp.components.misc

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.mutoxu_n.splitapp.R

sealed class InRoomNavItem(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    data object Receipt: InRoomNavItem("receipt", R.string.bottom_nav_item_receipts, Icons.Outlined.Receipt)
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                        )
                        if(selected) {
                            Text(
                                text = stringResource(id = item.title),
                            )
                        }
                    }
                }
            )
        }
    }
}