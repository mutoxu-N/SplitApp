package com.github.mutoxu_n.splitapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TestService {
    @GET("hello")
    suspend fun hello(
        @Query("param") param: String
    ): Response<Hello>
}

data class Hello(
    val message: String,
    val param: String,
)
