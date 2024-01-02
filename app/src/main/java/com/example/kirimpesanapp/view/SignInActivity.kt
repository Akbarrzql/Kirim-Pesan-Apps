package com.example.kirimpesanapp.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivitySignInBinding
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var themeViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val viewModelFactory = ViewModelFactory(pref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setUi()
        onClick()
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

    private fun onClick() {
        binding.apply {
            ivBackSignIn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSignIn.setOnClickListener {
               Toast.makeText(this@SignInActivity, "Sign In", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignInActivity, BottomNavigationActivity::class.java))
            }
            btnSignInGoogle.setOnClickListener {
                Toast.makeText(this@SignInActivity, "Sign In Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUi() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {
            if(isNightMode){
                binding.etEmailSignIn.setTextColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
                binding.etPasswordSignIn.setTextColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
                binding.tiEmail.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.tiPassword.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.ivBackSignIn.setImageResource(R.drawable.baseline_arrow_back_white_24)
                binding.clSignIn.setBackgroundColor(ContextCompat.getColor(this@SignInActivity, R.color.dark))
                binding.btnSignIn.setTextColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
                binding.btnSignIn.setBackgroundColor(ContextCompat.getColor(this@SignInActivity, R.color.secondary))
                binding.tvSignInGoogle.setTextColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
                binding.btnSignInGoogle.setBackgroundResource(R.drawable.bg_button_stroke_dark)
            }else{
                binding.ivBackSignIn.setImageResource(R.drawable.baseline_arrow_back_24)
                binding.clSignIn.setBackgroundColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
            }
        }
    }
}