package com.github.mutoxu_n.splitapp.api

import com.github.mutoxu_n.splitapp.models.ReceiptModel
import com.github.mutoxu_n.splitapp.models.SettingsModel
import com.github.mutoxu_n.splitapp.models.MemberModel
import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomServices {
    @POST("room/create")
    suspend fun createRoom(
        @Header("token") token: String,
        @Header("name") name: String,
        @Body body: RoomCreateBody,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/join")
    suspend fun joinRoom(
        @Header("token") token: String,
        @Header("name") name: String,
        @Path("room_id") roomId: String,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/vote")
    suspend fun vote(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: VoteBody,
    ): Response<Map<String, Any>>


    @POST("room/{room_id}/accept")
    suspend fun accept(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: AcceptBody,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/create_guest")
    suspend fun createGuest(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: MemberModel,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/member/edit")
    suspend fun editMember(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: EditMemberBody,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/settings/edit")
    suspend fun editSettings(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: EditSettingsBody,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/delete")
    suspend fun deleteRoom(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/receipt/add")
    suspend fun addReceipt(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: ReceiptModel,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/receipt/edit")
    suspend fun editReceipt(
        @Header("token") token: String,
        @Path("room_id") roomId: String,
        @Body body: EditReceiptBody,
    ): Response<Map<String, Any>>
}

data class RoomCreateBody(
    @Json(name = "settings") val settingsModel: SettingsModel,
)

data class VoteBody(
    @Json(name = "vote_for") val voteFor: String,
    @Json(name = "accepted") val accepted: Boolean,
)

data class AcceptBody(
    @Json(name = "accept_for") val acceptFor: String,
    @Json(name = "accepted") val accepted: Boolean,
)

data class EditMemberBody(
    @Json(name = "old") var oldName: String,
    @Json(name = "new") var newMemberModel: MemberModel,
)

data class EditSettingsBody(
    @Json(name = "settings") val settingsModel: SettingsModel,
)

data class EditReceiptBody(
    @Json(name = "receipt_id") var receiptId: String,
    @Json(name = "receipt") var receiptModel: ReceiptModel,
)