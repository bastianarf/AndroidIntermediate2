package com.bastian.storyapps.data.di

import android.content.Context
import com.bastian.storyapps.data.api.ApiConfig
import com.bastian.storyapps.data.database.StoryDatabase
import com.bastian.storyapps.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService, database)
    }
}