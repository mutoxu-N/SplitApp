package com.github.mutoxu_n.splitapp.firebase

class Auth {
    companion object {
        private var _instance: Auth? = null

        fun get(): Auth? {
            if(_instance == null) _instance = Auth()
            return _instance
        }
    }

    init {
        if(_instance != null)
            throw RuntimeException("Use get() method to get the single instance of this class.")
    }
}
