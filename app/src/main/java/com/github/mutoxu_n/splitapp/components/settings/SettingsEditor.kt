package com.github.mutoxu_n.splitapp.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.misc.DoneButton
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SplitUnit

@Composable
fun SettingsEditor(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = true,
    settings: Settings,
    onSettingsChange: (Settings) -> Unit = {},
    saveButtonText: String = stringResource(R.string.button_save_settings),
    isActive: Boolean = true,
) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var roomName by rememberSaveable { mutableStateOf(settings.name) }
        var splitUnit by rememberSaveable { mutableStateOf(settings.splitUnit) }
        var permissionReceiptCreate by rememberSaveable { mutableStateOf(settings.permissionReceiptCreate) }
        var permissionReceiptEdit by rememberSaveable { mutableStateOf(settings.permissionReceiptEdit) }
        var onNewMemberRequest by rememberSaveable { mutableStateOf(settings.onNewMemberRequest) }
        var acceptRate by rememberSaveable { mutableIntStateOf(settings.acceptRate) }
        var isError by rememberSaveable { mutableStateOf(settings.name.isBlank()) }

        // ルーム名
        if (isReadOnly) {
            DisplayRow(name = stringResource(R.string.settings_room_name), value = roomName)
        } else {
            OutlinedTextField(
                modifier = modifier
                    .padding(7.dp, 0.dp)
                    .fillMaxWidth(),
                value = roomName,
                label = { Text(text = stringResource(id = R.string.settings_room_name)) },
                onValueChange = {
                    isError = it.isBlank()
                    roomName = it
                },
                maxLines = 1,
                isError = isError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            )
        }
        DisplayRow(
            name = stringResource(R.string.settings_split_unit),
            value = splitUnit,
            isReadOnly = isReadOnly,
            onValueChange = {
                splitUnit = it
            },
            entries = SplitUnit.entries
        )
        DisplayRow(
            name = stringResource(R.string.settings_perm_receipt_create),
            value = permissionReceiptCreate,
            isReadOnly = isReadOnly,
            onValueChange = {
                permissionReceiptCreate = it
            },
            entries = Role.entries.filter { it != Role.CREATOR }
        )
        DisplayRow(
            name = stringResource(R.string.settings_perm_receipt_edit),
            value = permissionReceiptEdit,
            isReadOnly = isReadOnly,
            onValueChange = {
                permissionReceiptEdit = it
            },
            entries = Role.entries
        )
        DisplayRow(
            name = stringResource(R.string.settings_new_member),
            value = onNewMemberRequest,
            isReadOnly = isReadOnly,
            onValueChange = {
                onNewMemberRequest = it
            },
            entries = RequestType.entries
        )


        if(onNewMemberRequest == RequestType.VOTE) {
            if (isReadOnly) {
                DisplayRow(
                    name = stringResource(R.string.settings_accept_rate),
                    value = acceptRate,
                    suffix = "%",
                    isReadOnly = true,
                )

            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    DisplayRow(
                        name = stringResource(R.string.settings_accept_rate),
                        value = acceptRate,
                        suffix = "%",
                        isReadOnly = false,
                        onValueChange = {
                            acceptRate = if(it > 100) 100 else it
                        },
                    )

                    Row(
                        modifier = Modifier.padding(10.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "0%",
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Slider(
                            modifier = Modifier.weight(1f),
                            value = acceptRate.toFloat(),
                            onValueChange = {
                                acceptRate = it.toInt()
                            },
                            valueRange = 0f..100f,
                        )

                        Text(
                            text = "100%",
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        if(!isReadOnly) {
            HorizontalDivider()
            DoneButton(
                modifier = Modifier,
                onConfirmed= {
                    onSettingsChange(
                        Settings(
                            name = roomName,
                            splitUnit = splitUnit,
                            permissionReceiptCreate = permissionReceiptCreate,
                            permissionReceiptEdit = permissionReceiptEdit,
                            onNewMemberRequest = onNewMemberRequest,
                            acceptRate = if(onNewMemberRequest == RequestType.VOTE) acceptRate else 0,
                        )
                    )
                },
                enabled = !isError && isActive,
                doneButtonText = saveButtonText,
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
    prefix: String = "",
    suffix: String = "",
    onValueChange: (T) -> Unit = {},
    entries: List<T> = listOf(),
) {
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
            modifier = if(isReadOnly) modifier else modifier.clickable { isDialogShown = true },
            text = "$prefix$value$suffix",
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
                onValueChange(it)
                isDialogShown = false
            },
            entries = entries
        )
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun ReadonlyPreview() {
    val readOnly = Settings(
        name = "readOnly",
        splitUnit = SplitUnit.TEN,
        permissionReceiptEdit = Role.OWNER,
        permissionReceiptCreate = Role.NORMAL,
        onNewMemberRequest = RequestType.ALWAYS,
        acceptRate = 50,
    )
    SplitAppTheme {
        Surface {
            SettingsEditor(
                settings = readOnly
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun WritablePreview() {
    val writable = Settings(
        name = "writable",
        splitUnit = SplitUnit.TEN,
        permissionReceiptEdit = Role.OWNER,
        permissionReceiptCreate = Role.NORMAL,
        onNewMemberRequest = RequestType.ALWAYS,
        acceptRate = 30,
    )
    SplitAppTheme {
        Surface {
            SettingsEditor(
                settings = writable,
                isReadOnly = false
            )
        }
    }
}