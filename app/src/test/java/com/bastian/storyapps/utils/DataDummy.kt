package com.bastian.storyapps.utils

import com.bastian.storyapps.data.response.StoryItem

object DataDummy {

    fun dummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0 .. 100) {
            val listStory = StoryItem(
                i.toString(),
                "name : $i",
                "description : $i",
                "url : $i",
                "created + $i",
                32.227821,
                -78.192349
            )
            items.add(listStory)
        }
        return items
    }
}