package com.bastian.storyapps.data.response

import com.google.gson.annotations.SerializedName

class ErrorLoginResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)