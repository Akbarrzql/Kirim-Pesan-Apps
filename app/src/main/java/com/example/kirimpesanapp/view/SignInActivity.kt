package com.example.kirimpesanapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUi()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            ivBackSignIn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSignIn.setOnClickListener {
               Toast.makeText(this@SignInActivity, "Sign In", Toast.LENGTH_SHORT).show()
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