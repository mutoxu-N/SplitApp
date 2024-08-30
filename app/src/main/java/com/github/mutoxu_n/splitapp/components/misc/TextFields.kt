package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme

@Composable
fun DisplayNameTextField(
    initialDisplayName: String?,
    onValueChanged: (String, Boolean, Boolean) -> Unit,
) {
    var displayName by rememberSaveable { mutableStateOf(initialDisplayName ?: "") }
    var isDisplayNameError by rememberSaveable { mutableStateOf(initialDisplayName.isNullOrBlank()) }
    var saveDisplayName by rememberSaveable { mutableStateOf(false) }
    onValueChanged(displayName, isDisplayNameError, saveDisplayName)

    Column(
        modifier = Modifier
            .padding(horizontal = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(7.dp, 0.dp)
                .fillMaxWidth(),
            value = displayName,
            onValueChange = {
                isDisplayNameError = it.isBlank()
                displayName = it
                onValueChanged(displayName, isDisplayNameError, saveDisplayName)
            },
            isError = isDisplayNameError,
            maxLines = 1,
            label = { Text(text = "表示名") },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Checkbox(
                checked = saveDisplayName,
                onCheckedChange = {
                    saveDisplayName = it
                    onValueChanged(displayName, isDisplayNameError, saveDisplayName)
                }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        saveDisplayName = !saveDisplayName
                        onValueChanged(displayName, isDisplayNameError, saveDisplayName)
                    },
                text = "この名前を記録する",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewDisplayNameTextField() {
    SplitAppTheme {
        DisplayNameTextField(initialDisplayName = "太郎") { _, _, _-> }
    }
}