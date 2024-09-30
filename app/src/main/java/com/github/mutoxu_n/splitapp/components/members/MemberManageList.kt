package com.github.mutoxu_n.splitapp.components.members

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.components.dialogs.AttentionDialog
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Role

@Composable
fun MemberManageList(
    modifier: Modifier = Modifier,
    members: List<Member>,
    isReadOnly: Boolean = true,
    onMemberChanged: (String, Member)-> Unit = {_, _ ->},
    onDeleteGuest: (Member) -> Unit = {},
    bottomSpacerSize: Dp = 0.dp
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(members) { member ->
            MemberManageListItem(
                member = member,
                isReadOnly = isReadOnly,
                onMemberChanged = { old, new ->
                    onMemberChanged(old, new)
                },
                onDeleteGuest = {
                    onDeleteGuest(it)
                },
            )
        }

        item {
            Spacer(modifier = Modifier.size(bottomSpacerSize))
        }
    }
}

@Composable
private fun MemberManageListItem(
    modifier: Modifier = Modifier,
    member: Member,
    onMemberChanged: (String, Member)-> Unit,
    onDeleteGuest: (Member) -> Unit,
    isReadOnly: Boolean = true,
) {
    var isRoleDialogShown by rememberSaveable { mutableStateOf(false) }
    var isNameDialogShown by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogShown by rememberSaveable { mutableStateOf(false) }
    var isOwner by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier
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
            .padding(10.dp, 10.dp)
    ) {
        Column(
            modifier = modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row {
                Text(
                    text = stringResource(R.string.member_role) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier =
                    if(isReadOnly || member.uid == null)
                        modifier
                    else
                        modifier
                            .clickable {
                                isRoleDialogShown = true
                            },
                    text = member.role.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration =
                    if(isReadOnly || member.uid == null) TextDecoration.None
                    else TextDecoration.Underline,
                )
            }
        }

        if(!isReadOnly) {
            Row {
                OutlinedIconButton(onClick = { isNameDialogShown = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                if(member.uid == null) {
                    OutlinedIconButton(onClick = { isDeleteDialogShown = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
    if(isRoleDialogShown) {
        ValueChangeDialog(
            title = stringResource(R.string.member_role),
            value = member.role,
            onDismiss = { isRoleDialogShown = false },
            onConfirm = { role ->
                isRoleDialogShown = false
                if(role == Role.OWNER || member.role == Role.OWNER) {
                    isOwner = true

                } else {
                    onMemberChanged(member.name, member.copy(role = role))
                }
            },
            entries = Role.generalEntries,
        )
    }

    if(isNameDialogShown) {
        ValueChangeDialog(
            title = "メンバー名の変更",
            value = member.name,
            onDismiss = { isNameDialogShown = false },
            onConfirm = {
                isNameDialogShown = false
                onMemberChanged(member.name, member.copy(name = it))
            }
        )
    }

    if(isDeleteDialogShown) {
        AttentionDialog(
            title = "ゲストの削除",
            message = "ゲスト ${member.name} を削除します。",
            onDismiss = { isDeleteDialogShown = false },
            confirmText = "削除する",
            onConfirm = {
                isDeleteDialogShown = false
                onDeleteGuest(member)
            }
        )
    }

    if(isOwner) {
        if(member.role == Role.OWNER) {
            AttentionDialog(
                title = "オーナーを変更できません",
                message = "自分自身のオーナー権限をはく奪することはできません。他のメンバーの役職をオーナーに設定することで、オーナー権限を譲渡できます。",
                dismissText = null,
                onDismiss = { isOwner=false },
                confirmText = "閉じる",
                onConfirm = { isOwner = false }
            )

        } else {
            AttentionDialog(
                title = "オーナーを変更します",
                message = "オーナー権限を ${member.name} に譲渡します。この操作が完了すると、再度オーナー権限を得るまで一部の操作にアクセスできなくなります。",
                onDismiss = { isOwner=false },
                onConfirm = {
                    onMemberChanged(member.name, member.copy(role = Role.OWNER))
                    isOwner = false
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MemberManageListReadOnlyPreview() {
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
    MaterialTheme {
        Surface {
            MemberManageList(members = listOf(member1, member2, member3))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MemberManageListWritablePreview() {
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
    MaterialTheme {
        Surface {
            MemberManageList(
                members = listOf(member1, member2, member3),
                isReadOnly = false,
            )
        }
    }
}