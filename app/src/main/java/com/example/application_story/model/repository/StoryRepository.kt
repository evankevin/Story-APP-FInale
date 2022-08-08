package com.example.application_story.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.application_story.model.api.*
import com.example.application_story.model.data.StoryPagingSource
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyPagingSource: StoryPagingSource
) {

    private val _storyLocation = MutableLiveData<List<ListStoryLoc>>()
    val storyLocation: LiveData<List<ListStoryLoc>> = _storyLocation

    private val _uploadResponse = MutableLiveData<FileUploadResponse>()
    val uploadResponse: LiveData<FileUploadResponse> = _uploadResponse

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                pagingSourceFactory = {
                    storyPagingSource
                }
            ).liveData
        }


    fun getStoryLocation(token: String) {
        apiService.getStoryLocation("Bearer $token")
        .enqueue(object : Callback<GetStoriesLoc> {
            override fun onResponse(
                call: Call<GetStoriesLoc>,
                response: Response<GetStoriesLoc>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _storyLocation.value = responseBody.listStory
                }
            }

            override fun onFailure(call: Call<GetStoriesLoc>, t: Throwable) {
                Log.e(TAG,"On Fail: ${t.message.toString()}")
            }
        })
    }

    fun uploadImage(token: String, file: File, description: String, location: LatLng) {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val requestBodyLat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
        val requestBodyLon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
        apiService.addStory("Bearer $token", imageMultipart, requestDescription,requestBodyLat,requestBodyLon)
        .enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(call: Call<FileUploadResponse>, response: Response<FileUploadResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _uploadResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                Log.e(TAG,"On Fail: ${t.message.toString()}")
            }

        })
    }

    companion object{
        private const val TAG = "StoryRepositoryyyy"
    }


}