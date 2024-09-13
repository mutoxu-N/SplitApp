package com.github.mutoxu_n.splitapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.components.members.MemberManageList
import com.github.mutoxu_n.splitapp.components.misc.InRoomTopBar
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Role
import kotlinx.coroutines.launch

class MemberManageActivity : ComponentActivity() {
    companion object {
        fun launch(
            context: Context,
            launcher: ActivityResultLauncher<Intent>? = null
        ) {
            // Intent作成
            val intent = Intent(context, MemberManageActivity::class.java)
            val args = Bundle()
            intent.putExtras(args)

            // launch
            if (launcher == null) context.startActivity(intent)
            else launcher.launch(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val members by Store.members.collectAsState()
            var guestCreateDialogShown by rememberSaveable { mutableStateOf(false) }

            var isError = false
            if(members == null) { Text(text = "メンバーの読み込みに失敗しました"); isError=true }
            if(isError) return@setContent


            SplitAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        InRoomTopBar(
                            title = "メンバーの管理",
                            onBackClicked = { finish() }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { guestCreateDialogShown = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                ) { innerPadding ->
                    MemberManageList(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(start = 7.dp, end = 7.dp, top = 5.dp),
                        members = members!!,
                        isReadOnly = false,
                        onMemberChanged = {
                            lifecycleScope.launch {
                                updateMember(it)
                            }
                        }
                    )
                }

                if(guestCreateDialogShown) {
                    ValueChangeDialog(
                        title = "ゲストメンバーの作成",
                        value = "",
                        onDismiss = { guestCreateDialogShown = false },
                        onConfirm =  {
                            guestCreateDialogShown = false
                            lifecycleScope.launch {
                                createGuest(it)
                            }
                        }
                    )
                }
            }
        }
    }

    private suspend fun updateMember(member: Member) {
        val roomId = App.roomId.value ?: return
        API().editMember(roomId, member.name, member.toModel())
    }

    private suspend fun createGuest(name: String) {
        val roomId = App.roomId.value ?: return

        API().createGuest(
            roomId = roomId,
            name = name
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MemberManagePreview() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InRoomTopBar(
                title = "〇〇キャンプ",
            )
        },
    ) { innerPadding ->
        MemberManageList(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp, 10.dp),
            members = listOf(
                Member(
                    name = "Taro",
                    uid = "null",
                    weight = 1.0f,
                    role = Role.OWNER,
                ),
                Member(
                    name = "Jiro",
                    uid = "null",
                    weight = 1.0f,
                    role = Role.NORMAL,
                ),
                Member(
                    name = "Saburo",
                    uid = "null",
                    weight = 1.0f,
                    role = Role.NORMAL,
                )
            ),
            isReadOnly = false,
        )
    }
}