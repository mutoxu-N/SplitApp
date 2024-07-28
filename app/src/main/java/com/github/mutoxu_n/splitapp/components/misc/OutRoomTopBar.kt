package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutRoomTopBar(
    title: String,
    onBackClicked: () -> Unit = {},
    menu: List<String> = listOf(),
    onMenuClicked: (String) -> Unit = {},
) {
    var menuShown by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier
            .shadow(elevation = 2.dp),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onBackClicked() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if(menu.isNotEmpty()) {
                IconButton(onClick = { menuShown = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                    )

                    DropdownMenu(
                        expanded = menuShown,
                        onDismissRequest = { menuShown = false }
                    ) {
                        menu.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    menuShown = false
                                    onMenuClicked(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OutRoomTopBarPreview() {
    SplitAppTheme {
        Scaffold(
            topBar = { OutRoomTopBar(
                title = "ルームに参加",
                menu = listOf("ログアウト"),
            ) },
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}