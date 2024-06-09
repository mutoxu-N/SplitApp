package com.github.mutoxu_n.splitapp.models

import java.util.Locale

enum class Role(val roleId: Int) {
    NORMAL(0), MODERATOR(1), OWNER(99);

    companion object {
        private val map = mapOf(
            0 to "NORMAL",
            1 to "MODERATOR",
            99 to "OWNER",
        )
        fun fromString(role: String) = Role.valueOf(role.uppercase(Locale.getDefault()))

    }

    override fun toString() = map[roleId] ?: "UNKNOWN"
}
