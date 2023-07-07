package com.bastian.storyapps.data.response

import com.bastian.storyapps.data.model.UserModel
import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: UserModel,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)