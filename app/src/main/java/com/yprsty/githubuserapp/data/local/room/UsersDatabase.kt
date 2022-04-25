package com.yprsty.githubuserapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yprsty.githubuserapp.data.local.entity.UsersEntity

@Database(version = 1, entities = [UsersEntity::class])
abstract class UsersRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UsersDao

    companion object {
        @Volatile
        private var instance: UsersRoomDatabase? = null
        fun getInstance(context: Context): UsersRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UsersRoomDatabase::class.java,
                    "UsersFavorite"
                ).build()
            }
    }
}