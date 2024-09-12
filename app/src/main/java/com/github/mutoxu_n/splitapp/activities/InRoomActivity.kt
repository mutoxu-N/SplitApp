package com.github.mutoxu_n.splitapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.InRoomActivity.InfoTabIndex
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.components.dialogs.AttentionDialog
import com.github.mutoxu_n.splitapp.components.members.MemberList
import com.github.mutoxu_n.splitapp.components.misc.BottomNavigation
import com.github.mutoxu_n.splitapp.components.misc.InRoomNavItem
import com.github.mutoxu_n.splitapp.components.misc.InRoomTopBar
import com.github.mutoxu_n.splitapp.components.receipts.ReceiptList
import com.github.mutoxu_n.splitapp.components.settings.RoomIdDisplay
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PaymentDetail
import com.github.mutoxu_n.splitapp.models.PendingMember
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SplitUnit
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class InRoomActivity : ComponentActivity() {
    private val roomId: String? = App.roomId.value
    private var leaveRoomConfirmDialogShown by mutableStateOf(false)

    companion object {
        private const val TAG = "InRoomActivity"
        fun launch(
            context: Context,
            launcher: ActivityResultLauncher<Intent>? = null
        ) {
            // Intent作成
            val intent = Intent(context, InRoomActivity::class.java)
            val args = Bundle()
            intent.putExtras(args)

            // launch
            if (launcher == null) context.startActivity(intent)
            else launcher.launch(intent)

        }
    }

    init {
        Store.startObserving()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback {
            leaveRoomConfirmDialogShown = true
        }

        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                val controller = rememberNavController()
                val settings: Settings? by Store.settings.collectAsState()
                if(settings == null) return@SplitAppTheme

                val receipts by Store.receipts.collectAsState()
                val members: List<Member>? by Store.members.collectAsState()
                val pending: List<PendingMember>? by Store.pendingMembers.collectAsState()
                val me: Member? by App.me.collectAsState()
                Log.e(TAG, "me: $me")

                if(roomId != null
                    && receipts != null
                    && members != null
                    && pending != null
                    && me != null) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            InRoomTopBar(
                                title = settings!!.name,
                                onBackClicked = { leaveRoomConfirmDialogShown = true},

                            )
                        },
                        bottomBar = {
                            BottomNavigation(
                                controller = controller
                            )
                        },
                    ) { innerPadding ->
                        NavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = controller,
                            startDestination = InRoomNavItem.Receipt.route,
                        ) {
                            // Receipt
                            composable(InRoomNavItem.Receipt.route) {
                                ReceiptScreen(
                                    receipts = receipts!!.mapNotNull { it.toReceipt() }.toList(),
                                    onReceiptEditClicked = { receipt ->
                                        launchEditReceipt(receipt)
                                    },
                                    onReceiptCreate = {
                                        lifecycleScope.launch {
                                            createReceipt()
                                        }
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
                                                    settings = settings!!,
                                                )
                                            }
                                            // Info/Members
                                            InfoTabIndex.MEMBERS.value -> {
                                                InfoMembersScreen(
                                                    role = me!!.role,
                                                    members = members!!,
                                                    onRemoveMember = {
                                                        lifecycleScope.launch {
                                                            onRemoveMember(it)
                                                        }
                                                    },
                                                    onWeightChanged = {
                                                        lifecycleScope.launch {
                                                            onWeightChanged(it)
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Settings
                            composable(InRoomNavItem.Setting.route) {
                                SettingsScreen(
                                    role = me!!.role,
                                    settings = settings!!,
                                    onSettingsChanged = {
                                        lifecycleScope.launch {
                                            onSettingsChanged(it)
                                        }
                                    },
                                    onRemoveRoomClicked = {
                                        lifecycleScope.launch {
                                            onRemoveRoom()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if(leaveRoomConfirmDialogShown) {
                        AttentionDialog(
                            title = "ルームを退出します",
                            message = "ルーム(${settings!!.name})を退出します。ルームが削除されていなければ、招待コードを入力することで再入室できます。",
                            dismissText = "キャンセル",
                            onDismiss = { leaveRoomConfirmDialogShown = false },
                            confirmText = "退出",
                            onConfirm = {
                                leaveRoomConfirmDialogShown = false
                                Store.stopObserving()
                                App.updateRoomId(null)
                                finish()
                            })
                    }

                } else {
                    Column(Modifier.fillMaxSize()) {
                        Text(text = "Loading")
                        Text(text = "roomId: $roomId")
                        Text(text = "receipts: $receipts")
                        Text(text = "members: $members")
                        Text(text = "pending: $pending")
                        Text(text = "me: $me")
                    }

                }

            }
        }
    }

    override fun onDestroy() {
        Store.stopObserving()
        super.onDestroy()
    }

    private fun launchEditReceipt(receipt: Receipt) {
        // TODO: 編集画面に遷移
    }

    private suspend fun onWeightChanged(member: Member) {
        // TODO: メンバーの重みを更新
    }

    private suspend fun onRemoveMember(member: Member) {
        // TODO: メンバーを削除
    }

    private suspend fun onSettingsChanged(settings: Settings) {
        roomId?.let{
            API().editSettings(
                it,
                settings.toModel(),
                callBack = { b ->
                    Toast.makeText(
                        this@InRoomActivity,
                        if(b) "ルーム設定の更新に成功しました" else "ルーム設定の更新に失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                },
            )
        }
    }

    private suspend fun onRemoveRoom() {
        roomId?.let { API().deleteRoom(it) }
        Store.stopObserving()
        App.updateRoomId(null)
        finish()
    }

    private fun createReceipt() {
        roomId?.let { EditReceiptActivity.launch(this@InRoomActivity, it) }
    }

    enum class InfoTabIndex(val value: Int) {
        PAY(0), SETTINGS(1), MEMBERS(2)
    }
}


@Composable
private fun ReceiptScreen(
    receipts: List<Receipt>,
    onReceiptEditClicked: (Receipt) -> Unit = {},
    onReceiptCreate: () -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onReceiptCreate() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ReceiptList(
                modifier = Modifier
                    .padding(top = 10.dp),
                receipts = receipts,
                launchEditReceiptActivity = { receipt ->
                    onReceiptEditClicked(receipt)
                },
                bottomSpacerSize = 75.dp,
            )
        }
    }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp),
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        RoomIdDisplay(roomId = roomId)
        HorizontalDivider()

        key(settings) {
            SettingsEditor(
                settings = settings,
                isReadOnly = true,
            )
        }
    }

}


@Composable
private fun InfoMembersScreen(
    role: Role,
    members: List<Member>,
    onWeightChanged: (Member) -> Unit = {},
    onRemoveMember: (Member) -> Unit = {},
) {
    MemberList(
        modifier = Modifier
            .padding(10.dp, 10.dp),
        members = members,
        onEditMember = {
            onWeightChanged(it)
        },
        onRemoveMember = {
            onRemoveMember(it)
        },
        enabled = role == Role.OWNER,
        removable = role == Role.OWNER,
    )
}

@Composable
private fun SettingsScreen(
    role: Role,
    settings: Settings,
    onSettingsChanged: (Settings) -> Unit = {},
    onRemoveRoomClicked: () -> Unit,
) {
    var confirmDialogShown by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            SettingsEditor(
                settings = settings,
                onSettingsChange = {
                    onSettingsChanged(it)
                },
                isReadOnly = role != Role.OWNER,
            )
        }

        if(role == Role.OWNER) {
            HorizontalDivider()
            Button(
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.error),
                onClick = { confirmDialogShown = true },
            ) {
                Text(
                    text = "ルームを削除する"
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }

    if(confirmDialogShown) {
        AttentionDialog(
            title = "ルームを削除します",
            message = "ルーム(${settings.name})のすべてのデータを削除し、ルームを削除します。一度削除したルームは元に戻せません！",
            onDismiss = { confirmDialogShown = false },
            dismissText = "キャンセル",
            onConfirm = {
                confirmDialogShown = false
                onRemoveRoomClicked()
            },
            confirmText = "削除する",
        )
    }
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
        ),
    )
    val settings = Settings(
        name = "○○キャンプ",
        acceptRate = 50,
        permissionReceiptEdit = Role.OWNER,
        permissionReceiptCreate = Role.OWNER,
        onNewMemberRequest = RequestType.ACCEPT_BY_MODS,
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
                                        role = Role.OWNER,
                                        members = listOf(member1, member2),
                                        onRemoveMember = {},
                                        onWeightChanged = {},
                                    )
                                }
                            }
                        }
                    }
                }

                // Settings
                composable(InRoomNavItem.Setting.route) {
                    SettingsScreen(
                        role = Role.OWNER,
                        settings = settings,
                        onSettingsChanged = {},
                        onRemoveRoomClicked = {}
                    )
                }
            }
        }
    }
}