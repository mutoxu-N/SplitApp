package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R

enum class SplitUnit(val value: Int) {
    NONE(1), TEN(10), HUNDRED(100), THOUSAND(1000);

    override fun toString(): String {
        return if(App.appContext == null) {
                "¤$value"
            } else {
                "${App.appContext!!.getString(R.string.settings_currency)}$value"
            }
    }
}