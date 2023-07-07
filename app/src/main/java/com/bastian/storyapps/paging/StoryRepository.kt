package com.bastian.storyapps.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bastian.storyapps.data.api.ApiService
import com.bastian.storyapps.data.database.StoryDatabase
import com.bastian.storyapps.data.response.StoryItem

class StoryRepository(private val apiService: ApiService, database: StoryDatabase) {
    fun getStory(token: String): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, apiService)
            }
        ).liveData
    }
}