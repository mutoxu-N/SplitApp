package com.github.mutoxu_n.splitapp.components.members

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.R
import com.github.mutoxu_n.splitapp.components.dialogs.ValueChangeDialog
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.Role

@Composable
fun MemberManageList(
    modifier: Modifier = Modifier,
    members: List<Member>,
    isReadOnly: Boolean = true,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(members) { member ->
            MemberManageListItem(
                member = member,
                isReadOnly = isReadOnly,
            )
        }
    }
}

@Composable
private fun MemberManageListItem(
    modifier: Modifier = Modifier,
    member: Member,
    onMemberChanged: (Member)-> Unit = {},
    isReadOnly: Boolean = true,
) {
    var isDialogShown by rememberSaveable { mutableStateOf(false) }

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
            .padding(10.dp, 10.dp)
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
                    if(isReadOnly)
                        modifier
                    else
                        modifier
                        .clickable {
                            isDialogShown = true
                        },
                text = member.role.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration =
                    if(isReadOnly) TextDecoration.None
                    else TextDecoration.Underline,
            )
        }
    }

    if(isDialogShown) {
        ValueChangeDialog(
            title = stringResource(R.string.member_role),
            value = member.role,
            onDismiss = { isDialogShown = false },
            onConfirm = {
                isDialogShown = false
                onMemberChanged(member.copy(role = it))
            }
        )
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