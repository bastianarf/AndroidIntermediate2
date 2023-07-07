package com.bastian.storyapps.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bastian.storyapps.data.response.StoryItem
import com.bastian.storyapps.databinding.StoryItemBinding
import com.bastian.storyapps.ui.detailstory.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<StoryItem, StoryAdapter.ListStoryViewHolder>(DIFF_CALLBACK) {

    class ListStoryViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: StoryItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .into(ivItemPhoto)

                tvItemName.text = item.name
                tvItemDescription.text = item.description

                itemView.setOnClickListener {
                    val detailIntent = Intent(itemView.context, DetailStoryActivity::class.java)
                    detailIntent.putExtra("key_story", item)
                    itemView.context.startActivity(detailIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding =
            StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bindItem(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}