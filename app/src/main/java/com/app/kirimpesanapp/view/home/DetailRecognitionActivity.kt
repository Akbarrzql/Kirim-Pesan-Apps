package com.app.kirimpesanapp.view.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityDetailRecognitionBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.utils.setProgressDialog
import com.app.kirimpesanapp.viewmodel.auth.AuthViewModel
import com.app.kirimpesanapp.viewmodel.data_recognition.DataRecognitionViewModel
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale

class DetailRecognitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRecognitionBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var textRecognitionViewModel: DataRecognitionViewModel
    private lateinit var authViewModel: AuthViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var emailUser: String

    //text recognizer
    private var imageUri: Uri? = null
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        textRecognitionViewModel = ViewModelProvider(this)[DataRecognitionViewModel::class.java]
        emailUser = auth.currentUser?.email.toString()


        //init text recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        Log.d("nameUser", emailUser)

        initUI()
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
                Toast.makeText(this@DetailRecognitionActivity, R.string.copy, Toast.LENGTH_SHORT).show()
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


    private fun initUI() {
        val intent = intent.getStringExtra("Camera")
        checkForPermission()
        if (intent == "Camera"){
            if (checkCameraPermission()){
                pickCamera()
            } else{
                requestCameraPermission()
            }
        }else{
            if (checkStoragePermission()){
                pickGallery()
            } else{
                requestStoragePermission()
            }
        }
    }

    private fun recognizeText() {
        val dialog = setProgressDialog(this, getString(R.string.tittle_dialog), getString(R.string.please_wait))
        dialog.show()

        val image = com.google.mlkit.vision.common.InputImage.fromFilePath(this, imageUri!!)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                dialog.dismiss()
                val resultText = visionText.text
                binding.tvTitle.text = resultText
                binding.ivCamera.visibility = View.GONE

                val formattedText = extractPhoneNumber(resultText)
                binding.tvTitle.text = formattedText

                if (binding.tvTitle.text == "No phone number found"){
                    binding.cvWa.visibility = View.GONE
                }else{
                    binding.cvWa.visibility = View.VISIBLE
                    insertToDatabase(formattedText)
                }
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertToDatabase(formattedText: String) {
        val date = java.text.SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(java.util.Date())
        val imageUri = imageUri.toString()
        val dataRecognition = com.app.kirimpesanapp.source.model.DataRecognition(0, emailUser, formattedText, date, imageUri)
        Log.d("dataRecognition", dataRecognition.toString())
        textRecognitionViewModel.insertDataRecognition(dataRecognition)
    }

    private fun extractPhoneNumber(fullText: String): String {
        val patterns = listOf(
            Regex(getString(R.string.regex1)),
            Regex(getString(R.string.regex2)),
            Regex(getString(R.string.regex_3)),
            Regex(getString(R.string.regex_4))
        )

        for (pattern in patterns) {
            val matchResult = pattern.find(fullText)
            if (matchResult != null) {
                val phoneNumber = matchResult.groupValues.filterIndexed { index, _ -> index > 0 }.joinToString("")
                return addLeadingZeroIfNeeded(phoneNumber)
            }
        }

        return "No phone number found"
    }

    private fun addLeadingZeroIfNeeded(phoneNumber: String): String {
        // Cek apakah nomor telepon tidak dimulai dengan "0" dan tidak dimulai dengan kode negara
        if (!phoneNumber.startsWith("0") && !phoneNumber.startsWith("+")) {
            return "0$phoneNumber"
        }
        return phoneNumber
    }


    private fun checkForPermission(){
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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


        binding.apply {
            mtDetailHistory.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            mtDetailHistory.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete -> {
                        Toast.makeText(this@DetailRecognitionActivity, R.string.delete, Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            if (isNightMode){
                clDetailRecognition.setBackgroundColor(ContextCompat.getColor(this@DetailRecognitionActivity, R.color.dark))
                mtDetailHistory.setBackgroundColor(ContextCompat.getColor(this@DetailRecognitionActivity, R.color.dark))
                mtDetailHistory.navigationIcon?.setTint(ContextCompat.getColor(this@DetailRecognitionActivity, R.color.white))
                ivClock.setColorFilter(ContextCompat.getColor(this@DetailRecognitionActivity, R.color.grey_light))
                tvDate.setTextColor(ContextCompat.getColor(this@DetailRecognitionActivity, R.color.grey_light))
            }

            val date = java.text.SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(java.util.Date())
            tvDate.text = date
        }
    }

    private fun pickCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.ivDetailRecognition.setImageURI(imageUri)
            binding.ivCamera.visibility = View.GONE
            recognizeText()
        } else{
            Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun pickGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            imageUri = data?.data
            binding.ivDetailRecognition.setImageURI(imageUri)
            binding.ivCamera.visibility = View.GONE
            recognizeText()
        } else {
            Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean {
        val result = checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result1 = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted){
                        pickCamera()
                    } else{
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted){
                        pickGallery()
                    } else{
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }
}