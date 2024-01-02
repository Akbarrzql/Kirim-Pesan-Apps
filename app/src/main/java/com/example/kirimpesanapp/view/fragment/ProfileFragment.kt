package com.example.kirimpesanapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.FragmentProfileFragmentBinding
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.view.ShowProfileActivity
import com.example.kirimpesanapp.view.WelcomeActivity
import com.example.kirimpesanapp.viewmodel.ThemeViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        onClick()
        themePreferences()
    }

    private fun themePreferences() {
        val pref = ThemePreferences.getInstance(requireContext().dataStore)
        val mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[ThemeViewModel::class.java]
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchDarkMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchDarkMode.isChecked = false
            }
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun onClick() {
        binding.apply {
            showProfile.setOnClickListener {
                startActivity(Intent(requireContext(), ShowProfileActivity::class.java))
            }
            helpSupport.setOnClickListener {
                Toast.makeText(requireContext(), "Help & Support \n(Comming Soon)", Toast.LENGTH_SHORT).show()
            }
            logout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logout)
                    .setMessage(R.string.logout_message)
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.logout) { _, _ ->
                        startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                        requireActivity().finish()
                    }
                    .show()
            }
            language.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
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
                ivLogoutArrow.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red_light))
                ivLogout.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red_hint))
            }
        }
    }

}