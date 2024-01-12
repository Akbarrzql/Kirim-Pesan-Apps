package com.example.kirimpesanapp.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kirimpesanapp.databinding.FragmentHistoryBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.view.adapter.HistoryAdapter
import com.example.kirimpesanapp.viewmodel.DataRecognitionViewModel
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private lateinit var themeViewModel: MainViewModel
    private lateinit var dataRecognitionViewModel: DataRecognitionViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = ThemePreferences.getInstance(requireContext().dataStore)
        val authPref = AuthPreferences.getInstance(requireContext().authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        auth = Firebase.auth

        setUI()
        setRecyclerView()
        settingTheme()
    }

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isNightMode){
            binding.clHistory.setBackgroundResource(com.example.kirimpesanapp.R.color.dark)
        }else {
            binding.clHistory.setBackgroundResource(com.example.kirimpesanapp.R.color.white)
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

    private fun setRecyclerView() {
        val recyclerView = binding.rvHistory
        adapter = HistoryAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupViewModel()
    }

    private fun setupViewModel() {
        dataRecognitionViewModel = ViewModelProvider(this)[DataRecognitionViewModel::class.java]
        dataRecognitionViewModel.readAllData.observe(viewLifecycleOwner) { dataRecognition ->
            val data = dataRecognition.filter { it.emailUser == auth.currentUser?.email }
            Log.d("data name", data.toString())
            if (dataRecognition.isNotEmpty() && data.isNotEmpty()) {
                adapter.setData(data)
                binding.llEmptyHistory.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            } else {
                binding.llEmptyHistory.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            }
        }
    }

}