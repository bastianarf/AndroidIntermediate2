package com.bastian.storyapps.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bastian.storyapps.data.api.ApiConfig
import com.bastian.storyapps.data.model.UserModel
import com.bastian.storyapps.data.preferences.UserPreferences
import com.bastian.storyapps.data.response.AddNewStoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _uploadResponse = MutableLiveData<AddNewStoryResponse>()
    val uploadResponse: LiveData<AddNewStoryResponse> = _uploadResponse

    private val _hasUploaded = MutableLiveData<File>()
    val hasUploaded: LiveData<File> = _hasUploaded

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setFile(value: File) {
        _hasUploaded.value = value
    }

    fun uploadImage(token: String, file: File, description: String, lat: RequestBody?, lon: RequestBody?) {
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val apiService = ApiConfig.getApiService()
        val uploadImageRequest = apiService.addNewStory(token ,imageMultipart, descriptionRequestBody, lat, lon)

        uploadImageRequest.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _uploadResponse.value = responseBody!!
                    } else {
                        _uploadResponse.value = AddNewStoryResponse(true, "Response body is null")
                    }
                } else {
                    _uploadResponse.value = AddNewStoryResponse(true, response.message())
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _uploadResponse.value = AddNewStoryResponse(true, t.message ?: "Unknown error occurred")
            }
        })
    }
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}