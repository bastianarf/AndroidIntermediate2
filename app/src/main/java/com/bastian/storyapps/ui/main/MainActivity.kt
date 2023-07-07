package com.bastian.storyapps.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bastian.storyapps.R
import com.bastian.storyapps.data.preferences.UserPreferences
import com.bastian.storyapps.databinding.ActivityMainBinding
import com.bastian.storyapps.ui.adapter.LoadingStateAdapter
import com.bastian.storyapps.ui.adapter.StoryAdapter
import com.bastian.storyapps.ui.addstory.AddStoryActivity
import com.bastian.storyapps.ui.login.LoginActivity
import com.bastian.storyapps.ui.maps.MapsActivity
import com.bastian.storyapps.ui.paging.PagingViewModel
import com.bastian.storyapps.ui.paging.ViewModelFactory
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val pagingViewModel: PagingViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                MapsActivity.start(this)
            }
            R.id.add_story -> {
                AddStoryActivity.start(this)
            }
            R.id.action_logout -> {
                logoutAlert()
            }
        }
        return true
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            com.bastian.storyapps.ui.ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]
        mainViewModel.getUser().observe(this) { user ->
            supportActionBar?.title = "Selamat Datang, ${user.name}!"
            val token = user.token
            showLoading(true)
            getData(token)
            showLoading(false)
        }
    }

    private fun getData(token: String) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry() }
        )
        binding.rvStory.setHasFixedSize(true)
        lifecycleScope.launch {
            pagingViewModel.story(token).observe(this@MainActivity) {
                adapter.submitData(lifecycle, it)
            }
        }
    }


    private fun logoutAlert() {
        AlertDialog.Builder(this).apply {
            setMessage("Yakin ingin Log out?")
            setPositiveButton("Ya") { _, _ ->
                logout()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun logout() {
        mainViewModel.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(value : Boolean) {
        binding.pbLoadingScreen.isVisible = value
        binding.rvStory.isVisible = !value

    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            starter.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(starter)
        }
    }
}