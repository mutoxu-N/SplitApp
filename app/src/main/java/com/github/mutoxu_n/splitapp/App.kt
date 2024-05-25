package com.github.mutoxu_n.splitapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class App: Application() {
    companion object {
        private var _appContext: Context? = null
        val appContext: Context get() = _appContext!!

    }

    override fun onCreate() {
        super.onCreate()
        _appContext = applicationContext

        FirebaseApp.initializeApp(appContext)
    }

}