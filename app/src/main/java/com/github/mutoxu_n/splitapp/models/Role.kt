package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import java.util.Locale

enum class Role(val roleId: Int) {
    NORMAL(0), CREATOR(1), MODERATOR(10), OWNER(99);

    companion object {
        private val map = mapOf(
            NORMAL.roleId to (App.appContext?.getString(R.string.role_normal) ?: NORMAL.name),
            CREATOR.roleId to (App.appContext?.getString(R.string.role_creator) ?: CREATOR.name),
            MODERATOR.roleId to (App.appContext?.getString(R.string.role_moderator) ?: MODERATOR.name),
            OWNER.roleId to (App.appContext?.getString(R.string.role_owner) ?: OWNER.name),
        )

        private val ID = mapOf(
            NORMAL.roleId to "normal",
            CREATOR.roleId to "creator",
            MODERATOR.roleId to "moderator",
            OWNER.roleId to "owner",
        )
        fun fromValue(value: Int) = entries.firstOrNull { it.roleId == value } ?: NORMAL
        fun fromString(role: String) = Role.valueOf(role.uppercase(Locale.getDefault()))
    }

    fun toIDString(): String {
        return ID[roleId] ?: "ERROR"
    }
    override fun toString() = map[roleId] ?: (App.appContext?.getString(R.string.role_unknown) ?: "ERROR")
}
