package com.yprsty.githubuserapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yprsty.githubuserapp.ui.main.MainActivity

class SplashScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val intentToMain = Intent(this, MainActivity::class.java)
        startActivity(intentToMain)
        finish()
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }
}