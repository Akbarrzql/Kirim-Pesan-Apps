package com.app.kirimpesanapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.kirimpesanapp.databinding.ActivityMainBinding
import com.google.firebase.appdistribution.InterruptionLevel
import com.google.firebase.appdistribution.ktx.appDistribution
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.appDistribution.showFeedbackNotification(
            // Text providing notice to your testers about collection and
            // processing of their feedback data
            "We'd love to hear from you!",
            // The level of interruption for the notification
            InterruptionLevel.HIGH,
        )
    }
}