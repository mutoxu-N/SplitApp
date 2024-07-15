@file:Suppress("UNCHECKED_CAST")

package com.github.mutoxu_n.splitapp.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.SplitUnit
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ValueChangeDialog(
    title: String,
    value: T,
    onDismiss: () -> Unit,
    onConfirm: (T) -> Unit,
    entries: List<T> = listOf(),
) {
    var newValue by rememberSaveable { mutableStateOf(value) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
            ) {
                Text(text = stringResource(R.string.button_cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newValue) },
            ) {
                Text(text = stringResource(R.string.button_confirm))
            }
        },

        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        text = {
            // 値変更画面
            when(newValue) {
                is String -> {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(7.dp, 0.dp)
                            .fillMaxWidth(),
                        value = newValue as String,
                        onValueChange = {
                            isError = it.isBlank()
                            newValue = it as T
                        },
                        isError = isError,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    )
                }

                is Int -> {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(7.dp, 0.dp)
                            .fillMaxWidth(),
                        value = if((newValue as Int) < 0) "" else newValue.toString(),
                        onValueChange = {
                            try {
                                if(it.isBlank()) {
                                    newValue = -1 as T
                                }
                                val n = it.toInt()
                                if(n >= 0) {
                                    newValue = n as T
                                    isError = false
                                }

                            } catch (_: NumberFormatException) {}
                        },
                        isError = isError,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    )
                }

                is Role, is RequestType, is SplitUnit -> {
                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = {
                            isExpanded = !isExpanded
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = newValue.toString(),
                            onValueChange = {},
                            isError = isError,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)}
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            entries.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(text = role.toString()) },
                                    onClick = {
                                        newValue = role
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    Text(text = "ERROR")
                }
            }
        },
    )
}


@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun StringPreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "文字列の設定",
                    value = "入力データ",
                    onDismiss = {},
                    onConfirm = {},
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun IntPreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "整数の設定",
                    value = 10,
                    onDismiss = {},
                    onConfirm = {},
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun RolePreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "Roleの設定",
                    value = Role.OWNER,
                    onDismiss = {},
                    onConfirm = {},
                    entries = Role.entries
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun SplitUnitPreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "SplitUnitの設定",
                    value = SplitUnit.TEN,
                    onDismiss = {},
                    onConfirm = {},
                    entries = SplitUnit.entries
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun RequestTypePreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "RequestTypeの設定",
                    value = RequestType.ALWAYS,
                    onDismiss = {},
                    onConfirm = {},
                    entries = RequestType.entries
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ja")
@Composable
private fun ErrorPreview() {
    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ValueChangeDialog(
                    title = "非対応データの設定",
                    value = 1f,
                    onDismiss = {},
                    onConfirm = {},
                )
            }
        }
    }
}