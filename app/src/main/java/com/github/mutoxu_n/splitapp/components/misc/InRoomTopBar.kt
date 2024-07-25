package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InRoomTopBar(
    title: String,
    onBackClicked: () -> Unit = {},
) {
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
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InRoomTopBarPreview() {
    SplitAppTheme {
        Scaffold(
            topBar = { InRoomTopBar(
                title = "ルーム名: 〇〇キャンプ",
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