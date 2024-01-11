package com.example.kirimpesanapp.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityBottomNavigationBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.view.fragment.HistoryFragment
import com.example.kirimpesanapp.view.fragment.MenuFragment
import com.example.kirimpesanapp.view.fragment.ProfileFragment
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ThemeViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private var selectedItemId = R.id.menu
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.menu)
        }

        setCurrentFragment(selectedItemId)

        binding.bottomNavigation.setOnItemSelectedListener {
            selectedItemId = it.itemId
            setCurrentFragment(it.itemId)
            true
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedItemId", selectedItemId)
    }

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (!isNightMode) {
            binding.bottomNavigation.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun setCurrentFragment(itemId: Int) {
        val fragment = when(itemId) {
            R.id.menu -> MenuFragment()
            R.id.history -> HistoryFragment()
            R.id.profile -> ProfileFragment()
            else -> MenuFragment()
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_container, fragment)
            commit()
        }
    }
}