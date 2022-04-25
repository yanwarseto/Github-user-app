package com.yprsty.githubuserapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yprsty.githubuserapp.R
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.databinding.ItemRowUserBinding
import com.yprsty.githubuserapp.utils.Helper.Companion.loadImage
import com.yprsty.githubuserapp.utils.Helper.Companion.sendIntentWithData

/**
 * Handle all item on recycler view
 * @param LambdaFunction
 * */
class UserAdapter(
    private val onBookmarkClick: (UsersEntity) -> Unit
) : ListAdapter<UsersEntity, UserAdapter.UserViewHolder>(UserDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        val ivFavorite = holder.binding.ivFavorite
        if (user.isFavorite) {
            ivFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    ivFavorite.context, R.drawable.ic_favorite_filled_red
                )
            )
        } else {
            ivFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    ivFavorite.context, R.drawable.ic_favorite_border_red
                )
            )
        }

        // Triggered icon to add or delete user favorite
        ivFavorite.setOnClickListener { onBookmarkClick(user) }
    }

    class UserViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersEntity) {
            binding.ivItemAvatar.loadImage(itemView.context, user.avatarUrl)
            binding.tvItemName.text = user.login
            itemView.setOnClickListener { sendIntentWithData(itemView.context, user) }
        }
    }
}