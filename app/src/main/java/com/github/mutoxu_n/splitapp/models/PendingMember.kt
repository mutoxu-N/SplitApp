package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlin.jvm.Throws


@IgnoreExtraProperties
data class PendingMember(
    val name: String,
    val uid: String?,
    val isAccepted: Boolean,
    val approval: Int,
    val required: Int,
    val size: Int,
    val voted: List<String>,
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_UID = "uid"
        const val FIELD_IS_ACCEPTED = "isAccepted"
        const val FIELD_APPROVAL = "approval"
        const val FIELD_REQUIRED = "required"
        const val FIELD_SIZE = "size"
        const val FIELD_VOTED = "voted"

        @Suppress("UNCHECKED_CAST")
        @Throws(ClassCastException::class)
        fun fromMap(map: Map<String, Any>): PendingMember{
            return PendingMember(
                name = map[FIELD_NAME] as String,
                uid = map[FIELD_UID] as String?,
                isAccepted = map[FIELD_IS_ACCEPTED] as Boolean,
                approval = map[FIELD_APPROVAL] as Int,
                required = map[FIELD_REQUIRED] as Int,
                size = map[FIELD_SIZE] as Int,
                voted = map[FIELD_VOTED] as List<String>
            )
        }
    }
}