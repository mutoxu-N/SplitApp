package com.github.mutoxu_n.splitapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.activities.InRoomActivity.InfoTabIndex
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.components.dialogs.AttentionDialog
import com.github.mutoxu_n.splitapp.components.members.MemberList
import com.github.mutoxu_n.splitapp.components.misc.BottomNavigation
import com.github.mutoxu_n.splitapp.components.misc.InRoomNavItem
import com.github.mutoxu_n.splitapp.components.misc.InRoomTopBar
import com.github.mutoxu_n.splitapp.components.misc.PaymentList
import com.github.mutoxu_n.splitapp.components.receipts.ReceiptList
import com.github.mutoxu_n.splitapp.components.settings.RoomIdDisplay
import com.github.mutoxu_n.splitapp.components.settings.SettingsEditor
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PendingMember
import com.github.mutoxu_n.splitapp.models.PendingState
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.ReceiptModel
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
        Store.stopPendingObserving()
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
                val receipts: List<ReceiptModel>? by Store.receipts.collectAsState()
                val members: List<Member>? by Store.members.collectAsState()
                val pending: List<PendingMember>? by Store.pendingMembers.collectAsState()

                if(
                    roomId == null ||
                    settings == null ||
                    receipts == null ||
                    members == null ||
                    pending == null
                ) {
                    Text(text = "roomId: $roomId, settings: $settings, receipts: $receipts, members: $members, pending: $pending")
                    return@SplitAppTheme
                }

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
                                    val me by Store.me.collectAsState()
                                    when (selectedTabIndex) {
                                        // Info/Pay
                                        InfoTabIndex.PAY.value -> {
                                            InfoPayScreen(
                                                me = me,
                                                members = members!!,
                                                receipts = receipts!!.mapNotNull { it.toReceipt() }.toList(),
                                                settings = settings!!,
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
                                                onDeleteGuest = {
                                                    lifecycleScope.launch {
                                                        onDeleteGuest(it)
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
                                settings = settings!!,
                                onSettingsChanged = {
                                    lifecycleScope.launch {
                                        onSettingsChanged(it)
                                    }
                                },
                                onDeleteRoomClicked = {
                                    lifecycleScope.launch {
                                        onDeleteRoom()
                                    }
                                }
                            )
                        }
                    }

                    // 投票画面
                    if(pending!!.isNotEmpty()) {
                        val me by Store.me.collectAsState()
                        var showDialog = false
                        me?.role?.let{ role ->
                            when(settings!!.onNewMemberRequest) {
                                RequestType.ACCEPT_BY_MODS -> {
                                    if(role.roleId >= Role.MODERATOR.roleId) showDialog = true
                                }

                                RequestType.ACCEPT_BY_OWNER -> {
                                    if(role == Role.OWNER) showDialog = true
                                }

                                RequestType.VOTE -> showDialog = true
                                RequestType.ALWAYS -> {}
                            }

                            if(showDialog) {
                                val target = pending!!.first()
                                AttentionDialog(
                                    title = "投票",
                                    message = "${target.name} がルームへの参加を希望しています。承認しますか？",
                                    dismissText = "拒否",
                                    onDismiss = {
                                        lifecycleScope.launch {
                                            API().accept(roomId, target.uid, false)
                                        }
                                    },
                                    confirmText = "承認",
                                    onConfirm = {
                                        lifecycleScope.launch {
                                            API().accept(roomId, target.uid, true)
                                        }
                                    }
                                )
                            }
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
            }
        }
    }

    override fun onDestroy() {
        Store.stopObserving()
        super.onDestroy()
    }

    private fun launchEditReceipt(receipt: Receipt) {
        EditReceiptActivity.launch(this@InRoomActivity, receipt)
    }

    private suspend fun onWeightChanged(member: Member) {
        roomId?.let{
            API().editMember(
                it,
                member.name,
                member.toModel(),
            )
        }
    }

    private suspend fun onDeleteGuest(member: Member) {
        roomId?.let {
            API().deleteGuest(it, member.name)
        }
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

    private suspend fun onDeleteRoom() {
        roomId?.let { API().deleteRoom(it) }
        Store.stopObserving()
        App.updateRoomId(null)
        finish()
    }

    private fun createReceipt() {
        roomId?.let { EditReceiptActivity.launch(this@InRoomActivity) }
    }

    enum class InfoTabIndex(val value: Int) {
        PAY(0), SETTINGS(1), MEMBERS(2)
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
        ) {
            ReceiptList(
                modifier = Modifier,
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
    me: Member?,
    members: List<Member>,
    receipts: List<Receipt>,
    settings: Settings,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp)
    ) {
        PaymentList(
            me = me,
            members = members,
            receipts = receipts,
            settings = settings,
        )
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
    onWeightChanged: (Member) -> Unit = {},
    onDeleteGuest: (Member) -> Unit = {},
) {
    val me: Member? by Store.me.collectAsState()
    me ?: return

    val isOwner = me!!.role == Role.OWNER
    MemberList(
        modifier = Modifier
            .padding(10.dp, 10.dp),
        onEditMember = {
            onWeightChanged(it)
        },
        onDeleteGuest = {
            onDeleteGuest(it)
        },
        enabled = isOwner,
        removable = isOwner,
    )
}

@Composable
private fun SettingsScreen(
    settings: Settings,
    onSettingsChanged: (Settings) -> Unit = {},
    onDeleteRoomClicked: () -> Unit,
) {
    val me: Member? by Store.me.collectAsState()
    val role = me?.role ?: return

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

            if(role == Role.OWNER) {
                Spacer(modifier = Modifier.size(20.dp))
                val context = LocalContext.current
                SettingsRow(title = "メンバー管理") {
                    MemberManageActivity.launch(context)
                }
            }
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
                onDeleteRoomClicked()
            },
            confirmText = "削除する",
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ActivityPreview() {
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
                                        me = member1,
                                        members = listOf(member1, member2),
                                        receipts = receipts,
                                        settings = settings,
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
                                        onDeleteGuest = {},
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
                        settings = settings,
                        onSettingsChanged = {},
                        onDeleteRoomClicked = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsRowPreview() {
    SplitAppTheme {
        SettingsRow(title = "テスト") {
        }
    }
}