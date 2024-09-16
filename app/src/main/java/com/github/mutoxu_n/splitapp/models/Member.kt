package com.github.mutoxu_n.splitapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val name: String,
    val uid: String?,
    val weight: Float,
    val role: Role,
): Parcelable {
    companion object {
        val Empty = Member(
            name = "ERROR",
            uid = null,
            weight = 1f,
            role = Role.NORMAL,
        )
    }

    fun toModel(): MemberModel {
        return MemberModel(
            name = name,
            uid = uid.toString(),
            weight = weight.toDouble(),
            role = role.roleId,
        )
    }
}
