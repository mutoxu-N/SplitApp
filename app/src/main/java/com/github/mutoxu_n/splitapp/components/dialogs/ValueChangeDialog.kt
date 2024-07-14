package com.github.mutoxu_n.splitapp.components.dialogs

import androidx.compose.runtime.Composable

@Composable
fun <T> ValueChangeDialog(
    title: String,
    value: T,
    onDismiss: () -> Unit,
    onConfirm: (T) -> Unit
) {
    // TODO: 値を変更するためのダイアログ
}
