package com.example.application_story.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.application_story.model.api.FileUploadResponse
import com.example.application_story.model.api.LoginResult
import com.example.application_story.model.api.UserPreference
import com.example.application_story.model.repository.StoryRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val preference: UserPreference
) : ViewModel() {

    fun getUser(): LiveData<LoginResult> {
        return preference.getUser().asLiveData()
    }

    fun uploadImage(token: String, imgFile: File,description: String, location: LatLng) {
        storyRepository.uploadImage(token, imgFile, description, location)
    }

    val uploadResponse : LiveData<FileUploadResponse> = storyRepository.uploadResponse

}