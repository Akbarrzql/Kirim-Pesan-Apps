package com.app.kirimpesanapp.view.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.FragmentMenuBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = ThemePreferences.getInstance(requireContext().dataStore)
        val authPref = AuthPreferences.getInstance(requireContext().authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        throw RuntimeException("Test Crash")

        setUI()
        settingTheme()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            flCamera.setOnClickListener {
                val intent = Intent(requireContext(), DetailRecognitionActivity::class.java)
                intent.putExtra("Camera", "Camera")
                startActivity(intent)
            }
            flGalery.setOnClickListener {
                val intent = Intent(requireContext(), DetailRecognitionActivity::class.java)
                intent.putExtra("Galery", "Galery")
                startActivity(intent)
            }
        }
    }

    private fun settingTheme() {
        themeViewModel.getThemeSettings().observe(viewLifecycleOwner) { isLightModeActive: Boolean ->
            if (isLightModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (isNightMode){
            binding.apply {
                clMenu.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark))
                ivInfo.setImageResource(R.drawable.head_menu_dark)
                flCamera.setBackgroundResource(R.drawable.bg_cardview_dark)
                flGalery.setBackgroundResource(R.drawable.bg_cardview_dark)
            }
        }
    }
}