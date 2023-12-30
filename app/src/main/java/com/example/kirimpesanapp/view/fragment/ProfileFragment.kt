package com.example.kirimpesanapp.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.FragmentProfileFragmentBinding
import com.example.kirimpesanapp.view.ShowProfileActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            showProfile.setOnClickListener {
                startActivity(Intent(requireContext(), ShowProfileActivity::class.java))
            }
            helpSupport.setOnClickListener {
                Toast.makeText(requireContext(), "Help & Support \n(Comming Soon)", Toast.LENGTH_SHORT).show()
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