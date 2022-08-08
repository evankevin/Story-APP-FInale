package com.example.application_story.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.application_story.databinding.StoryItemRowBinding
import com.example.application_story.model.api.ListStoryItem
import com.example.application_story.activities.DetailActivity

class StoryListAdapter : PagingDataAdapter<ListStoryItem, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: StoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: ListStoryItem) {
            val context = itemView.context
                Glide.with(itemView.context)
                    .load(dataItem.photoUrl)
                    .into(binding.imgItemPhoto)
                binding.rvName.text = dataItem.name
                binding.rvDesc.text = dataItem.description


            itemView.setOnClickListener {
                Toast.makeText(context, "You choose ${dataItem.name}'s story", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.STORY_DETAIL, dataItem)
                context.startActivity(intent)
            }
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {

            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.photoUrl == newItem.photoUrl &&
                        oldItem.description == newItem.description
            }
        }
    }


}
