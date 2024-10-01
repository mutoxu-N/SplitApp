package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PendingState(
    val id: String,
    val isApproved: Boolean? = null,
)
