package com.yprsty.githubuserapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class UsersEntity(
    @PrimaryKey
    @ColumnInfo(name = "username")
    val login: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean
) : Parcelable