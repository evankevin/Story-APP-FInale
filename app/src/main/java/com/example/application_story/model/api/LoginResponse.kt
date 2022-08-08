package com.example.application_story.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponse(
    val loginResult: LoginResult,
    val error: Boolean,
    val message: String
):Parcelable

@Parcelize
data class LoginResult(
    val name: String,
    val userId: String,
    val token: String
):Parcelable

data class LoginUser(
    @field:SerializedName("email")
    var email: String,
    @field:SerializedName("password")
    var password: String,
)

@Parcelize
data class RegisResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
):Parcelable

@Parcelize
data class RegisterUser(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("email")
    var email: String,

    @field:SerializedName("password")
    var password: String,
):Parcelable




