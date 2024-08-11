@file:Suppress("UNCHECKED_CAST")

package com.github.mutoxu_n.splitapp.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Role

@Composable
fun<T> ListSelectDialog(
    title: String,
    selected: List<T>,
    candidates: List<T>,
    onDismiss: () -> Unit,
    onConfirm: (List<T>) -> Unit,
    multiselect: Boolean = false,
) {
    if(candidates.isEmpty()) {
        Text(text = "ERROR")
        return
    }

    val elem = candidates.first()
    var newValue by rememberSaveable { mutableStateOf(selected) }
    var everyone by rememberSaveable { mutableStateOf(newValue.isEmpty()) }
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
                onClick = {
                    if(multiselect && everyone)
                        onConfirm(listOf())
                    else
                        onConfirm(newValue)
                },
                enabled = !isError,
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
            when(elem) {
                is Member -> {
                    if(multiselect) {
                        Column {
                            CheckboxRow(
                                name = "全員",
                                onClicked = {
                                    everyone = it
                                    if(it) {
                                        newValue = listOf()

                                    } else {
                                        newValue = candidates.toList()
                                    }
                                },
                                selected = everyone,
                                enabled = true,
                            )

                            val selectedNames = newValue.map { (it as Member).name }
                            candidates.forEach { tmp ->
                                val member = tmp as Member
                                CheckboxRow(
                                    name = member.name,
                                    onClicked = {
                                        newValue =
                                            if (selectedNames.contains(member.name))
                                                newValue
                                                    .toMutableList()
                                                    .apply {
                                                        removeIf { x -> (x as Member).name == member.name }
                                                    }
                                            else
                                                newValue
                                                    .toMutableList()
                                                    .apply {
                                                        add(member as T)
                                                    }
                                        isError = newValue.isEmpty()
                                    },
                                    selected = everyone || selectedNames.contains(member.name),
                                    enabled = !everyone,
                                )
                            }
                        }

                    } else {

                        Column {
                            candidates.forEach {
                                val member = it as Member
                                RadioRow(
                                    name = member.name,
                                    onClicked = {
                                        newValue = listOf(member as T)
                                    },
                                    selected = member.name == (newValue[0] as Member).name
                                )
                            }
                        }

                    }
                }
            }
        },
    )
}

@Composable
private fun CheckboxRow(
    name: String,
    onClicked: (Boolean) -> Unit,
    selected: Boolean,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onClicked(!selected) }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onClicked(it) },
            enabled = enabled,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = if(enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun RadioRow(
    name: String,
    onClicked: () -> Unit,
    selected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onClicked() }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClicked() }
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListSelectDialogSinglePreview() {
    val member1 = Member(
        name = "member1",
        uid = "null",
        weight = 1.0f,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "member2",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )
    val member3 = Member(
        name = "member3",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )

    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ListSelectDialog(
                    title = "単一選択",
                    selected = listOf(member1),
                    candidates = listOf(member1, member2, member3),
                    onDismiss = {},
                    onConfirm = {},
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListSelectDialogMultiPreview() {
    val member1 = Member(
        name = "member1",
        uid = "null",
        weight = 1.0f,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "member2",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )
    val member3 = Member(
        name = "member3",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )

    SplitAppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ListSelectDialog(
                    title = "複数選択",
                    selected = listOf(member1, member3),
                    candidates = listOf(member1, member2, member3),
                    onDismiss = {},
                    onConfirm = {},
                    multiselect = true,
                )
            }
        }
    }
}