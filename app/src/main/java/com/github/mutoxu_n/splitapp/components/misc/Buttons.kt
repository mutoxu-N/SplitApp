package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DoneButton(
    modifier: Modifier = Modifier,
    onConfirmed: () -> Unit = {},
    enabled: Boolean = true,
    doneButtonText: String,
) {
    Button(
        modifier = modifier,
        onClick = {
            if(enabled) return@Button
            onConfirmed()
        },
        content = {
            Text(text = doneButtonText)
        },
        enabled = enabled
    )
}