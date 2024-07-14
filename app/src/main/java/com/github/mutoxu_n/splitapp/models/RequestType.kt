package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R
import java.util.Locale

enum class RequestType {
    ALWAYS, VOTE, MODERATOR;

    companion object {
        private val map = mapOf(
            ALWAYS to App.appContext.getString(R.string.request_type_always),
            VOTE to App.appContext.getString(R.string.request_type_vote),
            MODERATOR to App.appContext.getString(R.string.request_type_moderator),
        )

        fun fromString(type: String) = RequestType.valueOf(type.uppercase(Locale.getDefault()))
    }

    override fun toString() = map[this] ?: "エラー"
}
