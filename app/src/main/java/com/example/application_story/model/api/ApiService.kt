package com.example.application_story.model.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun loginUser(
        @Body loginUser: LoginUser
    ): Call<LoginResponse>

    @POST("register")
    fun registerUser(
        @Body registerUser: RegisterUser
    ): Call<RegisResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoriesResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody
    ): Call<FileUploadResponse>

    @GET("stories")
    fun getStoryLocation(
        @Header("Authorization") auth: String,
        @Query("location") loc : Int = 1
    ): Call<GetStoriesLoc>
}