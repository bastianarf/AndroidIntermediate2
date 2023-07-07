package com.bastian.storyapps.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bastian.storyapps.data.api.ApiService
import com.bastian.storyapps.data.response.StoryItem

class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, StoryItem>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
                return try {
                        val position = params.key ?: INITIAL_PAGE_INDEX
                        val responseData = apiService.getAllStories("Bearer $token", position, params.loadSize)
                        Log.d("paging","$responseData")
                        return LoadResult.Page(
                                data = responseData.listStoryItem,
                                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                                nextKey = if (responseData.listStoryItem.isEmpty()) null else position + 1
                        )
                } catch (exception: Exception) {
                        LoadResult.Error(exception)
                }
        }

        override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                        val anchorPage = state.closestPageToPosition(anchorPosition)
                        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
        }

        private companion object {
                const val INITIAL_PAGE_INDEX = 1
        }
}

