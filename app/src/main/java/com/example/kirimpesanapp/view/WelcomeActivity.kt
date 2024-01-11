package com.example.kirimpesanapp.view

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityWelcomeBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        auth = Firebase.auth

        setUi()
        onClick()
        settingTheme()
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, BottomNavigationActivity::class.java))
            finish()
        }
    }

    private fun settingTheme() {
        themeViewModel.getThemeSettings().observe(this) { isLightModeActive: Boolean ->
            if (isLightModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun onClick() {
        binding.apply {
            btnSignIn.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, SignInActivity::class.java))
            }
            btnSignInDark.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, SignInActivity::class.java))
            }
            btnSignUp.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, SignUpActivity::class.java))
            }
        }
    }

    private fun setUi(){
    val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    if (isNightMode) {
        binding.clWelcome.setBackgroundColor(ContextCompat.getColor(this, R.color.dark))
        binding.ivWelcome.setImageResource(R.drawable.mockupwelcomedark)
        binding.btnSignUp.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.btnSignUp.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary))
        binding.btnSignInDark.visibility = View.VISIBLE
        binding.btnSignIn.visibility = View.GONE
    } else {
        binding.clWelcome.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnSignInDark.visibility = View.GONE
        binding.btnSignIn.visibility = View.VISIBLE
        binding.ivWelcome.setImageResource(R.drawable.mockupwelcomelight)
    }
}


}