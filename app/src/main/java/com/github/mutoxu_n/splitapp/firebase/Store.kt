package com.github.mutoxu_n.splitapp.firebase

class Store {
    companion object {
        private var _instance: Store? = null

        fun get(): Store? {
            if(_instance == null) _instance = Store()
            return _instance
        }
    }

    init {
        if(_instance != null)
            throw RuntimeException("Use get() method to get the single instance of this class.")
    }
}