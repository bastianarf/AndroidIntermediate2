package com.bastian.storyapps.data.response

import com.google.gson.annotations.SerializedName

class ListStoriesResponse(

    @field:SerializedName("listStory")
    val listStoryItem: List<StoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)