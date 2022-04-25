package com.yprsty.githubuserapp.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yprsty.githubuserapp.ui.detail.UserFragment

class OneFragmentPageAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = UserFragment()
        fragment.arguments = Bundle().apply {
            putInt(UserFragment.ARG_SECTION_NUMBER, position)
        }
        return fragment
    }
}