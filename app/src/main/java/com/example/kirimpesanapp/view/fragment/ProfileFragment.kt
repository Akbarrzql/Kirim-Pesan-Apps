package com.example.kirimpesanapp.view.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.FragmentProfileFragmentBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.view.HelpSupportActivity
import com.example.kirimpesanapp.view.ShowProfileActivity
import com.example.kirimpesanapp.view.WelcomeActivity
import com.example.kirimpesanapp.viewmodel.AuthViewModel
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ThemeViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = ThemePreferences.getInstance(requireContext().dataStore)
        val authPref = AuthPreferences.getInstance(requireContext().authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[ThemeViewModel::class.java]
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        auth = Firebase.auth

        setUI()
        initUI()
        onClick()
        themePreferences()
    }

    private fun initUI() {
        binding.apply {

            if (auth.currentUser?.displayName == null || auth.currentUser?.displayName == ""){
                tvName.text = authViewModel.getUserName().observe(viewLifecycleOwner) { name: String? ->
                    tvName.text = name
                }.toString()
            }else{
                tvName.text = auth.currentUser?.displayName
            }
            tvEmail.text = auth.currentUser?.email

            if (auth.currentUser?.photoUrl != null){
                Glide.with(requireContext()).load(auth.currentUser?.photoUrl).into(ivProfile)
                icCamera.visibility = View.GONE
            }
        }
    }

    private fun themePreferences() {
        themeViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchDarkMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchDarkMode.isChecked = false
            }
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            themeViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun onClick() {
        binding.apply {
            showProfile.setOnClickListener {
                startActivity(Intent(requireContext(), ShowProfileActivity::class.java))
            }
            helpSupport.setOnClickListener {
                startActivity(Intent(requireContext(), HelpSupportActivity::class.java))
            }
            logout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logout)
                    .setMessage(R.string.logout_message)
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.logout) { _, _ ->
                        auth.signOut()
                        startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                        requireActivity().finish()
                    }
                    .show()
            }
            language.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            swiperefresh.setOnRefreshListener {
                swiperefresh.isRefreshing = false
                tvName.text = (auth.currentUser?.displayName ?: authViewModel.getUserName().observe(viewLifecycleOwner){ user ->
                    tvName.text = user
                }).toString()
                tvEmail.text = auth.currentUser?.email

                if (auth.currentUser?.photoUrl != null){
                    Glide.with(requireContext()).load(auth.currentUser?.photoUrl).into(ivProfile)
                    icCamera.visibility = View.GONE
                }
            }
        }
    }

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isNightMode){
            binding.apply {
                clProfile.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dark))
                ivShowProfile.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                ivHelpSupport.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                ivLanguageArrow.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                ivLogoutArrow.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red_light))
                ivLogout.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red_hint))
            }
        }
    }
}