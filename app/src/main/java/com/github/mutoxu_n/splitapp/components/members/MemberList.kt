package com.github.mutoxu_n.splitapp.components.members

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.components.dialogs.AttentionDialog
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Role
import java.util.Locale

@Composable
fun MemberList(
    modifier: Modifier = Modifier,
    members: List<Member>,
    onEditMember: (Member) -> Unit,
    onDeleteGuest: (Member) -> Unit = {},
    enabled: Boolean = true,
    removable: Boolean = false,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            members.sortedBy { it.name } .sortedByDescending { Role.entries.indexOf(it.role) },
            key = { it.name },
        ) { m ->
            MemberListItem(
                member = m,
                onWeightChanged = {
                    onEditMember(it)
                },
                onDeleteGuest = {
                    onDeleteGuest(it)
                },
                enabled = enabled,
                removable = removable && m.uid == null,
            )
        }
    }
}

@Composable
private fun MemberListItem(
    modifier: Modifier = Modifier,
    member: Member,
    onWeightChanged: (Member) -> Unit,
    onDeleteGuest: (Member) -> Unit,
    enabled: Boolean = true,
    removable: Boolean,
) {
    var isDialogShown by remember { mutableStateOf(false) }
    var moving by remember { mutableFloatStateOf(member.weight) }

    Column(
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
            .padding(10.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = modifier
                    .weight(1f),
                text = member.name,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            if(removable) {
                OutlinedIconButton(onClick = {
                    isDialogShown = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = modifier,
                text = stringResource(R.string.member_weight),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                modifier = modifier
                    .weight(1f),
                value = moving,
                onValueChange = { moving = it },
                onValueChangeFinished = { onWeightChanged(member.copy(weight = moving)) },
                valueRange = .05f..2f,
                steps = 38,
                enabled = enabled,
            )
            Text(
                modifier = modifier,
                text = String.format(Locale.getDefault(), "%.2f", moving),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if(isDialogShown) {
            AttentionDialog(
                title = "ゲストの削除",
                message = "ゲスト ${member.name} を削除します。",
                onDismiss = { isDialogShown = false },
                confirmText = "削除する",
                onConfirm = {
                    isDialogShown = false
                    onDeleteGuest(member)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MemberListEnabledPreview() {
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
            val members = remember { mutableStateListOf(member1, member2, member3) }
            MemberList(
                members = members,
                onEditMember = {
                    members.removeIf { m -> m.name == it.name }
                    members.add(it)
                },
                onDeleteGuest = {
                    members.remove(it)
                },
                removable = true,
            )
        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MemberListValueChangePreview() {
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
            val members = remember { mutableStateListOf(member1, member2, member3) }
            MemberList(
                members = members,
                onEditMember = {
                    members.removeIf { m -> m.name == it.name }
                    members.add(it)
                },
                onDeleteGuest = {
                    members.remove(it)
                },
                enabled = true,
                removable = false,
            )
        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MemberListReadOnlyPreview() {
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
            val members = remember { mutableStateListOf(member1, member2, member3) }
            MemberList(
                members = members,
                onEditMember = {
                    members.removeIf { m -> m.name == it.name }
                    members.add(it)
                },
                onDeleteGuest = {
                    members.remove(it)
                },
                enabled = false,
                removable = false,
            )
        }
    }

}