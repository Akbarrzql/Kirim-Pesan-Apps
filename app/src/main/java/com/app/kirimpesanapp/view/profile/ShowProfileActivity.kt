package com.app.kirimpesanapp.view.profile

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.app.kirimpesanapp.R
import com.app.kirimpesanapp.databinding.ActivityShowProfileBinding
import com.app.kirimpesanapp.preferences.AuthPreferences
import com.app.kirimpesanapp.preferences.ThemePreferences
import com.app.kirimpesanapp.preferences.authStore
import com.app.kirimpesanapp.preferences.dataStore
import com.app.kirimpesanapp.viewmodel.auth.AuthViewModel
import com.app.kirimpesanapp.viewmodel.MainViewModel
import com.app.kirimpesanapp.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProfileBinding
    private lateinit var themeViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var authViewModel: AuthViewModel

    private var imageUri: Uri? = null
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        val authPref = AuthPreferences.getInstance(application.authStore)
        val viewModelFactory = ViewModelFactory(pref, authPref)
        themeViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        auth = Firebase.auth
        storage = Firebase.storage

        initView()
        setUI()
        updateUser()
        settingTheme()
        checkForPermission()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            ivProfile.setOnClickListener {
                showInputImageDialog()
            }
        }
    }

    private fun checkForPermission(){
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun updateUser() {
        val user = Firebase.auth.currentUser

        binding.apply {
            btnUpdate.setOnClickListener {
                if (etUsername.text.toString().isEmpty()){
                    etUsername.error = R.string.error_message.toString()
                    etUsername.requestFocus()
                    return@setOnClickListener
                }
                if (etEmail.text.toString().isEmpty()){
                    etEmail.error = R.string.error_message.toString()
                    etEmail.requestFocus()
                    return@setOnClickListener
                }
                val profileUpdates = userProfileChangeRequest {
                    displayName = etUsername.text.toString()
                }
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        authViewModel.saveUserName(etUsername.text.toString())
                        Toast.makeText(this@ShowProfileActivity, R.string.profile_update, Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }else{
                        Toast.makeText(this@ShowProfileActivity, R.string.profile_update_failed, Toast.LENGTH_SHORT).show()
                    }
                }

                if (imageUri != null){
                    val ref = storage.reference.child("profile/${auth.currentUser?.uid}")
                    ref.putFile(imageUri!!).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setPhotoUri(it)
                                .build()
                            auth.currentUser?.updateProfile(profileUpdates)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this@ShowProfileActivity, R.string.profile_update_failed, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    //kondisi jika user tidak memilih gambar baru maka akan tetap menggunakan gambar lama yang sudah ada
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setPhotoUri(auth.currentUser?.photoUrl)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                }
            }
        }
    }

    private fun initView() {
        binding.apply {
            if (auth.currentUser?.displayName == null || auth.currentUser?.displayName == ""){
                etUsername.setText(authViewModel.getUserName().observe(this@ShowProfileActivity) { name: String? ->
                    etUsername.setText(name)
                }.toString())
            }else{
                etUsername.setText(auth.currentUser?.displayName)
            }
            etEmail.setText(auth.currentUser?.email)
            etEmail
            if (auth.currentUser?.photoUrl != null){
                Glide.with(this@ShowProfileActivity).load(auth.currentUser?.photoUrl).into(ivProfile)
                icProfile.visibility = View.GONE
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
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.apply {

            mtShowProfile.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (isNightMode){
                clShowProfile.setBackgroundColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.dark))
                mtShowProfile.setBackgroundColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.dark))
                mtShowProfile.navigationIcon?.setTint(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.etUsername.setTextColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.etEmail.setTextColor(ContextCompat.getColor(this@ShowProfileActivity, R.color.white))
                binding.tiEmail.setBackgroundResource(R.drawable.rounded_edittext_dark)
                binding.tiUsername.setBackgroundResource(R.drawable.rounded_edittext_dark)
            }
        }
    }

    private fun showInputImageDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.choose_image)
            .setItems(
                arrayOf("Camera", "Gallery")
            ) { _, which ->
                if (which == 0){
                    if (checkCameraPermission()){
                        pickCamera()
                    } else{
                        requestCameraPermission()
                    }
                } else{
                    if (checkStoragePermission()){
                        pickGallery()
                    } else{
                        requestStoragePermission()
                    }
                }
            }
            .show()

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
            binding.ivProfile.setImageURI(imageUri)
        } else{
            Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show()
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
            binding.ivProfile.setImageURI(imageUri)
        } else{
            Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show()
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