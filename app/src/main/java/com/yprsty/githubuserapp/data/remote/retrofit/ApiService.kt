package com.yprsty.githubuserapp.data.remote.retrofit

import com.yprsty.githubuserapp.BuildConfig
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUser
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUserDetail
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUsersWithUsername
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/users")
    fun getListUsers(
        @Query("q") username: String
    ): Call<ResponseUsersWithUsername>

    @GET("users/{username}")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    fun getUser(
        @Path("username") username: String
    ): Call<ResponseUserDetail>

    @GET("users/{username}/followers")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    fun getUserFollowers(
        @Path("username") username: String
    ): Call<List<ResponseUser>>

    @GET("users/{username}/following")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    fun getUserFollowing(
        @Path("username") username: String
    ): Call<List<ResponseUser>>
}