package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.R

enum class SplitUnit(val unit: Int) {
    NONE(1), TEN(10), HUNDRED(100), THOUSAND(1000);

    companion object {
        fun fromUnit(unit: Int): SplitUnit {
            return entries.find { it.unit == unit } ?: NONE
        }
    }

    override fun toString(): String {
        return if(App.appContext == null) {
                "¤$unit"
            } else {
                "${App.appContext!!.getString(R.string.settings_currency)}$unit"
            }
    }
}