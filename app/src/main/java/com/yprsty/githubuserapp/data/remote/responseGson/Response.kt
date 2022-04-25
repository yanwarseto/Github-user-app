package com.yprsty.githubuserapp.data.remote.responseGson

import com.google.gson.annotations.SerializedName

data class ResponseUsersWithUsername(

    @field:SerializedName("items")
    val items: List<ResponseUser>
)

data class ResponseUser(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,
)

data class ResponseUserDetail(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("company")
    val company: String? = null,

    @field:SerializedName("public_repos")
    val publicRepos: Int,

    @field:SerializedName("followers")
    val followers: Int,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    @field:SerializedName("following")
    val following: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("location")
    val location: String? = null,
)