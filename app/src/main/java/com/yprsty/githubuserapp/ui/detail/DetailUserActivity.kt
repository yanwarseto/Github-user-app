package com.yprsty.githubuserapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.tabs.TabLayoutMediator
import com.yprsty.githubuserapp.R
import com.yprsty.githubuserapp.data.Result
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUserDetail
import com.yprsty.githubuserapp.databinding.ActivityDetailUserBinding
import com.yprsty.githubuserapp.model.SettingViewModel
import com.yprsty.githubuserapp.model.SettingViewModelFactory
import com.yprsty.githubuserapp.model.UserViewModel
import com.yprsty.githubuserapp.model.ViewModelFactory
import com.yprsty.githubuserapp.ui.adapter.OneFragmentPageAdapter
import com.yprsty.githubuserapp.ui.favorite.FavoriteUserActivity
import com.yprsty.githubuserapp.ui.main.dataStore
import com.yprsty.githubuserapp.data.local.datastore.SettingPreferences
import com.yprsty.githubuserapp.utils.Helper.Companion.loadImage
import com.yprsty.githubuserapp.utils.Helper.Companion.showLoading
import com.yprsty.githubuserapp.utils.Helper.Companion.showToast

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding

    private var isDarkModeLiveData: Boolean = false

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(SettingPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_detail)
        supportActionBar?.elevation = 0f

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val mainViewModel: UserViewModel by viewModels { factory }

        val receiveUserEntity = intent.getParcelableExtra<UsersEntity>(EXTRA_ENTITY) as UsersEntity

        val oneFragmentPageAdapter = OneFragmentPageAdapter(this)
        binding.viewPager.adapter = oneFragmentPageAdapter

        TabLayoutMediator(
            binding.customAppbar.tabs,
            binding.viewPager
        ) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        // Get setting theme based on dataStore
        // And changing theme to dark or light
        settingViewModel.getThemeSetting()
            .observe(this@DetailUserActivity) { isDarkMode: Boolean ->
                val themeMode =
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(themeMode)
                isDarkModeLiveData = isDarkMode
            }

        mainViewModel.findUserWithUsername(receiveUserEntity.login)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> showLoading(binding.frameProgressbar, true)
                        is Result.Success -> showDataUser(result.data)
                        is Result.Error -> {
                            showLoading(binding.frameProgressbar, true)
                            showToast(this@DetailUserActivity, getString(R.string.info_error))
                        }
                    }
                }
            }

        // Trigger FloatingActionButton to add favorite
        binding.fbFavorite.setOnClickListener {
            val (logo, avatarUrl, isFavorite) = receiveUserEntity
            if (!isFavorite) {
                mainViewModel.saveUsers(UsersEntity(logo, avatarUrl, isFavorite))
                showToast(this@DetailUserActivity, getString(R.string.info_add_favorite))
            } else {
                showToast(this@DetailUserActivity, getString(R.string.info_on_favorite))
            }
        }
    }

    // Setup data response to Widget
    private fun showDataUser(userDetail: ResponseUserDetail) {
        showLoading(binding.frameProgressbar, false)
        val (login, company, publicRepos, followers, avatarUrl, following, name, location) = userDetail

        binding.customAppbar.detailProfile.apply {
            ivAvatar.loadImage(this@DetailUserActivity, avatarUrl)
            tvName.text = name
            tvUsername.text = login
            tvCompany.text = company
            tvLocation.text = location

            statistics.apply {
                tvRepositories.text = publicRepos.toString()
                tvFollowers.text = followers.toString()
                tvFollowing.text = following.toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> startActivity(Intent(this, FavoriteUserActivity::class.java))
            R.id.menu_theme -> switchTheme()
        }
        return super.onOptionsItemSelected(item)
    }

    // Switching theme based on dataStore
    private fun switchTheme() {
        if (isDarkModeLiveData) {
            settingViewModel.saveThemeSetting(false)
        } else {
            settingViewModel.saveThemeSetting(true)
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )

        const val EXTRA_ENTITY = "extra_entity"
    }
}
