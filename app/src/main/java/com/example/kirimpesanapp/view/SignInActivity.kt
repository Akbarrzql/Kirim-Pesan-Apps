package com.example.kirimpesanapp.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.BuildConfig
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivitySignInBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.AuthViewModel
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]

        auth = Firebase.auth

        setUi()
        onClick()
        settingTheme()
        googleSignIn()
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.web_client_id)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignInGoogle.setOnClickListener {
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
                    this@SignInActivity, BottomNavigationActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
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

    private fun onClick() {
        binding.apply {
            ivBackSignIn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSignIn.setOnClickListener {
                loginUsers()
            }
            btnSignInGoogle.setOnClickListener {
                Toast.makeText(this@SignInActivity, "Sign In Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUsers() {
        val email = binding.etEmailSignIn.text.toString()
        val password = binding.etPasswordSignIn.text.toString()

        when {
            email.isEmpty() -> {
                binding.etEmailSignIn.error = "Email is required"
                binding.etEmailSignIn.requestFocus()
                return
            }
            password.isEmpty() -> {
                binding.etPasswordSignIn.error = "Password is required"
                binding.etPasswordSignIn.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.etPasswordSignIn.error = "Password must be at least 6 characters"
                binding.etPasswordSignIn.requestFocus()
                return
            }
            !email.contains("@") -> {
                binding.etEmailSignIn.error = "Email must be valid"
                binding.etEmailSignIn.requestFocus()
                return
            }
            else -> {
                binding.loadingView.root.visibility = android.view.View.VISIBLE
            }
        }


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.loadingView.root.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@SignInActivity, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignInActivity, BottomNavigationActivity::class.java))
                    finish()
                } else {
                    binding.loadingView.root.visibility = android.view.View.GONE
                    Toast.makeText(this@SignInActivity, R.string.sign_in_failed, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setUi() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {
            if(isNightMode){
                binding.loadingView.clLoadingView.setBackgroundColor(ContextCompat.getColor(this@SignInActivity, R.color.dark))
                binding.loadingView.tvLoading.setTextColor(ContextCompat.getColor(this@SignInActivity, R.color.white))
                binding.loadingView.pbLoading.indeterminateTintList = ContextCompat.getColorStateList(this@SignInActivity, R.color.white)
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
                                            this@SignInActivity, BottomNavigationActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    Toast.makeText(
                                        this@SignInActivity,
                                        "Sign In Success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@SignInActivity,
                                        "Sign In Failed",
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
}