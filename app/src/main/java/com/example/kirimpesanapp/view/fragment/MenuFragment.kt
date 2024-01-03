package com.example.kirimpesanapp.view.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.FragmentMenuBinding
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.view.DetailRecognitionActivity
import com.example.kirimpesanapp.view.ShowProfileActivity
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val viewModelFactory = ViewModelFactory(pref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

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