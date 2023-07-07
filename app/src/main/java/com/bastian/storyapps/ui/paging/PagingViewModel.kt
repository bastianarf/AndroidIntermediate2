package com.bastian.storyapps.ui.paging

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bastian.storyapps.data.di.Injection
import com.bastian.storyapps.data.response.StoryItem
import com.bastian.storyapps.paging.StoryRepository

class PagingViewModel (private val repository: StoryRepository): ViewModel() {

    private val _stories = MutableLiveData<PagingData<StoryItem>>()

    fun story(token: String): LiveData<PagingData<StoryItem>> {
        val response = repository.getStory(token).cachedIn(viewModelScope)
        _stories.value = response.value
        return response
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PagingViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}