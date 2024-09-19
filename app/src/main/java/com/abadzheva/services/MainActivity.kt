package com.abadzheva.services

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abadzheva.services.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var notificationManager: NotificationManager

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                showNotification()
                Log.e("MainActivity", "requestPermissionLauncher: isGranted")
            } else {
                Log.e("MainActivity", "requestPermissionLauncher: isNOTGranted")
                showFeatureNeedPermissionDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 25)) // запускаем сервис
        }

        binding.foregroundService.setOnClickListener {
            setupNotification()
            checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        val notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID)

        when {
            NotificationManagerCompat.from(this).areNotificationsEnabled() -> {
                if (notificationChannel != null && notificationChannel.importance == NotificationManager.IMPORTANCE_NONE) {
                    Log.e("MainActivity", "onCreate: Notification channel is disabled by the user.")
                    showPermissionExplanation()
                } else {
                    Log.e("MainActivity", "onCreate: PERMISSION GRANTED")
                    showNotification()
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Log.e("MainActivity", "onCreate: PERMISSION Snackbar.make")
                showPermissionExplanation()
            }

            else -> {
                Log.e("MainActivity", "onCreate: PERMISSION ELSE")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                } else {
                    showFeatureNeedPermissionDialog()
                }
            }
        }
    }

    private fun showPermissionExplanation() {
        Snackbar
            .make(binding.root, "Notification blocked", Snackbar.LENGTH_LONG)
            .setAction("Settings") {
                openAppNotificationSettings()
            }.show()
    }

    private fun showFeatureNeedPermissionDialog() {
        AlertDialog
            .Builder(this)
            .setTitle("Title")
            .setMessage(
                "Отсутствует разрешение на показ уведомлений для " +
                    "${
                        packageName.substringAfterLast(".").replaceFirstChar { it.uppercase() }
                    }.\n" +
                    "Вам не доступна функция ПОКАЗА ВАЖНЫХ УВЕДОМЛЕНИЙ.\n" +
                    "Нажмите НАСТРОЙКИ, чтобы включить разрешение на показ уведомлений",
            ).setPositiveButton("Настройки") { _, buttonId ->
                Log.w("AlertDialog", buttonId.toString() + "was clicked")
                openAppNotificationSettings()
            }.setNegativeButton("Отмена", null)
            .setCancelable(false)
            .show()
    }

    private fun setupNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun showNotification() {
        val notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        notificationManager.notify(1, notification)
    }

    private fun openAppNotificationSettings() {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}
