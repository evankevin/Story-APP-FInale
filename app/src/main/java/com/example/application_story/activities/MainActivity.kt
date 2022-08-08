package com.example.application_story.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application_story.R
import com.example.application_story.databinding.ActivityMainBinding
import com.example.application_story.view.adapter.LoadingStateAdapter
import com.example.application_story.view.adapter.StoryListAdapter
import com.example.application_story.view.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainviewmodel by viewModels<MainViewModel>()
    private lateinit var storiesAdapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAdapter()
        checkUserLogin()

        binding.addStoryButton.setOnClickListener {
            Toast.makeText(this, "You choose add story", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

    }

    private fun checkUserLogin() {
        mainviewmodel.getUser().observe(this) {
            if (it.token.trim() == "") {
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                binding.nameHome.setText("Welcome ${it.name}, here is the latest story feed :")
                setUserData()
            }
        }
    }

    private fun setupAdapter() {
        storiesAdapter = StoryListAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvStory.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storiesAdapter.retry()
            }
        )
    }

    private fun setUserData() {
        mainviewmodel.story.observe(this) { pagingData ->
            storiesAdapter.submitData(lifecycle, pagingData)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logoutUser()
                true
            }
            R.id.maps_act -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun logoutUser() {
        mainviewmodel.logout()
        checkUserLogin()
    }
}