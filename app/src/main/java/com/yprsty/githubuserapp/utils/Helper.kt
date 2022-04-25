package com.yprsty.githubuserapp.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.ui.detail.DetailUserActivity

/**
 * This class for helping all Activity/Fragment
 * to use function often called
 *
 * and implement DRY pattern
 * */
class Helper {
    companion object {

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showLoading(view: View, isLoading: Boolean) {
            view.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Move activity to DetailUserActivity with data
        fun sendIntentWithData(from: Context, user: UsersEntity) {
            val toDetailUser = Intent(from, DetailUserActivity::class.java)
            toDetailUser.putExtra(DetailUserActivity.EXTRA_ENTITY, user)
            from.startActivity(toDetailUser)
        }

        // Extension function for circle and load image
        fun ImageView.loadImage(context: Context, source: Any) {
            Glide.with(context)
                .load(source)
                .circleCrop()
                .into(this)
        }

        fun setupLayoutManager(recyclerView: RecyclerView, context: Context) {
            recyclerView.layoutManager = if (isOrientationLandscape(context))
                GridLayoutManager(context, 2)
            else
                LinearLayoutManager(context)
        }

        private fun isOrientationLandscape(context: Context): Boolean =
            context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}