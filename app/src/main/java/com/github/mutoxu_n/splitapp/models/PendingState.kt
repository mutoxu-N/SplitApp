package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PendingState(
    val id: String,
    val isApproved: Boolean? = null,
) {
    companion object{
        const val FIELD_UID = "id"
        const val FIELD_IS_APPROVED = "isApproved"
    }

    @Throws(ClassCastException::class)
    fun fromMap(map: Map<String, Any>): PendingState{
        return PendingState(
            id = map[FIELD_UID] as String,
            isApproved = map[FIELD_IS_APPROVED] as Boolean?,
        )
    }
}