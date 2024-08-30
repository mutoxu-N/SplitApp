package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import java.util.Locale

enum class RequestType {
    ALWAYS, VOTE, ACCEPT_BY_MODS, ACCEPT_BY_OWNER;

    companion object {
        private val map = mapOf(
            ALWAYS to (App.appContext?.getString(R.string.request_type_always) ?: ALWAYS.name),
            VOTE to (App.appContext?.getString(R.string.request_type_vote) ?: VOTE.name),
            ACCEPT_BY_MODS to (App.appContext?.getString(R.string.request_type_moderator) ?: ACCEPT_BY_MODS.name),
        )
        private val ID = mapOf(
            ALWAYS to "always",
            VOTE to "vote",
            ACCEPT_BY_MODS to "accept_by_mods",
            ACCEPT_BY_OWNER to "accept_by_owner",
        )

        fun fromString(type: String) = RequestType.valueOf(type.uppercase(Locale.getDefault()))
    }

    fun toIDString(): String = ID[this] ?: "ERROR"
    override fun toString() = map[this] ?: (App.appContext?.getString(R.string.request_type_error) ?: "ERROR")
}
