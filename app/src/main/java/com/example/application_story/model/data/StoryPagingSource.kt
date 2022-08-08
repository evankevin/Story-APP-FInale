package com.example.application_story.model.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.application_story.model.api.ListStoryItem
import com.example.application_story.model.api.UserPreference
import com.example.application_story.model.api.ApiService
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

class StoryPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { position ->
            val anchorPage = state.closestPageToPosition(position)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = userPreference.getUser().first().token
                if (token.trim().isNotEmpty()) {
                    val responseData =
                        apiService.getStory("Bearer $token", position, params.loadSize)
                    if (responseData.isSuccessful) {
                        LoadResult.Page(
                            data = responseData.body()?.listStory ?: emptyList(),
                            prevKey = if (position == 1) null else position - 1,
                            nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                        )
                    } else {
                        LoadResult.Error(Exception("Faileddd!"))
                    }
                } else {
                    LoadResult.Error(Exception("Faileddd"))
                }

        } catch (e: Exception) {
            Log.d("exception", "load: Error ${e.message}")
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
