package com.example.walking


import com.example.walking.model.*
import com.example.walking.model.LoginDto
import com.example.walking.model.User
import retrofit2.Call
import retrofit2.http.*

interface INetworkService {
    @POST("walking/user/join")
    fun doInsertUser(@Body user: User?): Call<User>

    @POST("login")
    fun login(@Body loginDto: LoginDto): Call<LoginDto>

    @GET("walking/user/oneUser")
    fun doGetOneUser(@Query("email") email: String?): Call<User>
}