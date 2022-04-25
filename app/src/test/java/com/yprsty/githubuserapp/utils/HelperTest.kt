package com.yprsty.githubuserapp.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build.VERSION_CODES.Q
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Q])
class HelperTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun showLoadingTest_isViewVisible() {
        val view = View(context)
        val isLoading = true
        view.visibility = if (isLoading) View.VISIBLE else View.GONE
        Assert.assertEquals("View should be Visible", View.VISIBLE, view.visibility)
    }

    @Test
    fun showLoadingTest_isViewGone() {
        val view = View(context)
        val isLoading = false
        view.visibility = if (isLoading) View.VISIBLE else View.GONE
        Assert.assertEquals("View should be Invisible", View.GONE, view.visibility)
    }

    @Test
    fun setupLayoutManagerTest_isLinearLayout() {
        val rv = RecyclerView(context)
        val orientationPortrait = Configuration.ORIENTATION_PORTRAIT
        context.resources.configuration.orientation = orientationPortrait

        rv.layoutManager = if (context.resources.configuration.orientation == orientationPortrait) {
            LinearLayoutManager(context)
        } else {
            GridLayoutManager(context, 2)
        }

        val isLinearLayout = rv.layoutManager is LinearLayoutManager
        Assert.assertTrue("Layout should be LinearLayout", isLinearLayout)
    }

    @Test
    fun setupLayoutManagerTest_isGridLayout() {
        val rv = RecyclerView(context)
        val orientationLandscape = Configuration.ORIENTATION_LANDSCAPE
        context.resources.configuration.orientation = orientationLandscape

        rv.layoutManager =
            if (context.resources.configuration.orientation == orientationLandscape) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }

        val isGridLayout = rv.layoutManager is GridLayoutManager
        Assert.assertTrue("Layout should be GridLayout", isGridLayout)
    }
}