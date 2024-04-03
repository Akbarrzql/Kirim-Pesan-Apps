package com.app.kirimpesanapp.view.profile.settings

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityHelpSupportBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpSupportBinding
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setUi()
        onClick()
        settingTheme()
    }

    private fun onClick() {
        binding.apply {
            mtHelpSupport.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            changePassword.setOnClickListener {
                startActivity(Intent(this@HelpSupportActivity, ChangePasswordActivity::class.java))
            }
            bugReport.setOnClickListener {
                startActivity(Intent(this@HelpSupportActivity, BugReportActivity::class.java))
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

        if (isNightMode){
            binding.apply {
                toolbar.setBackgroundResource(R.color.dark)
                clHelpSupport.setBackgroundResource(R.color.dark)
                mtHelpSupport.setBackgroundColor(ContextCompat.getColor(this@HelpSupportActivity, R.color.dark))
                mtHelpSupport.navigationIcon?.setTint(ContextCompat.getColor(this@HelpSupportActivity, R.color.white))
                ivChangePassword.setColorFilter(androidx.core.content.ContextCompat.getColor(this@HelpSupportActivity, R.color.white))
                ivBugReport.setColorFilter(androidx.core.content.ContextCompat.getColor(this@HelpSupportActivity, R.color.white))
            }
        }
    }
}