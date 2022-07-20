package com.example.foodemption.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.foodemption.R

class SplashScreenActivity : AppCompatActivity() {
    private val times = 1000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, times)
    }
}