package com.abadzheva.services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abadzheva.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        askPermission()
        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 25))
        }
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
        }
    }

    private fun askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1,
                )
            }
        }
    }
}
