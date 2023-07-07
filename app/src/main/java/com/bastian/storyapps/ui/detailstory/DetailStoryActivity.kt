package com.bastian.storyapps.ui.detailstory

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.bastian.storyapps.data.response.StoryItem
import com.bastian.storyapps.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val userId by lazy { intent.getStringExtra(USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getDetailStory()
    }


    private fun getDetailStory(){
        @Suppress("DEPRECATION")
        val getIntent = intent.getParcelableExtra<StoryItem>("key_story") as StoryItem
        binding.tvDetailName.text = getIntent.name
        binding.tvDetailDescription.text = getIntent.description
        Glide.with(this)
            .load(getIntent.photoUrl)
            .into(binding.ivDetailPhoto)
    }
    companion object {
        @JvmStatic
        fun start(context: Context, photoUrl: String, userId: String, pair: Pair<View, String>) {
            val starter = Intent(context, DetailStoryActivity::class.java)
                .putExtra(USER_ID, userId)
                .putExtra(PHOTO_URL, photoUrl)

            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, pair)
            context.startActivity(starter, optionsCompat.toBundle())
        }

        private const val USER_ID = "userId"
        private const val PHOTO_URL = "photo_url"
    }
}