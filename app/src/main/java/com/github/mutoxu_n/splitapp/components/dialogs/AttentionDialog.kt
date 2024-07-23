package com.github.mutoxu_n.splitapp.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@Composable
fun AttentionDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    dismissText: String? = stringResource(id = R.string.button_cancel),
    onConfirm: () -> Unit,
    confirmText: String? = stringResource(id = R.string.button_confirm),
) {
    // ボタンが両方表示されてないときは表示しない
    if(dismissText == null && confirmText == null) { onDismiss() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            confirmText?.let {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = it)
                }
            }
        },
        dismissButton = {
            dismissText?.let {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = it)
                }
            }
        },
        title = { Text(text = title,) },
        text = { Text(text = message) },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AttentionDialogPreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                AttentionDialog(
                    title = "情報表示ダイアログ",
                    message = "どんな情報を表示しているか, どんな意思決定を要求しているかの詳細をここに表示する. ",
                    onDismiss = {},
                    onConfirm = {},
                    dismissText = "キャンセル",
                    confirmText = "決定",
                )
            }
        }
    }
}