package com.app.kirimpesanapp.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityChangePasswordBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        auth = Firebase.auth

        onClick()
        setUi()
        initUI()
        settingTheme()
    }

    private fun initUI() {
        val email = auth.currentUser?.email.toString()
        binding.etEmail.setText(email)
    }

    private fun onClick() {
        val email = auth.currentUser?.email.toString()
        auth.setLanguageCode("id")

        binding.btnUpdate.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.email_sent, Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
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

    private fun setUi() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {

            mtChangePassword.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (isNightMode){
                clChangePassword.setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity, R.color.dark))
                mtChangePassword.setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity, R.color.dark))
                mtChangePassword.navigationIcon?.setTint(ContextCompat.getColor(this@ChangePasswordActivity, R.color.white))
                binding.etEmail.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity, R.color.white))
                binding.tiEmail.setBackgroundResource(R.drawable.rounded_edittext_dark)
            }
        }
    }
}