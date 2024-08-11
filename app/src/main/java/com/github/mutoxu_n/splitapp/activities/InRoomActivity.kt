package com.github.mutoxu_n.splitapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.InRoomActivity.InfoTabIndex
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.misc.BottomNavigation
import com.github.mutoxu_n.splitapp.components.misc.InRoomNavItem
import com.github.mutoxu_n.splitapp.components.misc.InRoomTopBar
import com.github.mutoxu_n.splitapp.components.receipts.ReceiptList
import com.github.mutoxu_n.splitapp.components.settings.RoomIdDisplay
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PaymentDetail
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SplitUnit
import java.time.LocalDateTime

class InRoomActivity : ComponentActivity() {
    private var roomName by mutableStateOf("〇〇キャンプ")
    private var roomId = App.roomId ?: ""
    private var receipts by mutableStateOf(listOf<Receipt>())
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                val controller = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        InRoomTopBar(
                            title = roomName,
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            controller = controller
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = controller,
                        startDestination = InRoomNavItem.Receipt.route,
                    ) {
                        // Receipt
                        composable(InRoomNavItem.Receipt.route) {
                            ReceiptScreen(
                                receipts = receipts,
                                onReceiptEditClicked = { receipt ->
                                    launchEditReceipt(receipt)
                                }
                            )
                        }

                        // Info
                        composable(InRoomNavItem.Info.route) {
                            var selectedTabIndex by rememberSaveable { mutableIntStateOf(InfoTabIndex.SETTINGS.value) }
                            Scaffold(
                                topBar = {
                                    TabRow(selectedTabIndex = selectedTabIndex) {
                                        Tab(
                                            selected = selectedTabIndex == InfoTabIndex.PAY.value,
                                            onClick = { selectedTabIndex = InfoTabIndex.PAY.value },
                                            text = {
                                                Text(text = "支払い")
                                            }
                                        )
                                        Tab(
                                            selected = selectedTabIndex == InfoTabIndex.SETTINGS.value,
                                            onClick = { selectedTabIndex = InfoTabIndex.SETTINGS.value },
                                            text = {
                                                Text(text = "ルーム設定")
                                            }
                                        )
                                        Tab(
                                            selected = selectedTabIndex == InfoTabIndex.MEMBERS.value,
                                            onClick = { selectedTabIndex = InfoTabIndex.MEMBERS.value },
                                            text = {
                                                Text(text = "メンバー")
                                            }
                                        )
                                    }
                                }
                            ) { padding ->
                                Column(
                                    modifier = Modifier.padding(padding)
                                ) {
                                    when (selectedTabIndex) {
                                        // Info/Pay
                                        InfoTabIndex.PAY.value -> {
                                            InfoPayScreen(
                                                paymentDetails = listOf() // TODO: 支払い情報を算出し格納する
                                            )
                                        }
                                        // Info/Settings
                                        InfoTabIndex.SETTINGS.value -> {
                                            InfoSettingsScreen(
                                                roomId = roomId,
                                                settings = settings,
                                            )
                                        }
                                        // Info/Members
                                        InfoTabIndex.MEMBERS.value -> {
                                            InfoMembersScreen(
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Settings
                        composable(InRoomNavItem.Setting.route) {
                            SettingsScreen(
                            )
                        }
                    }
                }
            }
        }
    }

    private fun launchEditReceipt(receipt: Receipt) {
        // 編集画面に遷移
    }

    enum class InfoTabIndex(val value: Int) {
        PAY(0), SETTINGS(1), MEMBERS(2)
    }
}


@Composable
private fun ReceiptScreen(
    receipts: List<Receipt>,
    onReceiptEditClicked: (Receipt) -> Unit = {},
) {
    ReceiptList(
        modifier = Modifier
            .padding(top=10.dp),
        receipts = receipts,
        launchEditReceiptActivity = { receipt ->
            onReceiptEditClicked(receipt)
        }
    )
}

@Composable
private fun InfoPayScreen(
    paymentDetails: List<PaymentDetail>
) {
    LazyColumn(
        modifier = Modifier.padding(10.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(paymentDetails) { item ->
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
                Column {
                    Text(
                        text = "${item.from.name}は${item.to.name}へ " +
                                "${stringResource(R.string.settings_currency)}${item.amount} 支払う",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text =
                        "${item.from.name}の合計支払い金額: ${stringResource(R.string.settings_currency)}${item.total}",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

        }
    }
}


@Composable
private fun InfoSettingsScreen(
    roomId: String,
    settings: Settings,
) {
    Column {
        Spacer(modifier = Modifier.size(10.dp))
        RoomIdDisplay(roomId = roomId)
        HorizontalDivider()
        SettingsEditor(
            settings = settings,
            isReadOnly = true,
        )
    }

}


@Composable
private fun InfoMembersScreen(
) {
    Text("InfoMembersScreen")
}

@Composable
private fun SettingsScreen(
) {
    Text("SettingScreen")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActivityPreview() {
    val date = LocalDateTime.of(1000, 1, 1, 0, 0, 0)
    val member1 = Member(
        name = "Taro",
        uid = "null",
        weight = 1.0f,
        role = Role.OWNER,
    )
    val member2 = Member(
        name = "Jiro",
        uid = "null",
        weight = 1.0f,
        role = Role.NORMAL,
    )
    val receipts = listOf(
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
    val settings = Settings(
        name = "○○キャンプ",
        acceptRate = 50,
        permissionReceiptEdit = Role.OWNER,
        permissionReceiptCreate = Role.OWNER,
        onNewMemberRequest = RequestType.MODERATOR,
        splitUnit = SplitUnit.TEN,
    )
    val paymentDetails = listOf(
        PaymentDetail(
            from = member1,
            to = member2,
            amount = 120_000,
            total = 740000,
        ),
        PaymentDetail(
            from = member2,
            to = member1,
            amount = 5_000,
            total = 8000,
        ),
        PaymentDetail(
            from = member1,
            to = member2,
            amount = 500_000,
            total = 740000,
        ),
        PaymentDetail(
            from = member2,
            to = member1,
            amount = 3_000,
            total = 8000,
        ),
        PaymentDetail(
            from = member1,
            to = member2,
            amount = 120_000,
            total = 740000,
        ),
    )
    val roomName = "○○キャンプ"
    SplitAppTheme {
        val controller = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                InRoomTopBar(
                    title = roomName,
                )
            },
            bottomBar = {
                BottomNavigation(
                    controller = controller
                )
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = controller,
                startDestination = InRoomNavItem.Receipt.route,
            ) {
                // Receipt
                composable(InRoomNavItem.Receipt.route) {
                    ReceiptScreen(
                        receipts = receipts,
                    )
                }

                // Info
                composable(InRoomNavItem.Info.route) {
                    var selectedTabIndex by rememberSaveable { mutableIntStateOf(InfoTabIndex.SETTINGS.value) }
                    Scaffold(
                        topBar = {
                            TabRow(selectedTabIndex = selectedTabIndex) {
                                Tab(
                                    selected = selectedTabIndex == InfoTabIndex.PAY.value,
                                    onClick = { selectedTabIndex = InfoTabIndex.PAY.value },
                                    text = {
                                        Text(text = "支払い")
                                    }
                                )
                                Tab(
                                    selected = selectedTabIndex == InfoTabIndex.SETTINGS.value,
                                    onClick = { selectedTabIndex = InfoTabIndex.SETTINGS.value },
                                    text = {
                                        Text(text = "ルーム設定")
                                    }
                                )
                                Tab(
                                    selected = selectedTabIndex == InfoTabIndex.MEMBERS.value,
                                    onClick = { selectedTabIndex = InfoTabIndex.MEMBERS.value },
                                    text = {
                                        Text(text = "メンバー")
                                    }
                                )
                            }
                        }
                    ) { padding ->
                        Column(
                            modifier = Modifier.padding(padding)
                        ) {
                            when (selectedTabIndex) {
                                // Info/Pay
                                InfoTabIndex.PAY.value -> {
                                    InfoPayScreen(
                                        paymentDetails = paymentDetails,
                                    )
                                }
                                // Info/Settings
                                InfoTabIndex.SETTINGS.value -> {
                                    InfoSettingsScreen(
                                        roomId = "AB12C3",
                                        settings = settings,
                                    )
                                }
                                // Info/Members
                                InfoTabIndex.MEMBERS.value -> {
                                    InfoMembersScreen(
                                    )
                                }
                            }
                        }
                    }
                }

                // Settings
                composable(InRoomNavItem.Setting.route) {
                    SettingsScreen(
                    )
                }
            }
        }
    }
}