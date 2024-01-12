package com.example.kirimpesanapp.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityBugReportBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics

class BugReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBugReportBinding
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBugReportBinding.inflate(layoutInflater)
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
        val bugReport = binding.etBugReport.text.toString()
        binding.apply {
            mtBugReport.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnBugReport.setOnClickListener {
                Toast.makeText(this@BugReportActivity, "Thank you for your bug report", Toast.LENGTH_SHORT).show()
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.recordException(Throwable(bugReport))
                onBackPressedDispatcher.onBackPressed()
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
                clBugReport.setBackgroundColor(ContextCompat.getColor(this@BugReportActivity,
                    R.color.dark
                ))
                mtBugReport.setBackgroundColor(ContextCompat.getColor(this@BugReportActivity,
                    R.color.dark
                ))
                mtBugReport.navigationIcon?.setTint(ContextCompat.getColor(this@BugReportActivity,
                    R.color.white
                ))
            }
        }
    }
}