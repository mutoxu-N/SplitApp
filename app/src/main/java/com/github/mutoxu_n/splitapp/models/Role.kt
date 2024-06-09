package com.github.mutoxu_n.splitapp.models

import java.util.Locale

enum class Role(val roleId: Int) {
    NORMAL(0), MODERATOR(1), OWNER(99);

    companion object {
        fun fromString(role: String) = Role.valueOf(role.uppercase(Locale.getDefault()))

    }

    override fun toString() = name.lowercase(Locale.getDefault())
}
