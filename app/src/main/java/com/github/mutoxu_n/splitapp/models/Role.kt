package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import java.util.Locale

enum class Role(val roleId: Int) {
    NORMAL(0), CREATOR(1), MODERATOR(10), OWNER(99);

    companion object {
        private val map = mapOf(
            NORMAL.roleId to App.appContext.getString(R.string.role_normal),
            CREATOR.roleId to App.appContext.getString(R.string.role_creator),
            MODERATOR.roleId to App.appContext.getString(R.string.role_moderator),
            OWNER.roleId to App.appContext.getString(R.string.role_owner),
        )
        fun fromString(role: String) = Role.valueOf(role.uppercase(Locale.getDefault()))

    }

    override fun toString() = map[roleId] ?: App.appContext.getString(R.string.role_unknown)
}
