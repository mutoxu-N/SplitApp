package com.github.mutoxu_n.splitapp.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Receipt
import java.time.LocalDateTime
import java.util.PriorityQueue

private data class Transaction(val payer: Int, val receiver: Int, val amount: Int)
private data class RemainPayment(val index: Int, var amount: Int)


@Composable
fun PaymentList() {
    val me by Store.me.collectAsState()
    val settings by Store.settings.collectAsState()
    val receipts by Store.receipts.collectAsState()
    val members by Store.members.collectAsState()

    if(receipts == null || members == null || settings == null || me == null) {
        return
    }

    val (totals, remains) = calcTotalsAndRemains(receipts!!.mapNotNull { it.toReceipt() }, members!!)
    val transactions = calcTransactions(
        members!!,
        remains,
        settings!!.splitUnit.unit
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        itemsIndexed(members!!) { index, member ->
            PaymentListItem(
                me!!,
                member,
                members!!,
                totals[index],
                remains[index],
                transactions[index],
            )
        }
    }
}

@Composable
private fun PaymentListItem(
    me: Member?,
    member: Member,
    members: List<Member>,
    total: Int,
    remain: Int,
    transactions: List<Transaction> = listOf(),
) {

    var r = total
    for(t in transactions) r += t.amount

    Column(
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
            .padding(10.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            val used = stringResource(id = R.string.settings_currency) +  "%,d".format(total + remain)
            Text(
                text = member.name,
                textAlign = TextAlign.Start,
                color =
                    if(member.name == me?.name) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "(総額: $used)",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
            val totalString = stringResource(id = R.string.settings_currency) +  "%,d".format(total)
            val remainString = stringResource(id = R.string.settings_currency) +  "%,d".format(total + remain -r)
            Text(
                modifier = Modifier
                    .weight(1f),
                text = "支払済: ${totalString}, 残り: $remainString",
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }


        if (transactions.isNotEmpty())
            Row(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Column {
                    for(t in transactions) {
                        val name =
                            if(t.amount > 0) members[t.receiver].name
                            else members[t.payer].name
                        val msg =
                            if(t.amount > 0) " へ ${stringResource(id = R.string.settings_currency)}${"%,d".format(t.amount)} 支払う"
                            else " から ${stringResource(id = R.string.settings_currency)}${"%,d".format(-t.amount)} 受け取る"

                        Row(
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(
                                text = name,
                                textAlign = TextAlign.Start,
                                color =
                                    if(name == me?.name) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface,
                                style =
                                    if(name == me?.name) MaterialTheme.typography.bodyLarge
                                    else MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = msg,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
    }
}

private fun calcTotalsAndRemains(
    receipts: List<Receipt>,
    members: List<Member>,
): Pair<List<Int>, List<Int>> {
    // メンバー名をキーにしたマップを作る
    val memberMap = mutableMapOf<String, Int>()
    members.mapIndexed { index, member -> memberMap[member.name] = index }

    // 支払金額計算
    val totalDouble = MutableList(members.size) { 0.0 }
    val remainDouble = MutableList(members.size) { 0.0 }
    for(r in receipts) {
        // 合計重み
        var sum = .0
        for(m in r.buyers) sum += m.weight

        // 金額計算
        val payment = r.payment.toDouble()
        totalDouble[memberMap[r.paid.name]!!] += payment
        remainDouble[memberMap[r.paid.name]!!] -= payment
        for(m in r.buyers)
            remainDouble[memberMap[m.name]!!] += payment * m.weight / sum
    }
    return Pair(totalDouble.map { (it+0.5).toInt() }, remainDouble.map { (it+0.5).toInt() })
}

private fun calcTransactions(
    members: List<Member>,
    remains: List<Int>,
    splitUnit: Int,
): List<List<Transaction>>{
    // 支払金額の絶対値が最大のものを取り出すために, ヒープを構築
    val payers = PriorityQueue<RemainPayment>(compareByDescending { it.amount })
    val receivers = PriorityQueue<RemainPayment>(compareByDescending { it.amount })
    remains.forEachIndexed { index, amount ->
        when {
            amount > 0 -> payers.add(RemainPayment(index, amount))
            amount < 0 -> receivers.add(RemainPayment(index, -amount))
        }
    }

    // 支払い金額計算
    val transactions = List<MutableList<Transaction>>(members.size) { mutableListOf() }
    while(payers.isNotEmpty() && receivers.isNotEmpty()) {
        val payer = payers.poll()
        val receiver = receivers.poll()

        var amount = minOf(payer!!.amount, receiver!!.amount)
        if(amount < splitUnit) break

        // amount を splitUnit で四捨五入
        val r = amount % splitUnit
        amount -= r
        if(r != 0 && r >= splitUnit / 2) {
            amount += splitUnit
        }

        // 支払い情報確定
        transactions[payer.index].add(Transaction(payer.index, receiver.index, amount))
        transactions[receiver.index].add(Transaction(payer.index, receiver.index, -amount))
        payer.amount -= amount
        receiver.amount -= amount

        if(payer.amount > 0) payers.add(payer)
        if(receiver.amount > 0) receivers.add(receiver)
    }

    return transactions.map { mList -> mList.toList() }
}

@Preview(showBackground = true)
@Composable
private fun PaymentListPreview() {
    SplitAppTheme {
        Surface {
            val memA = Member.Empty.copy(name = "メンバーA", weight = 1f)
            val memB = Member.Empty.copy(name = "メンバーB", weight = 2f)
            val memC = Member.Empty.copy(name = "メンバーC", weight = 1f)
            val memD = Member.Empty.copy(name = "メンバーD", weight = 1f)
            val memE = Member.Empty.copy(name = "メンバーE", weight = 1f)
            val members = listOf( memA, memB, memC, memD, memE)

            val receipts = listOf(
                Receipt(
                    stuff = "パソコン",
                    paid = memA,
                    buyers = listOf(memA, memB, memC),
                    payment = 129999,
                    reportedBy = memA,
                    timestamp = LocalDateTime.now(),
                ),
                Receipt(
                    stuff = "牛肉",
                    paid = memD,
                    buyers = listOf(memD, memE),
                    payment = 13874,
                    reportedBy = memD,
                    timestamp = LocalDateTime.now(),
                ),
                Receipt(
                    stuff = "文房具",
                    paid = memB,
                    buyers = listOf(memB, memC),
                    payment = 602,
                    reportedBy = memB,
                    timestamp = LocalDateTime.now(),
                ),
            )

            val (totals, remains) = calcTotalsAndRemains(receipts, members)
            val transactions = calcTransactions(members, remains, 50)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                itemsIndexed(members) { index, member ->
                    PaymentListItem(
                        memA,
                        member,
                        members,
                        totals[index],
                        remains[index],
                        transactions[index],
                    )
                }
            }

        }
    }
}