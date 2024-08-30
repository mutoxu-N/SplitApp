package com.github.mutoxu_n.splitapp.common

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PendingUser
import com.github.mutoxu_n.splitapp.models.Receipt
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneOffset

object Store {
    private var pendingStateListener: ListenerRegistration? = null
    var pendingState: MutableStateFlow<PendingUser?> = MutableStateFlow(null)
        private set
    private var settingsListener: ListenerRegistration? = null
    var settings: MutableStateFlow<Settings?> = MutableStateFlow(null)
        private set
    private var membersListener: ListenerRegistration? = null
    var members: MutableStateFlow<List<Member>?> = MutableStateFlow(null)
        private set
    private var pendingMembersListener: ListenerRegistration? = null
    var pendingMembers: MutableStateFlow<List<PendingUser>?> = MutableStateFlow(null)
        private set
    private var receiptsListener: ListenerRegistration? = null
    var receipts: MutableStateFlow<List<Receipt>?> = MutableStateFlow(null)
        private set

    fun updatePendingUser(pendingUser: PendingUser) {
        this.pendingState.update { pendingUser }
    }

    fun updateSettings(settings: Settings) {
        this.settings.update { settings }
        Log.e("Store", "updateSettings: $settings")
    }

    fun updateMembers(members: List<Member>) {
        this.members.update { members }
    }

    fun updatePendingMembers(pendingMembers: List<PendingUser>) {
        this.pendingMembers.update { pendingMembers }
    }

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts.update { receipts }
    }

    fun startObserving() {
        val db = FirebaseFirestore.getInstance()
        val roomId: String = App.roomId.value ?: return

        pendingStateListener = db.collection("pending_users").addSnapshotListener { snapshot, e ->
        }

        settingsListener = db.collection("rooms").document(roomId).addSnapshotListener { snapshot, e ->
        }

        members.update { listOf() }
        membersListener = db.collection("rooms").document(roomId).collection("members").addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if(snapshot == null) {
                members.update { listOf() }
                return@addSnapshotListener

            }

            val m = mutableListOf<Member>()
            for(data in snapshot.documents) {
                val member = Member(
                    name = data["name"] as String,
                    uid = data["uid"] as String,
                    weight = (data["weight"] as Long).toFloat(),
                    role = Role.fromValue((data["role"] as Long).toInt()),
                )
                m.add(member)
            }
            members.update { m }

        }
        pendingMembersListener = db.collection("rooms").document(roomId).collection("pending").addSnapshotListener { snapshot, e ->
        }

        receipts.update { listOf() }
        receiptsListener = db.collection("rooms").document(roomId).collection("receipts").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                receipts.update { listOf() }
            } else {
                val r = mutableListOf<Receipt>()
                for(data in snapshot.documents) {
                    r.add(Receipt(
                        stuff = data["stuff"] as String,
                        paid = data["paid"] as Member,
                        buyers = (data["buyers"] as List<Map<*, *>>).map {
                            Member(
                                name = it["name"] as String,
                                uid = it["uid"] as String,
                                weight = (it["weight"] as Long).toFloat(),
                                role = Role.fromValue((it["role"] as Long).toInt()),
                            )
                        },
                        payment = (data["payment"] as Long).toInt(),
                        reportedBy = members.value!!.find { it.uid == data["reported_by"] }!!,
                        timestamp = LocalDateTime.ofEpochSecond((data["timestamp"] as com.google.firebase.Timestamp).toDate().time, 0, ZoneOffset.UTC),
                    ))
                }
                receipts.update { r }
            }
        }
    }

    fun stopObserving() {
        pendingStateListener?.remove()
        pendingMembers.update { null }
        settingsListener?.remove()
        settings.update { null }
        membersListener?.remove()
        members.update { null }
        pendingMembersListener?.remove()
        pendingMembers.update { null }
        receiptsListener?.remove()
        receipts.update { null }
    }
}