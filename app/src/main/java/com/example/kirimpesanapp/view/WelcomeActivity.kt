package com.example.kirimpesanapp.view

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUi()
        onClick()
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