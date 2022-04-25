package com.yprsty.githubuserapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yprsty.githubuserapp.data.local.datastore.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val preferences: SettingPreferences) : ViewModel() {

    fun getThemeSetting(): LiveData<Boolean> = preferences.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferences.saveThemeSetting(isDarkMode)
        }
    }
}