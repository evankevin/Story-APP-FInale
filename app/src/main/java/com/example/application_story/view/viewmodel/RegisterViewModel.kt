package com.example.application_story.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application_story.model.api.LoginResult
import com.example.application_story.model.api.RegisterUser
import com.example.application_story.model.api.UserPreference
import com.example.application_story.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: UserPreference
) : ViewModel() {

    val status: LiveData<Boolean> = userRepository.userStatus
    val loginResult: LiveData<LoginResult> = userRepository.loginData

    fun regisUser(registerUser: RegisterUser) {
        userRepository.userRegister(registerUser)
    }

    fun saveUser(loginResult: LoginResult) {
        viewModelScope.launch {
            preference.saveUser(loginResult)
        }

    }
}