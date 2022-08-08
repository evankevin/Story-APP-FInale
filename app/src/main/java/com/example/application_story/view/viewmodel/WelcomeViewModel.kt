package com.example.application_story.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.application_story.model.api.LoginResult
import com.example.application_story.model.api.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel@Inject constructor(
    private val preference: UserPreference
) : ViewModel() {

    fun getUser(): LiveData<LoginResult> {
        return preference.getUser().asLiveData()
    }
}