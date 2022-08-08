package com.example.application_story.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.application_story.model.api.LoginResult
import com.example.application_story.model.api.LoginUser
import com.example.application_story.model.api.UserPreference
import com.example.application_story.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: UserPreference
) : ViewModel() {

    val status: LiveData<Boolean> = userRepository.userStatus
    val loginResult: LiveData<LoginResult> = userRepository.loginData

    fun login(loginUser: LoginUser) {
        userRepository.userLogin(loginUser)
    }

    fun getUser(): LiveData<LoginResult> {
        return preference.getUser().asLiveData()
    }

    fun saveUser(loginResult: LoginResult) {
        viewModelScope.launch {
            preference.saveUser(loginResult)
        }
    }



}