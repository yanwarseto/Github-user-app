package com.yprsty.githubuserapp.ui.favorite

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yprsty.githubuserapp.R
import com.yprsty.githubuserapp.databinding.ActivityFavoriteUserBinding
import com.yprsty.githubuserapp.model.SettingViewModel
import com.yprsty.githubuserapp.model.SettingViewModelFactory
import com.yprsty.githubuserapp.model.UserViewModel
import com.yprsty.githubuserapp.model.ViewModelFactory
import com.yprsty.githubuserapp.ui.adapter.UserAdapter
import com.yprsty.githubuserapp.ui.main.dataStore
import com.yprsty.githubuserapp.data.local.datastore.SettingPreferences
import com.yprsty.githubuserapp.utils.Helper
import com.yprsty.githubuserapp.utils.Helper.Companion.setupLayoutManager

class FavoriteUserActivity : AppCompatActivity() {

    private var _activityFavoriteUser: ActivityFavoriteUserBinding? = null
    private val binding get() = _activityFavoriteUser

    private lateinit var userAdapter: UserAdapter

    private var isDarkModeLiveData: Boolean = false

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(SettingPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityFavoriteUser = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = getString(R.string.title_favorite)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val mainViewModel: UserViewModel by viewModels { factory }

        settingViewModel.getThemeSetting()
            .observe(this@FavoriteUserActivity) { isDarkMode: Boolean ->
                val themeMode =
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(themeMode)
                isDarkModeLiveData = isDarkMode
            }

        userAdapter = UserAdapter { user -> // Trigger for add or delete favorite user
            if (user.isFavorite) {
                mainViewModel.deleteUsers(user)
                Helper.showToast(this@FavoriteUserActivity, getString(R.string.info_remove))
            } else {
                mainViewModel.saveUsers(user)
                Helper.showToast(this@FavoriteUserActivity, getString(R.string.info_add_favorite))
            }
        }

        mainViewModel.getFavoriteUsers().observe(this) { userFavorite ->
            if (userFavorite != null) {
                userAdapter.submitList(userFavorite)
            }
        }

        binding?.rvFavoriteUsers?.apply {
            setupLayoutManager(this, this@FavoriteUserActivity)
            setHasFixedSize(true)
            adapter = userAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_appbar, menu)
        menu.findItem(R.id.menu_favorite).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_theme -> switchTheme()
            R.id.menu_settings -> {} // TODO NOTHING
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

    override fun onDestroy() {
        super.onDestroy()
        _activityFavoriteUser = null
    }
}