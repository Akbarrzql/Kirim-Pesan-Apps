package com.example.kirimpesanapp.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.data.model.DataRecognition
import com.example.kirimpesanapp.databinding.ActivityDetailHistoryBinding
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences
import com.example.kirimpesanapp.preferences.authStore
import com.example.kirimpesanapp.preferences.dataStore
import com.example.kirimpesanapp.viewmodel.DataRecognitionViewModel
import com.example.kirimpesanapp.viewmodel.MainViewModel
import com.example.kirimpesanapp.viewmodel.ViewModelFactory

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var dataRecognitionViewModel: DataRecognitionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        dataRecognitionViewModel = ViewModelProvider(this)[DataRecognitionViewModel::class.java]

        setUI()
        settingTheme()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            cvCopy.setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", binding.tvTitle.text.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@DetailHistoryActivity, R.string.copy, Toast.LENGTH_SHORT).show()
            }
            cvWa.setOnClickListener {
                val phoneNumber = binding.tvTitle.text.toString()
                val phoneNumberWithoutZero = phoneNumber.substring(1)
                val phoneNumberWithCountryCode = "+62$phoneNumberWithoutZero"
                if (phoneNumber.startsWith("+62")){
                    val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }else{
                    val url = "https://api.whatsapp.com/send?phone=$phoneNumberWithCountryCode"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
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

    private fun setUI() {

        val isNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val id = intent.getIntExtra("id", 0)
        val nameUser = intent.getStringExtra("emailUser")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val dateRecognition = intent.getStringExtra("dateRecognition")
        val image = intent.getStringExtra("image")

        //convert image to bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(image))

        binding.apply {
            mtDetailHistory.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            mtDetailHistory.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete -> {
                        val dataRecognition = DataRecognition(id, nameUser.toString() ,phoneNumber!!, dateRecognition!!, image!!)
                        dataRecognitionViewModel.deleteShortcut(dataRecognition)
                        Toast.makeText(this@DetailHistoryActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    else -> false
                }
            }
            tvTitle.text = phoneNumber
            tvDate.text = dateRecognition
            ivDetailHistory.setImageBitmap(bitmap)
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