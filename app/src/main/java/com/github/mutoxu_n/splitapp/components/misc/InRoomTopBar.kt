package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@Composable
fun InRoomTopBar(
    title: String,
    onBackClicked: () -> Unit = {},
) {
    OutRoomTopBar(
        title = title,
        onBackClicked = { onBackClicked() },
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