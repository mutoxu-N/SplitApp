package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.squareup.moshi.Json
import kotlin.jvm.Throws

@IgnoreExtraProperties
data class MemberModel(
    @Json(name = FIELD_NAME) val name: String,
    @Json(name = FIELD_UID) val uid: String,
    @Json(name = FIELD_WEIGHT) val weight: Double,
    @Json(name = FIELD_ROLE) val role: Int,
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_UID = "uid"
        const val FIELD_WEIGHT = "weight"
        const val FIELD_ROLE = "role"
    }
}
