package com.bastian.storyapps.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bastian.storyapps.data.api.ApiConfig
import com.bastian.storyapps.data.model.UserModel
import com.bastian.storyapps.data.preferences.UserPreferences
import com.bastian.storyapps.data.response.ListStoriesResponse
import com.bastian.storyapps.data.response.StoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences): ViewModel() {
    private val _listAllStory = MutableLiveData<List<StoryItem>>()
    val listAllStory: LiveData<List<StoryItem>> = _listAllStory

    fun getAllStoriesWithLocation(location: Int, token: String) {
        val client = ApiConfig.getApiService().getAllStoriesWithLocation(location, token)
        client.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(
                call: Call<ListStoriesResponse>,
                response: Response<ListStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _listAllStory.value = responseBody.listStoryItem
                    }
                } else {
                    Log.e("MapsViewModel","OnFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                Log.e("MapsViewModel","onFailure: Gagal")
            }
        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}