package com.github.mutoxu_n.splitapp.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@Composable
fun SettingsEditor(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = true,
    settings: Settings,
    onSettingsChange: (Settings) -> Unit = {},
    saveButtonText: String = "ルーム設定を保存する"
) {
    Column(
        modifier = modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var roomName by rememberSaveable { mutableStateOf(settings.name) }
        var splitUnit by rememberSaveable { mutableIntStateOf(settings.splitUnit) }
        var permissionReceiptCreate by rememberSaveable { mutableStateOf(settings.permissionReceiptCreate) }
        var permissionReceiptEdit by rememberSaveable { mutableStateOf(settings.permissionReceiptEdit) }
        var onNewMemberRequest by rememberSaveable { mutableStateOf(settings.onNewMemberRequest) }
        var acceptRate by rememberSaveable { mutableIntStateOf(settings.acceptRate) }
        var isError by rememberSaveable { mutableStateOf(settings.name.isBlank()) }

        // ルーム名
        if(isReadOnly) {
            DisplayRow(name = "ルーム名", value = settings.name)
        } else {
            OutlinedTextField(
                modifier = modifier
                    .padding(7.dp, 0.dp)
                    .fillMaxWidth(),
                value = settings.name,
                label = { Text(text = "ルーム名") },
                onValueChange = {
                    isError = it.isBlank()
                    roomName = it
                },
                isError = isError,
            )
        }
        DisplayRow(
            name = "割り勘単位",
            value = settings.splitUnit,
            isReadOnly = isReadOnly,
            onValueChange = {
                splitUnit = it
            }
        )
        DisplayRow(
            name = "レシート作成権限",
            value = settings.permissionReceiptCreate,
            isReadOnly = isReadOnly,
            onValueChange = {
                permissionReceiptCreate = it
            }
        )
        DisplayRow(
            name = "レシート編集権限",
            value = settings.permissionReceiptEdit,
            isReadOnly = isReadOnly,
            onValueChange = {
                permissionReceiptEdit = it
            }
        )
        DisplayRow(
            name = "新規メンバー",
            value = settings.onNewMemberRequest,
            isReadOnly = isReadOnly,
            onValueChange = {
                onNewMemberRequest = it
            }
        )

        DisplayRow(
            name = "承認レート",
            value = settings.acceptRate,
            suffix = "%",
            isReadOnly = isReadOnly,
            onValueChange = {
                acceptRate = it
            }
        )

        if(!isReadOnly) {
            Slider(
                value = acceptRate.toFloat(),
                onValueChange = { acceptRate = it.toInt() },
                valueRange = 0f..100f,
            )
        }

        HorizontalDivider()
        if(!isReadOnly) {
            Button(
                modifier = modifier,
                onClick = {
                    onSettingsChange(
                        Settings(
                            name = roomName,
                            splitUnit = splitUnit,
                            permissionReceiptCreate = permissionReceiptCreate,
                            permissionReceiptEdit = permissionReceiptEdit,
                            onNewMemberRequest = onNewMemberRequest,
                            acceptRate = acceptRate
                        )
                    )
                },
                content = {
                    Text(text = saveButtonText)
                }
            )
        }

    }
}


@Composable
private fun <T> DisplayRow(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = true,
    name: String,
    value: T,
    suffix: String = "",
    onValueChange: (T) -> Unit = {}
) {
    var newValue by rememberSaveable { mutableStateOf(value) }
    var isDialogShown by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.weight(1f),
            text = name,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = modifier.size(3.dp))
        Text(
            modifier = modifier
                .clickable { isDialogShown = true },
            text = "$value$suffix",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if(isReadOnly) TextDecoration.None else TextDecoration.Underline,
        )
    }

    if(isDialogShown) {
        ValueChangeDialog(
            title = name,
            value = value,
            onDismiss = { isDialogShown = false },
            onConfirm = {
                newValue = it
                onValueChange(newValue)
                isDialogShown = false
            },
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsEditorPreview() {
    val readOnly = Settings(
        name = "readOnly",
        splitUnit = 10,
        permissionReceiptEdit = "OWNER",
        permissionReceiptCreate = "NORMAL",
        onNewMemberRequest = "always",
        acceptRate = 50,
    )
    val writable = Settings(
        name = "writable",
        splitUnit = 10,
        permissionReceiptEdit = "OWNER",
        permissionReceiptCreate = "NORMAL",
        onNewMemberRequest = "always",
        acceptRate = 30,
    )
    SplitAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(0.dp, 10.dp)
            ) {
                HorizontalDivider(color= Color(0xFFFF0000))
                SettingsEditor(
                    settings = readOnly
                )
                HorizontalDivider(color= Color(0xFFFF0000))

                Spacer(modifier = Modifier.size(10.dp))
                HorizontalDivider(color= Color(0xFF0000FF))
                SettingsEditor(
                    settings = writable,
                    isReadOnly = false
                )
                HorizontalDivider(color= Color(0xFF0000FF))
            }
        }
    }
}
