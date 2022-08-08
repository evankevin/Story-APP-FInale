package com.example.application_story.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.application_story.model.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
) {
    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean> = _userStatus

    private val _loginData = MutableLiveData<LoginResult>()
    val loginData: LiveData<LoginResult> = _loginData

    fun userLogin(loginUser: LoginUser) {
        apiService.loginUser(loginUser)
        .enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true
                    _loginData.value = responseBody.loginResult
                } else {
                    _userStatus.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG,"On Fail: ${t.message.toString()}")
            }
        })
    }

    fun userRegister(registerUser: RegisterUser) {
        apiService.registerUser(registerUser)
        .enqueue(object : Callback<RegisResponse> {
            override fun onResponse(
                call: Call<RegisResponse>,
                response: Response<RegisResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true
                    if (!responseBody.error) {
                        userLogin(LoginUser(registerUser.email, registerUser.password))
                    }
                } else {
                    _userStatus.value = false
                }
            }

            override fun onFailure(call: Call<RegisResponse>, t: Throwable) {
                Log.e(TAG,"On Fail: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "UseeeerRepositoryyy"
    }

}