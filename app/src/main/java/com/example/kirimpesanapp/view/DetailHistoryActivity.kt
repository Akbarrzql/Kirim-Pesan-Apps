package com.example.kirimpesanapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUI()

    }

    private fun setUI() {

        val isNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES


        binding.apply {
            mtDetailHistory.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            mtDetailHistory.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete -> {
                        Toast.makeText(this@DetailHistoryActivity, "Delete", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            if (isNightMode){
                clDetailHistory.setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.dark))
                mtDetailHistory.setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.dark))
                mtDetailHistory.navigationIcon?.setTint(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                mtDetailHistory.menu.getItem(0).icon?.setTint(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                ivClock.setColorFilter(ContextCompat.getColor(this@DetailHistoryActivity, R.color.grey_light))
                tvDate.setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.grey_light))
            }
        }
    }
}