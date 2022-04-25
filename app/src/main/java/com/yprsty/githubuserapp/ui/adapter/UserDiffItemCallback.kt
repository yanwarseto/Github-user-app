package com.yprsty.githubuserapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.yprsty.githubuserapp.data.local.entity.UsersEntity

class UserDiffItemCallback : DiffUtil.ItemCallback<UsersEntity>() {
    override fun areItemsTheSame(oldItem: UsersEntity, newItem: UsersEntity): Boolean {
        return oldItem.login == newItem.login
    }

    override fun areContentsTheSame(oldItem: UsersEntity, newItem: UsersEntity): Boolean {
        return oldItem == newItem
    }
}
