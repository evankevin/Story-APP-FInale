package com.example.application_story.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.application_story.databinding.ActivityDetailBinding
import com.example.application_story.model.api.ListStoryItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val storyDetail = intent.getParcelableExtra<ListStoryItem>(STORY_DETAIL) as ListStoryItem
        setDataView(storyDetail)
    }

    private fun setDataView(storyDetail: ListStoryItem) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(storyDetail.photoUrl).
                into(photoDetail)
            postText.text = "Posted by: " + storyDetail.name
            descDetail.text = "  "+storyDetail.description
        }
    }

    companion object {
        const val STORY_DETAIL = "STORY_DATA"
    }
}