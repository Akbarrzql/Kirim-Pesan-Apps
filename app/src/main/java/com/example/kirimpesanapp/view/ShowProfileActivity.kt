package com.example.kirimpesanapp.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityShowProfileBinding
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProfileBinding
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val viewModelFactory = ViewModelFactory(pref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setUI()
        settingTheme()
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

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {

            mtShowProfile.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (isNightMode){
                clShowProfile.setBackgroundColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.dark))
                mtShowProfile.setBackgroundColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.dark))
                mtShowProfile.navigationIcon?.setTint(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.etUsername.setTextColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.etEmail.setTextColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.tiEmail.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.tiUsername.setBackgroundResource(R.drawable.rounded_edittext_dark)
            }
        }
    }


}