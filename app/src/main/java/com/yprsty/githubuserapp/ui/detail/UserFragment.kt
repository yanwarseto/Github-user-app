package com.yprsty.githubuserapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.yprsty.githubuserapp.R
import com.yprsty.githubuserapp.data.Result
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.databinding.FragmentUserBinding
import com.yprsty.githubuserapp.model.UserViewModel
import com.yprsty.githubuserapp.model.ViewModelFactory
import com.yprsty.githubuserapp.ui.adapter.UserAdapter
import com.yprsty.githubuserapp.utils.Helper.Companion.setupLayoutManager
import com.yprsty.githubuserapp.utils.Helper.Companion.showLoading
import com.yprsty.githubuserapp.utils.Helper.Companion.showToast

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val owner = requireActivity()
        val thisContext = requireContext()
        val tabSectionPosition = arguments?.getInt(ARG_SECTION_NUMBER, 0)

        setupLayoutManager(binding.rvUserFollowers, thisContext)
        setupLayoutManager(binding.rvUserFollowing, thisContext)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(thisContext)
        val mainViewModel = ViewModelProvider(owner, factory)[UserViewModel::class.java]

        val userAdapter = UserAdapter { user -> // Trigger for add or delete favorite user
            if (user.isFavorite) {
                mainViewModel.deleteUsers(user)
                showToast(thisContext, getString(R.string.info_remove))
            } else {
                mainViewModel.saveUsers(user)
                showToast(thisContext, getString(R.string.info_add_favorite))
            }
        }

        // Display one of the recycler views based on the layout tab position
        val isTabFollowers = tabSectionPosition == FIRST_SECTION_POSITION
        if (isTabFollowers) {
            mainViewModel.showUserFollowers().observe(owner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> showLoading(binding.progressBar, true)
                        is Result.Success -> showFollow(result.data, userAdapter)
                        is Result.Error -> {} // Handle on DetailActivity
                    }
                }
            }

            showRecyclerView(binding.rvUserFollowers)
            binding.rvUserFollowers.adapter = userAdapter
        } else {
            mainViewModel.showUserFollowing().observe(owner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> showLoading(binding.progressBar, true)
                        is Result.Success -> showFollow(result.data, userAdapter)
                        is Result.Error -> {} // Handle on DetailActivity
                    }
                }
            }

            showRecyclerView(binding.rvUserFollowing)
            binding.rvUserFollowing.adapter = userAdapter
        }
    }

    // Add all data to adapter
    private fun showFollow(data: List<UsersEntity>, adapter: UserAdapter) {
        showLoading(binding.progressBar, false)
        if (data.isNotEmpty()) {
            adapter.submitList(data)
        } else {
            adapter.submitList(emptyList())
        }
    }

    private fun showRecyclerView(rv: RecyclerView) {
        rv.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val FIRST_SECTION_POSITION = 0
    }
}