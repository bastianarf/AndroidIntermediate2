package com.bastian.storyapps.data.response

import com.google.gson.annotations.SerializedName

data class StoryDetailResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("story")
    val story: StoryItem? = null
)