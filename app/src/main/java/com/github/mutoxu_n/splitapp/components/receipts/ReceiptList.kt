package com.github.mutoxu_n.splitapp.components.receipts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.Role
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptList(
    modifier: Modifier = Modifier,
    receipts: List<Receipt> = listOf()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(receipts) { receipt ->
            key(receipt.id) {
                ReceiptListItem(receipt = receipt)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReceiptListItem(
    receipt: Receipt
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium,
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(10.dp, 5.dp)
    ) {
        val df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
        Column {
            Text(
                text = df.format(receipt.timestamp.atZone(ZoneId.systemDefault())),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "${receipt.paid.name}が${receipt.stuff}を${stringResource(id = R.string.settings_currency)}${"%,d".format(receipt.payment)}で購入しました",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            HorizontalDivider(modifier = Modifier.padding(0.dp, 3.dp, 0.dp, 0.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for(buyer in receipt.buyers) {
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = {
                            Text(
                                text = buyer.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        })
                }
                if(receipt.buyers.isEmpty()) {
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = {
                            Text(
                                text = "全員",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        })
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "報告者: ${receipt.reportedBy.name}",
                textAlign = TextAlign.End,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReceiptListPreview() {
    val member1 = Member(
        name = "Owner member",
        uid = "null",
        weight = 1.0,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "Normal member",
        uid = "null",
        weight = 1.0,
        role = Role.NORMAL,
    )
    val date = LocalDateTime.of(2024, 1, 1, 0, 0, 0)

    MaterialTheme {
        Surface {
            ReceiptList(
                receipts = listOf(
                    Receipt(
                        stuff = "Kei-SuperComputer",
                        paid = member1,
                        buyers = listOf(member1, member2),
                        payment = 120_000,
                        reportedBy = member1,
                        timestamp = date,
                    ),
                    Receipt(
                        stuff = "Magic Wand",
                        paid = member2,
                        buyers = listOf(member2),
                        payment = 5_000,
                        reportedBy = member2,
                        timestamp = date,
                    ),
                    Receipt(
                        stuff = "Lava Bucket",
                        paid = member1,
                        buyers = listOf(member1),
                        payment = 500_000,
                        reportedBy = member2,
                        timestamp = date,
                    ),
                    Receipt(
                        stuff = "Lightning Canon",
                        paid = member1,
                        buyers = listOf(),
                        payment = 3_000,
                        reportedBy = member2,
                        timestamp = date,
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptListItemPreview() {
    val member1 = Member(
        name = "Owner member",
        uid = "null",
        weight = 1.0,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "Normal member",
        uid = "null",
        weight = 1.0,
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
    MaterialTheme {
        Surface {
            ReceiptListItem(
                receipt = receipt
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptListItemAllBuyerPreview() {
    val member1 = Member(
        name = "Owner member",
        uid = "null",
        weight = 1.0,
        role = Role.OWNER,
    )
    val receipt = Receipt(
        stuff = "Kei-SuperComputer",
        paid = member1,
        buyers = listOf(),
        payment = 120_000,
        reportedBy = member1,
        timestamp = LocalDateTime.of(2024, 1, 1, 0, 0, 0),
    )
    MaterialTheme {
        Surface {
            ReceiptListItem(
                receipt = receipt
            )
        }
    }
}