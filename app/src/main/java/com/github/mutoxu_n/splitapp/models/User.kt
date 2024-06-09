package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.squareup.moshi.Json
import kotlin.jvm.Throws

@IgnoreExtraProperties
data class User(
    @Json(name = FIELD_NAME) val name: String,
    @Json(name = FIELD_UID) val uid: String?,
    @Json(name = FIELD_WEIGHT) val weight: Double,
    @Json(name = FIELD_ROLE) val role: String,
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_UID = "uid"
        const val FIELD_WEIGHT = "weight"
        const val FIELD_ROLE = "role"

        @Throws(ClassCastException::class)
        fun fromMap(map: Map<String, Any>): User{
            return User(
                name = map[FIELD_NAME] as String,
                uid = map[FIELD_UID] as String?,
                weight = map[FIELD_WEIGHT] as Double,
                role = map[FIELD_ROLE] as String,
            )
        }
    }
}
