package com.github.mutoxu_n.splitapp.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PendingUser
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.Settings

object Store {
    var pendingState: PendingUser? by mutableStateOf(null)
        private set
    var settings: Settings? by mutableStateOf(null)
        private set
    var members: List<Member>? by mutableStateOf(null)
        private set
    var pendingMembers: List<PendingUser>? by mutableStateOf(null)
        private set
    var receipts: List<Receipt>? by mutableStateOf(null)
        private set

    fun updatePendingUser(pendingUser: PendingUser) {
        this.pendingState = pendingUser
    }

    fun updateSettings(settings: Settings) {
        this.settings = settings
    }

    fun updateMembers(members: List<Member>) {
        this.members = members
    }

    fun updatePendingMembers(pendingMembers: List<PendingUser>) {
        this.pendingMembers = pendingMembers
    }

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts = receipts
    }
}