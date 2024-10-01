package com.github.mutoxu_n.splitapp.api

import android.util.Log
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.models.MemberModel
import com.github.mutoxu_n.splitapp.models.ReceiptModel
import com.github.mutoxu_n.splitapp.models.Settings
import com.github.mutoxu_n.splitapp.models.SettingsModel
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

    suspend fun createRoom(settings: Settings) {
        if (Auth.token == null) return
        val name = App.displayName.value ?: return

        val service = retrofit.create(RoomServices::class.java)
        val settingsModel = settings.toModel()
        val body = RoomCreateBody(settingsModel = settingsModel)
        val response = service.createRoom(Auth.token!!, name, body)
        Log.d("API.roomCreate()", response.body().toString())
        App.updateRoomId(response.body()!!["room_id"] as String?)
    }

    suspend fun joinRoom(roomId: String, displayName: String, callBack: (Boolean, Boolean) -> Unit = {_, _ ->}) {
        if (Auth.token == null) return

        val service = retrofit.create(RoomServices::class.java)
        val response = service.joinRoom(Auth.token!!, roomId, displayName)

        Log.d("API.joinRoom()", response.body().toString())
        response.body()?.let {
            try {
                if(it["joined"]!! as Boolean) {
                    App.updateDisplayName(displayName)
                    App.updateRoomId(roomId)
                    callBack(true, false)

                } else if(it["pending"]!! as Boolean) {
                    callBack(false, true)

                } else {
                    callBack(false, false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
    }

    suspend fun cancel(roomId: String) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val response = service.cancel(Auth.token!!, roomId)
        Log.d("API.cancel()", response.body().toString())
    }

    suspend fun vote(roomId: String, voteFor: String, accepted: Boolean) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = VoteBody(
            voteFor = voteFor,
            accepted = accepted,
        )
        val response = service.vote(Auth.token!!, roomId, body)
        Log.d("API.vote()", response.body().toString())
    }

    suspend fun accept(roomId: String, acceptFor: String, accepted: Boolean) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = AcceptBody(
            acceptFor = acceptFor,
            accepted = accepted,
        )
        val response = service.accept(Auth.token!!, roomId, body)
        Log.d("API.vote()", response.body().toString())
    }

    suspend fun createGuest(roomId: String, name: String, callBack: (Boolean) -> Unit = {}) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = GuestCreateBody(
            name = name,
        )
        val response = service.createGuest(Auth.token!!, roomId, body)
        callBack(response.body()?.get("succeed") as Boolean? ?: false)
        Log.d("API.createGuest()", response.body().toString())
    }

    suspend fun deleteGuest(roomId: String, name: String, callBack: (Boolean) -> Unit = {}) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = GuestDeleteBody(
            name = name,
        )
        val response = service.deleteGuest(Auth.token!!, roomId, body)
        callBack(response.body()?.get("succeed") as Boolean? ?: false)
        Log.d("API.deleteGuest()", response.body().toString())
    }


    suspend fun editMember(roomId: String, oldName: String, new: MemberModel) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = EditMemberBody(
            oldName = oldName,
            newMemberModel = new,
        )
        val response = service.editMember(Auth.token!!, roomId, body)
        Log.d("API.editMember()", response.body().toString())
    }

    suspend fun editSettings(roomId: String, settingsModel: SettingsModel, callBack: (Boolean)-> Unit = {}) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = EditSettingsBody(
            settingsModel = settingsModel,
        )
        val response = service.editSettings(Auth.token!!, roomId, body)
        callBack(response.body()?.get("succeed") as Boolean? ?: false)
    }

    suspend fun deleteRoom(roomId: String) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        service.deleteRoom(Auth.token!!, roomId)
    }

    suspend fun createReceipt(roomId: String, receiptModel: ReceiptModel, callBack: (Boolean)-> Unit = {}) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val response = service.addReceipt(Auth.token!!, roomId, receiptModel)
        callBack(response.body()?.get("succeed") as Boolean? ?: false)
        Log.d("API.createReceipt()", response.body().toString())
    }

    suspend fun editReceipt(roomId: String, receiptModel: ReceiptModel, callBack: (Boolean)-> Unit = {}) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = EditReceiptBody(
            receiptId = receiptModel.id,
            receiptModel = receiptModel,
        )
        val response = service.editReceipt(Auth.token!!, roomId, body)
        callBack(response.body()?.get("succeed") as Boolean? ?: false)
        Log.d("API.editReceipt()", response.body().toString())
    }
}
