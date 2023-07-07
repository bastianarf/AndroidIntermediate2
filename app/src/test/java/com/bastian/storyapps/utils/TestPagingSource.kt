package com.bastian.storyapps.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bastian.storyapps.data.response.StoryItem

class TestPagingSource : PagingSource<Int, LiveData<List<StoryItem>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
            return PagingData.from(items)
        }
    }
}