package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class PendingMember(
    val name: String,
    val uid: String,
    val isAccepted: Boolean,
    val approval: Int,
    val required: Int,
    val size: Int,
    val voted: List<String>,
)
