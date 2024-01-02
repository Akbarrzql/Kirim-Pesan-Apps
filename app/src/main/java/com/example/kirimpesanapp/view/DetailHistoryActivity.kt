package com.example.kirimpesanapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityDetailHistoryBinding
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
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

        val isNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES


        binding.apply {
            mtDetailHistory.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            mtDetailHistory.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete -> {
                        Toast.makeText(this@DetailHistoryActivity, "Delete", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            if (isNightMode){
                clDetailHistory.setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.dark))
                mtDetailHistory.setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.dark))
                mtDetailHistory.navigationIcon?.setTint(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                mtDetailHistory.menu.getItem(0).icon?.setTint(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                ivClock.setColorFilter(ContextCompat.getColor(this@DetailHistoryActivity, R.color.grey_light))
                tvDate.setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.grey_light))
            }
        }
    }
}