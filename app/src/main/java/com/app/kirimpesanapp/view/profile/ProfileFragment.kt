package com.app.kirimpesanapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.FragmentProfileFragmentBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.view.profile.settings.HelpSupportActivity
import com.app.kirimpesanapp.view.auth.WelcomeActivity
import com.app.kirimpesanapp.viewmodel.auth.AuthViewModel
import com.app.kirimpesanapp.viewmodel.theme.ThemeViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.appdistribution.InterruptionLevel
import com.google.firebase.appdistribution.ktx.appDistribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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

        Firebase.appDistribution.showFeedbackNotification(
            // Text providing notice to your testers about collection and
            // processing of their feedback data
            "We'd love to hear from you!",
            // The level of interruption for the notification
            InterruptionLevel.HIGH)

        setUI()
        initUI()
        onClick()
        themePreferences()
//        feedbackTester()
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

    override fun onResume() {
        super.onResume()
        initUI()
    }
}