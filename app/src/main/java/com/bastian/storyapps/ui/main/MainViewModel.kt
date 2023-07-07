package com.bastian.storyapps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bastian.storyapps.data.model.UserModel
import com.bastian.storyapps.data.preferences.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}