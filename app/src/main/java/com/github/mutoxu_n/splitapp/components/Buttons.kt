package com.github.mutoxu_n.splitapp.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DoneButton(
    modifier: Modifier = Modifier,
    onComfirmed: () -> Unit = {},
    enabled: Boolean = true,
    doneButtonText: String,
) {
    Button(
        modifier = modifier,
        onClick = {
            if(enabled) return@Button
            onComfirmed()
        },
        content = {
            Text(text = doneButtonText)
        },
        enabled = enabled
    )
}