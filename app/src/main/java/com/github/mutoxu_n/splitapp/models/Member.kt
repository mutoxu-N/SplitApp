package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlin.jvm.Throws

@IgnoreExtraProperties
data class Member(
    val name: String,
    val uid: String?,
    val weight: Double,
    val role: String,
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_UID = "uid"
        const val FIELD_WEIGHT = "weight"
        const val FIELD_ROLE = "role"

        @Throws(ClassCastException::class)
        fun fromMap(map: Map<String, Any>): Member{
            return Member(
                name = map[FIELD_NAME] as String,
                uid = map[FIELD_UID] as String?,
                weight = map[FIELD_WEIGHT] as Double,
                role = map[FIELD_ROLE] as String,
            )
        }
    }
}
