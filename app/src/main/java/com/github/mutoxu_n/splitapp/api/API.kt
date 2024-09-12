package com.github.mutoxu_n.splitapp.api

import android.util.Log
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.BuildConfig
import com.github.mutoxu_n.splitapp.common.Auth
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.models.Member
import com.github.mutoxu_n.splitapp.models.ReceiptModel
import com.github.mutoxu_n.splitapp.models.Role
import com.github.mutoxu_n.splitapp.models.SettingsModel
import com.github.mutoxu_n.splitapp.models.MemberModel
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
        if (Auth.token == null) return null

        val service = retrofit.create(TestService::class.java)
        val response = service.write(Auth.token!!, hello)
        Log.e("API.write()", response.toString())
        return response.body()
    }

    suspend fun test() {
        if (Auth.token == null) return

        val service = retrofit.create(TestService::class.java)
        val response = service.test(Auth.token!!)
        Log.e("API.test()", response.toString())
    }

    suspend fun reset() {
        if (Auth.token == null) return

        val service = retrofit.create(TestService::class.java)
        val response = service.reset(Auth.token!!)
        Log.e("API.reset()", response.body().toString())
    }

    suspend fun createRoom(settings: Settings) {
        if (Auth.token == null) return
        val name = App.displayName.value ?: return

        val service = retrofit.create(RoomServices::class.java)
        val settingsModel = settings.toModel()
        val body = RoomCreateBody(settingsModel = settingsModel)
        val response = service.createRoom(Auth.token!!, name, body)
        Log.e("API.roomCreate()", response.body().toString())

        response.body().let {
            try {
                it ?: return

                App.updateMe(Member(
                    name = (it["me"]!! as Map<*, *>)["name"] as String,
                    uid = (it["me"]!! as Map<*, *>)["uid"]!! as String,
                    weight = ((it["me"]!! as Map<*, *>)["weight"]!! as Double).toFloat(),
                    role = Role.fromValue((it["me"]!! as Map<*, *>)["role"]!! as Double),
                ))
                Store.updateSettings(settings)
                App.updateRoomId(it["room_id"] as String)

            } catch (e: ClassCastException) {
                e.printStackTrace()
                return

            } catch (e: NullPointerException) {
                e.printStackTrace()
                return
            }
        }
    }

    suspend fun joinRoom(roomId: String, displayName: String, callBack: (Boolean) -> Unit = {}) {
        if (Auth.token == null) return

        val service = retrofit.create(RoomServices::class.java)
        val response = service.joinRoom(Auth.token!!, roomId, displayName)

        Log.e("API.joinRoom()", response.body().toString())
        response.body()?.let {
            try {
                if(it["joined"]!! as Boolean) {
                    App.updateDisplayName(displayName)
                    val d = it["me"] as Map<*, *>
                    App.updateMe(Member(
                        name = d["name"] as String,
                        uid = d["uid"] as String?,
                        weight = (d["weight"]!! as Double).toFloat(),
                        role = Role.fromValue((it["me"]!! as Map<*, *>)["role"]!! as Double),
                    ))
                    App.updateRoomId(roomId)
                    callBack(true)

                } else {
                    callBack(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
    }

    suspend fun vote(roomId: String, voteFor: String, accepted: Boolean) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = VoteBody(
            voteFor = voteFor,
            accepted = accepted,
        )
        val response = service.vote(Auth.token!!, roomId, body)
        Log.e("API.vote()", response.body().toString())
    }

    suspend fun accept(roomId: String, acceptFor: String, accepted: Boolean) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = AcceptBody(
            acceptFor = acceptFor,
            accepted = accepted,
        )
        val response = service.accept(Auth.token!!, roomId, body)
        Log.e("API.vote()", response.body().toString())
    }

    suspend fun createGuest(roomId: String, name: String) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val memberModel = MemberModel(
            name = name,
            uid = "",
            weight = 1.0,
            role = Role.NORMAL.roleId,
        )
        val response = service.createGuest(Auth.token!!, roomId, memberModel)
        Log.e("API.createGuest()", response.body().toString())
    }


    suspend fun editMember(roomId: String, name: String, new: MemberModel) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = EditMemberBody(
            oldName = name,
            newMemberModel = new,
        )
        val response = service.editMember(Auth.token!!, roomId, body)
        Log.e("API.editMember()", response.body().toString())
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
        Log.e("API.createReceipt()", response.body().toString())
    }

    suspend fun editReceipt(roomId: String, receiptId: String, receiptModel: ReceiptModel) {
        if (Auth.token == null) return
        val service = retrofit.create(RoomServices::class.java)
        val body = EditReceiptBody(
            receiptId = receiptId,
            receiptModel = receiptModel,
        )
        val response = service.editReceipt(Auth.token!!, roomId, body)
        Log.e("API.editReceipt()", response.body().toString())
    }
}
