package com.yprsty.githubuserapp.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yprsty.githubuserapp.R
import com.yprsty.githubuserapp.data.Result
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.databinding.ActivityMainBinding
import com.yprsty.githubuserapp.model.SettingViewModel
import com.yprsty.githubuserapp.model.SettingViewModelFactory
import com.yprsty.githubuserapp.model.UserViewModel
import com.yprsty.githubuserapp.model.ViewModelFactory
import com.yprsty.githubuserapp.ui.adapter.UserAdapter
import com.yprsty.githubuserapp.ui.favorite.FavoriteUserActivity
import com.yprsty.githubuserapp.data.local.datastore.SettingPreferences
import com.yprsty.githubuserapp.utils.Helper.Companion.setupLayoutManager
import com.yprsty.githubuserapp.utils.Helper.Companion.showLoading
import com.yprsty.githubuserapp.utils.Helper.Companion.showToast

// Initiate Singleton pattern for DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isDarkModeLiveData: Boolean = false

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(SettingPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_main)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = binding.svUsername
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val mainViewModel: UserViewModel by viewModels { factory }

        // Get setting theme based on dataStore
        // And changing theme to dark or light
        settingViewModel.getThemeSetting().observe(this@MainActivity) { isDarkMode: Boolean ->
            val themeMode =
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(themeMode)
            isDarkModeLiveData = isDarkMode
        }

        val userAdapter = UserAdapter { user -> // Trigger for add or delete favorite user
            if (user.isFavorite) {
                mainViewModel.deleteUsers(user)
                showToast(this@MainActivity, getString(R.string.info_remove))
            } else {
                mainViewModel.saveUsers(user)
                showToast(this@MainActivity, getString(R.string.info_add_favorite))
            }
        }

        mainViewModel.username.observe(this@MainActivity) { username ->
            mainViewModel.findAllUserWithUsername(username).observe(this@MainActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> showLoading(binding.frameProgressbar, true)
                        is Result.Success -> showListUser(result.data, userAdapter)
                        is Result.Error -> handleError()
                    }
                }
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel.setUsernameLiveData(query)
                searchView.setQuery("", false) // clear the text
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.rvUsers.apply {
            setupLayoutManager(this, context)
            setHasFixedSize(true)
            adapter = userAdapter
        }
    }

    private fun showListUser(data: List<UsersEntity>, adapter: UserAdapter) {
        setInfoText(data.size)
        showLoading(binding.frameProgressbar, false)

        if (data.isNotEmpty()) {
            adapter.submitList(null)
            adapter.submitList(data)
        } else {
            adapter.submitList(emptyList())
        }
    }

    private fun setInfoText(size: Int) {
        val isNotFound = size == 0
        binding.tvInfo.text = if (isNotFound)
            getString(R.string.info_result_empty)
        else
            getString(R.string.info_result_users, size)
    }

    private fun handleError() {
        showLoading(binding.frameProgressbar, false)
        showToast(this@MainActivity, getString(R.string.info_error))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> startActivity(
                Intent(
                    this@MainActivity,
                    FavoriteUserActivity::class.java
                )
            )
            R.id.menu_theme -> switchTheme()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchTheme() {
        if (isDarkModeLiveData) {
            settingViewModel.saveThemeSetting(false)
        } else {
            settingViewModel.saveThemeSetting(true)
        }
    }
}