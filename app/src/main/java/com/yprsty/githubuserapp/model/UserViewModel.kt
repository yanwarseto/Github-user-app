package com.yprsty.githubuserapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yprsty.githubuserapp.data.UserRepository
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import kotlinx.coroutines.launch

class UserViewModel(private val userRepo: UserRepository) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    fun setUsernameLiveData(username: String) {
        _username.value = username
    }

    fun findAllUserWithUsername(username: String) = userRepo.getAllUserWithUsername(username)

    fun findUserWithUsername(username: String) = userRepo.getUserWithUsername(username)

    fun showUserFollowers() = userRepo.getFollowers()

    fun showUserFollowing() = userRepo.getFollowing()

    fun getFavoriteUsers() = userRepo.getFavoriteUsers()

    fun saveUsers(users: UsersEntity) {
        viewModelScope.launch { userRepo.setBookmarkedUsers(users, true) }
    }

    fun deleteUsers(users: UsersEntity) {
        viewModelScope.launch { userRepo.setBookmarkedUsers(users, false) }
    }
}