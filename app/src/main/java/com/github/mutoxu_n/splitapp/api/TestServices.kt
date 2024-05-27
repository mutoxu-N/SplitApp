package com.github.mutoxu_n.splitapp.api

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TestService {
    @GET("hello")
    suspend fun hello(
        @Query("param") param: String
    ): Response<Hello>


    @POST("write")
    suspend fun write(
        @Header("token") token: String,
        @Body body: Hello
    ): Response<Hello>

    @POST("test")
    suspend fun test(
        @Header("token") token: String,
    ): Response<Void>

    @POST("reset")
    suspend fun reset(
        @Header("token") token: String,
    ): Response<Void>
}

data class Hello(
    @Json(name = "message") val message: String,
    @Json(name = "param") val param: String,
)
