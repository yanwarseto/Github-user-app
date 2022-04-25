package com.yprsty.githubuserapp.utils

import android.content.Context
import com.yprsty.githubuserapp.data.UserRepository
import com.yprsty.githubuserapp.data.local.room.UsersRoomDatabase
import com.yprsty.githubuserapp.data.remote.retrofit.ApiConfig

// Implement Injection pattern for Repository
// All components like apiService and other
// Called/Initiate from here
object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = UsersRoomDatabase.getInstance(context)
        val dao = database.userDao()
        val appExecutors = AppExecutors()
        return UserRepository.getInstance(apiService, dao, appExecutors)
    }
}