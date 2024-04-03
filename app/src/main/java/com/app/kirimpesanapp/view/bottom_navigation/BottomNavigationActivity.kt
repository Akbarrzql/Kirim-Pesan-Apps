package com.app.kirimpesanapp.view.bottom_navigation

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityBottomNavigationBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.view.history.HistoryFragment
import com.app.kirimpesanapp.view.home.MenuFragment
import com.app.kirimpesanapp.view.profile.ProfileFragment
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.appdistribution.InterruptionLevel
import com.google.firebase.appdistribution.ktx.appDistribution
import com.google.firebase.ktx.Firebase

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private var selectedItemId = R.id.menu
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.appDistribution.showFeedbackNotification(
            // Text providing notice to your testers about collection and
            // processing of their feedback data
            "We'd love to hear from you!",
            // The level of interruption for the notification
            InterruptionLevel.HIGH)

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

        binding.bottomNavigation.menu.forEach {
            val view = binding.bottomNavigation.findViewById<BottomNavigationItemView>(it.itemId)
            view.setOnLongClickListener {
                TooltipCompat.setTooltipText(view, null)
                true
            }
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