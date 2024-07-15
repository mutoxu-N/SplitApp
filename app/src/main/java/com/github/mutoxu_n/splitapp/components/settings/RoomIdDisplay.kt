package com.github.mutoxu_n.splitapp.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@Composable
fun RoomIdDisplay(
    modifier: Modifier = Modifier,
    roomId: String,
) {
    Column(
        modifier = modifier
            .padding(10.dp, 0.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "ルームID",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = roomId,
            style = MaterialTheme.typography.displayLarge
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun DisplayPreview() {
    SplitAppTheme {
        Surface {
            RoomIdDisplay(
                modifier = Modifier.padding(0.dp, 10.dp),
                roomId = "AB12C3",
            )
        }
    }
}