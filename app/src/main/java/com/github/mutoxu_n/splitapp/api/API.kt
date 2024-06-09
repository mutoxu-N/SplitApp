package com.github.mutoxu_n.splitapp.api

import android.util.Log
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.Settings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class API {
    private val moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.SERVER_ADDRESS)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    suspend fun hello(param: String): Hello? {
        val service = retrofit.create(TestService::class.java)
        val response = service.hello(param = param)
        Log.e("API.hello()", response.toString())
        return response.body()
    }

    suspend fun write(hello: Hello): Hello? {
        if(Auth.get().token == null) return null

        val service = retrofit.create(TestService::class.java)
        val response = service.write(Auth.get().token!!, hello)
        Log.e("API.write()", response.toString())
        return response.body()
    }

    suspend fun test() {
        if(Auth.get().token == null) return

        val service = retrofit.create(TestService::class.java)
        val response = service.test(Auth.get().token!!)
        Log.e("API.test()", response.toString())
    }

    suspend fun reset() {
        if(Auth.get().token == null) return

        val service = retrofit.create(TestService::class.java)
        val response = service.reset(Auth.get().token!!)
        Log.e("API.reset()", response.toString())
    }

    suspend fun roomCreate() {
        if(Auth.get().token == null) return
        val settings = Settings(
            splitUnit = 10,
            permissionReceiptEdit = Role.OWNER.toString(),
            permissionReceiptCreate = Role.NORMAL.toString(),
            onNewMemberRequest = "everyone",
            acceptRate = 50,

        )

        val service = retrofit.create(RoomServices::class.java)
        val response = service.createRoom(Auth.get().token!!, "mutoxu=N", settings)
        Log.e("API.roomCreate()", response.body().toString())
    }
}
