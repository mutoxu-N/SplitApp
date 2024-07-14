package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import java.util.Locale

enum class Role(val roleId: Int) {
    NORMAL(0), MODERATOR(1), OWNER(99);

    companion object {
        private val map = mapOf(
            0 to App.appContext.getString(R.string.role_normal),
            1 to App.appContext.getString(R.string.role_moderator),
            99 to App.appContext.getString(R.string.role_owner),
        )
        fun fromString(role: String) = Role.valueOf(role.uppercase(Locale.getDefault()))

    }

    override fun toString() = map[roleId] ?: App.appContext.getString(R.string.role_unknown)
}
