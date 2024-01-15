package com.app.kirimpesanapp.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.BuildConfig
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivitySignUpBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.AuthViewModel
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]


        auth = Firebase.auth

        onClick()
        setUi()
        settingTheme()
        googleSignIn()
    }

    private fun settingTheme() {
        mainViewModel.getThemeSettings().observe(this) { isLightModeActive: Boolean ->
            if (isLightModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.web_client_id)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignUpGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        }

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(
                Intent(
                    this@SignUpActivity, BottomNavigationActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }
    }

    private fun onClick() {
        binding.apply {
            ivBackSignUo.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSignUp.setOnClickListener {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val user = Firebase.auth.currentUser

        val email = binding.etEmailSignUp.text.toString()
        val password = binding.etPasswordSignUp.text.toString()

        when {
            email.isEmpty() -> {
                binding.etEmailSignUp.error = "Email is required"
                binding.etEmailSignUp.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPasswordSignUp.error = "Password is required"
                binding.etPasswordSignUp.requestFocus()
            }
            !email.contains("@") -> {
                binding.etEmailSignUp.error = "Email must be valid"
                binding.etEmailSignUp.requestFocus()
            }
            password.length < 6 -> {
                binding.etPasswordSignUp.error = "Password must be at least 6 characters"
                binding.etPasswordSignUp.requestFocus()
            }
            else -> {
                binding.loadingView.root.visibility = android.view.View.VISIBLE
            }
        }

        val profileUpdates = userProfileChangeRequest {
            displayName = binding.etUsername.text.toString()
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d("update", "Update username")
            }
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.loadingView.root.visibility = android.view.View.GONE
                    val username = binding.etUsername.text.toString()
                    authViewModel.saveUserName(username)
                    Toast.makeText(
                        this@SignUpActivity,
                        R.string.sign_in_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@SignUpActivity, BottomNavigationActivity::class.java))
                    finish()
                } else {
                    binding.loadingView.root.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@SignUpActivity,
                        R.string.sign_in_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            if (signInAccountTask.isSuccessful) {
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                // Check condition
                                if (task.isSuccessful) {
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity, BottomNavigationActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    finish()
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        R.string.sign_in_success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        R.string.sign_in_failed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
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