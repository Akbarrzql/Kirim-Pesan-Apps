package com.app.kirimpesanapp.view.profile.settings

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityBugReportBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
        val db = Firebase.firestore
        binding.apply {
            mtBugReport.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnBugReport.setOnClickListener {
                val data = hashMapOf(
                    "bugReport" to etBugReport.text.toString()
                )
                db.collection("bugReport")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this@BugReportActivity, "Thank you for your bug report", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@BugReportActivity, "Failed to send bug report", Toast.LENGTH_SHORT).show()
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