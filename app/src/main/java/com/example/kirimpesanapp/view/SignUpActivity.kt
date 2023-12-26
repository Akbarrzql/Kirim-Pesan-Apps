package com.example.kirimpesanapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
        setUi()
    }

    private fun onClick() {
        binding.apply {
            ivBackSignUo.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSignUp.setOnClickListener {
                Toast.makeText(this@SignUpActivity, "Sign Up", Toast.LENGTH_SHORT).show()
            }
            btnSignUpGoogle.setOnClickListener {
                Toast.makeText(this@SignUpActivity, "Sign Up Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUi() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {
            if(isNightMode){
                binding.etUsername.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                binding.etEmailSignUp.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                binding.etPasswordSignUp.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                binding.tiUsername.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.tiEmail.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.tiPassword.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.ivBackSignUo.setImageResource(R.drawable.baseline_arrow_back_white_24)
                binding.clSignUp.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.dark))
                binding.btnSignUp.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                binding.btnSignUp.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.secondary))
                binding.tvSignInGoogle.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                binding.btnSignUpGoogle.setBackgroundResource(R.drawable.bg_button_stroke_dark)
            }else{
                binding.ivBackSignUo.setImageResource(R.drawable.baseline_arrow_back_24)
                binding.clSignUp.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
            }
        }
    }
}