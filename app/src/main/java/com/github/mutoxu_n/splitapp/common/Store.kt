package com.github.mutoxu_n.splitapp.common

import android.util.Log
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.PendingMember
import com.github.mutoxu_n.splitapp.models.PendingState
import com.github.mutoxu_n.splitapp.models.ReceiptModel
import com.github.mutoxu_n.splitapp.models.RequestType
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SplitUnit
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneOffset

object Store {
    private var pendingListener: ListenerRegistration? = null
    var pendingState: MutableStateFlow<PendingState?> = MutableStateFlow(null)
        private set
    private var settingsListener: ListenerRegistration? = null
    var settings: MutableStateFlow<Settings?> = MutableStateFlow(null)
        private set
    private var membersListener: ListenerRegistration? = null
    var members: MutableStateFlow<List<Member>?> = MutableStateFlow(null)
        private set
    private var pendingMembersListener: ListenerRegistration? = null
    var pendingMembers: MutableStateFlow<List<PendingMember>?> = MutableStateFlow(null)
        private set
    private var receiptsListener: ListenerRegistration? = null
    var receipts: MutableStateFlow<List<ReceiptModel>?> = MutableStateFlow(null)
        private set
    private var displayNameListener: ListenerRegistration? = null
    private var displayName: MutableStateFlow<String?> = MutableStateFlow(null)
    private var meListener: ListenerRegistration? = null
    var me: MutableStateFlow<Member?> = MutableStateFlow(null)

    private var roomListener: ListenerRegistration? = null
    var room: MutableStateFlow<String?> = MutableStateFlow(null)

    init {
        // デバッグ時
        if(BuildConfig.DEBUG) {
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        }
    }

    fun startPendingObserving() {
        stopPendingObserving()
        val db = FirebaseFirestore.getInstance()

        pendingListener = db.collection("pending_users").document(Auth.auth.uid!!).addSnapshotListener { snapshot, e ->
            if(e != null) {
                pendingState.update { null }
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot == null || snapshot.data == null) {
                pendingState.update { null }
                return@addSnapshotListener
            }

            val data = snapshot.data!!
            pendingState.update {
                PendingState(
                    id = data["id"] as String,
                    isApproved = data["is_approved"] as Boolean?,
                )
            }
        }
    }

    fun stopPendingObserving() {
        pendingListener?.remove()
        pendingState.update { null }
    }

    fun startObserving() {
        stopObserving()

        val db = FirebaseFirestore.getInstance()
        val roomId: String = App.roomId.value ?: return

        settingsListener = db.collection("rooms").document(roomId).addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if(snapshot == null || snapshot.data == null) {
                settings.update { null }
                return@addSnapshotListener
            }

            val settingsData = snapshot.data!!["settings"] as Map<*, *>
            settings.update {
                Settings(
                    name = settingsData["name"] as String,
                    acceptRate = (settingsData["accept_rate"] as Long).toInt(),
                    permissionReceiptEdit = Role.fromString(settingsData["permission_receipt_edit"] as String),
                    permissionReceiptCreate = Role.fromString(settingsData["permission_receipt_create"] as String),
                    onNewMemberRequest = RequestType.fromString(settingsData["on_new_member_request"] as String),
                    splitUnit = SplitUnit.fromUnit((settingsData["split_unit"] as Long).toInt()),
                )
            }
        }

        members.update { listOf() }
        membersListener = db.collection("rooms").document(roomId).collection("members").orderBy("role", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
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
                    uid = (data["id"] as String?)?.let { if(it == "null") null else it },
                    weight = data["weight"].toString().toFloat(),
                    role = Role.fromValue((data["role"] as Long).toDouble()),
                )
                m.add(member)
            }
            members.update { m }

        }

        pendingMembers.update { listOf() }
        pendingMembersListener = db.collection("rooms").document(roomId).collection("pending").addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if(snapshot == null) {
                pendingMembers.update { listOf() }
                return@addSnapshotListener
            }

            val m = mutableListOf<PendingMember>()
            for(data in snapshot.documents) {
                val member = PendingMember(
                    name = data["name"] as String,
                    uid = data["id"] as String,
                    isAccepted = data["is_accepted"] as Boolean,
                    approval = (data["approval"] as Long).toInt(),
                    required = (data["required"] as Long).toInt(),
                    size = (data["size"] as Long).toInt(),
                    voted = data["voted"] as List<String>,
                )

                if(me.value?.uid !in member.voted) {
                    m.add(member)
                }
            }
            pendingMembers.update { m }

        }

        receipts.update { listOf() }
        receiptsListener = db.collection("rooms").document(roomId).collection("receipts").orderBy("timestamp").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                receipts.update { listOf() }

            } else {
                val r = mutableListOf<ReceiptModel>()
                for(data in snapshot.documents) {
                    val ts = (data["timestamp"] as Timestamp)
                    r.add(ReceiptModel(
                        id = data["id"] as String,
                        stuff = data["stuff"] as String,
                        paid = data["paid"] as String,
                        buyers = data["buyers"] as List<String>,
                        payment = (data["payment"] as Long).toInt(),
                        reportedBy = data["reported_by"] as String,
                        timestamp = LocalDateTime
                            .ofEpochSecond(ts.seconds, ts.nanoseconds, ZoneOffset.UTC).toString(),
                    ))
                }
                receipts.update { r }
            }
        }

        displayNameListener = db.collection("rooms").document(roomId).addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }
            if(snapshot?.data == null) {
                displayName.update { null }
                return@addSnapshotListener
            }

            meListener?.remove()

            val name = (snapshot["users"] as Map<*, *>)[Auth.auth.uid] as String
            displayName.update { name }
            meListener = db.collection("rooms").document(roomId).collection("members").document(name).addSnapshotListener { snapshot2, e2 ->
                if(e2 != null) {
                    Log.w("Store", "listen:error", e2)
                    return@addSnapshotListener
                }

                if(snapshot2 == null || snapshot.data == null) {
                    me.update { null }
                    return@addSnapshotListener
                }

                val data = (snapshot2.data as Map<*, *>?) ?: return@addSnapshotListener
                me.update {
                    Member(
                        name = data["name"] as String,
                        uid = (data["id"] as String?)?.let { if(it == "null") null else it },
                        weight = data["weight"].toString().toFloat(),
                        role = Role.fromValue((data["role"] as Long).toDouble()),
                    )
                }
            }
        }

        roomListener = db.collection("rooms").document(roomId).addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w("Store", "listen:error", e)
                return@addSnapshotListener
            }
            if(snapshot?.data == null) {
                room.update { null }
                return@addSnapshotListener
            }
            room.update { snapshot["name"] as String }
        }
    }

    fun stopObserving() {
        pendingListener?.remove()
        pendingMembers.update { null }
        settingsListener?.remove()
        settings.update { null }
        membersListener?.remove()
        members.update { null }
        pendingMembersListener?.remove()
        pendingMembers.update { null }
        receiptsListener?.remove()
        receipts.update { null }
        displayNameListener?.remove()
        displayName.update { null }
        meListener?.remove()
        me.update { null }
        roomListener?.remove()
        room.update { null }
    }
}