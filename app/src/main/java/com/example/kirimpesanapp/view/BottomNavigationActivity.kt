package com.example.kirimpesanapp.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityBottomNavigationBinding
import com.example.kirimpesanapp.view.fragment.HistoryFragment
import com.example.kirimpesanapp.view.fragment.MenuFragment
import com.example.kirimpesanapp.view.fragment.ProfileFragment

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentFragment(MenuFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu->setCurrentFragment(MenuFragment())
                R.id.history->setCurrentFragment(HistoryFragment())
                R.id.profile->setCurrentFragment(ProfileFragment())
            }
            true
        }

        setUI()
    }

    private fun setUI() {
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (!isNightMode) {
            binding.bottomNavigation.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }


    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_container,fragment)
            commit()
        }
}