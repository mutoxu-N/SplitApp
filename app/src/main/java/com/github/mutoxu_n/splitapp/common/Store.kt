package com.github.mutoxu_n.splitapp.common

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PendingUser
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object Store {
    var pendingState: MutableStateFlow<PendingUser?> = MutableStateFlow(null)
        private set
    var settings: MutableStateFlow<Settings?> = MutableStateFlow(null)
        private set
    var members: MutableStateFlow<List<Member>?> = MutableStateFlow(null)
        private set
    var pendingMembers: MutableStateFlow<List<PendingUser>?> = MutableStateFlow(null)
        private set
    var receipts: MutableStateFlow<List<Receipt>?> = MutableStateFlow(null)
        private set

    suspend fun updatePendingUser(pendingUser: PendingUser) {
        this.pendingState.update { pendingUser }
    }

    suspend fun updateSettings(settings: Settings) {
        this.settings.update { settings }
        Log.e("Store", "updateSettings: $settings")
    }

    suspend fun updateMembers(members: List<Member>) {
        this.members.update { members }
    }

    suspend fun updatePendingMembers(pendingMembers: List<PendingUser>) {
        this.pendingMembers.update { pendingMembers }
    }

    suspend fun updateReceipts(receipts: List<Receipt>) {
        this.receipts.update { receipts }
    }
}