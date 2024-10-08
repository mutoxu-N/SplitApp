@file:Suppress("UNCHECKED_CAST")

package com.github.mutoxu_n.splitapp.components.receipts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.dialogs.ListSelectDialog
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.components.misc.DoneButton
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.Role
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReceiptDetailDisplay(
    modifier: Modifier = Modifier,
    receipt: Receipt,
    onValueChanged: (Receipt) -> Unit,
    members: List<Member>,
) {
    var isStuffInputDialogShown by rememberSaveable { mutableStateOf(false) }
    var stuff by remember { mutableStateOf(receipt.stuff) }
    var isStuffError by rememberSaveable { mutableStateOf(receipt.stuff.isBlank()) }
    var paid by rememberSaveable { mutableStateOf(receipt.paid) }
    var payment by rememberSaveable { mutableIntStateOf(receipt.payment) }
    var isPaymentError by rememberSaveable { mutableStateOf(receipt.payment <= 0) }
    var buyers by rememberSaveable { mutableStateOf(receipt.buyers) }

    val df = DateTimeFormatter.ofPattern(stringResource(R.string.format_datetime))

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = {}) {}
            Text(
                modifier = modifier,
                text = stuff,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            IconButton(onClick = {
                isStuffInputDialogShown = true
            }) { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
        }
        HorizontalDivider(
            modifier = modifier
                .padding(5.dp, 0.dp),
        )

        DisplayRow(
            name = stringResource(R.string.receipt_paid),
            isReadOnly = false,
            value = paid,
            onValueChanged = { paid = it },
            entries = members, // 全ユーザー
        )
        DisplayRow(
            name = stringResource(R.string.receipt_payment),
            isReadOnly = false,
            value = payment,
            prefix = stringResource(id = R.string.settings_currency),
            onValueChanged = {
                payment = it
                isPaymentError = payment <= 0
            }
        )

        Column {
            DisplayRow(
                name = stringResource(R.string.receipt_term_buyer),
                isReadOnly = false,
                value = buyers,
                suffix = "人",
                onValueChanged = { buyers = (it as List<Member>) },
                multiselect = true,
                entries = members, // 全ユーザー
            )
            if(buyers.isNotEmpty()) {
                FlowRow(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = buyers.joinToString { it.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }

        DisplayRow(
            name = stringResource(R.string.receipt_term_reported_by),
            value = receipt.reportedBy,
        )
        DisplayRow(
            name = stringResource(R.string.receipt_term_created_time),
            value = df.format(receipt.timestamp.atZone(java.time.ZoneId.systemDefault())),
        )

        if(BuildConfig.DEBUG) {
            DisplayRow(
                name = "ID",
                value = receipt.id,
            )
        }


        HorizontalDivider()
        DoneButton(
            enabled = !isStuffError && !isPaymentError,
            doneButtonText = stringResource(R.string.button_save_receipt),
            onConfirmed = {
                onValueChanged(receipt.copy(
                    stuff = stuff,
                    paid = paid,
                    payment = payment,
                    buyers = buyers
                ))
            }
        )

        if(isStuffInputDialogShown) {
            ValueChangeDialog(
                title = stringResource(R.string.receipt_stuff),
                value = stuff,
                onDismiss = { isStuffInputDialogShown = false },
                onConfirm = { newStuff ->
                    stuff = newStuff
                    isStuffError = newStuff.isBlank()
                    isStuffInputDialogShown = false
                }
            )
        }
    }
}

@Suppress("IMPLICIT_CAST_TO_ANY")
@Composable
private fun <T> DisplayRow(
    modifier: Modifier = Modifier,
    name: String,
    isReadOnly: Boolean = true,
    value: T,
    prefix: String = "",
    suffix: String = "",
    onValueChanged: (T) -> Unit = {},
    entries: List<T> = listOf(),
    multiselect: Boolean = false,
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
            text = "$prefix${when(value) {
                is Member -> value.name
                is List<*> -> {
                    // メンバーリストの場合
                    if(value.isEmpty()) stringResource(id = R.string.term_everyone)
                    else "${value.size}$suffix"
                }
                else -> value
            }}",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if(isReadOnly) TextDecoration.None else TextDecoration.Underline,
        )

    }

    if(isDialogShown) {
        if(value is List<*>) {
            ListSelectDialog(
                title = name,
                selected = value as List<T>,
                candidates = entries,
                onDismiss = { isDialogShown = false },
                onConfirm = {
                    onValueChanged(it as T)
                    isDialogShown = false
                },
                multiselect = multiselect,
            )

        } else {
            ValueChangeDialog(
                title = name,
                value = value,
                onDismiss = { isDialogShown = false },
                onConfirm = {
                    onValueChanged(it)
                    isDialogShown = false
                },
                entries = entries,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReceiptDetailDisplayEveryonePreview() {
    val member = Member(
        name = "sample member",
        uid = "null",
        weight = 1.0f,
        role = Role.OWNER,
    )
    val receipt = Receipt(
        stuff = "Kei-SuperComputer",
        paid = member,
        buyers = listOf(),
        payment = 120_000,
        reportedBy = member,
        timestamp = LocalDateTime.of(2024, 1, 1, 0, 0, 0),
    )
    SplitAppTheme {
        Surface {
            ReceiptDetailDisplay(
                receipt = receipt,
                onValueChanged = {},
                members = listOf(member),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReceiptDetailDisplayPreview() {
    val member1 = Member(
        name = "sample member",
        uid = "null",
        weight = 1.0f,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "2nd member",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )
    val receipt = Receipt(
        stuff = "Kei-SuperComputer",
        paid = member1,
        buyers = listOf(member1, member2),
        payment = 120_000,
        reportedBy = member1,
        timestamp = LocalDateTime.of(2024, 1, 1, 0, 0, 0),
    )
    SplitAppTheme {
        Surface {
            ReceiptDetailDisplay(
                receipt = receipt,
                onValueChanged = {},
                members = listOf(member1, member2)
            )
        }
    }
}