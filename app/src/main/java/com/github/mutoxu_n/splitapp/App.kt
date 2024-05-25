package com.github.mutoxu_n.splitapp

import android.app.Application
import android.content.Context

class App: Application() {
    companion object {
        private var _appContext: Context? = null
        val appContext: Context get() = _appContext!!

    }
    init {
        _appContext = applicationContext
    }
}