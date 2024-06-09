package com.github.mutoxu_n.splitapp.api

import com.github.mutoxu_n.splitapp.models.Settings
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
        @Body settings: Settings,
    ): Response<Map<String, Any>>

    @POST("room/{room_id}/join")
    suspend fun joinRoom(
        @Header("token") token: String,
        @Header("name") name: String,
        @Path("room_id") roomId: String,
    ): Response<Map<String, Any>>
}