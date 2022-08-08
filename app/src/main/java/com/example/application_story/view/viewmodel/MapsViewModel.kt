package com.example.application_story.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.application_story.model.api.ListStoryLoc
import com.example.application_story.model.api.LoginResult
import com.example.application_story.model.api.UserPreference
import com.example.application_story.model.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val preference: UserPreference
) : ViewModel() {

    val storyLoc: LiveData<List<ListStoryLoc>> = storyRepository.storyLocation

    fun getStoryWithLocation(token: String) {
        storyRepository.getStoryLocation(token)
    }

    fun getUser(): LiveData<LoginResult> {
        return preference.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }
}