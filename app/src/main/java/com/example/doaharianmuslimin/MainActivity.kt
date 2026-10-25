package com.example.doaharianmuslimin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.doaharianmuslimin.data.AppThemeMode
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.service.AdhanReminderHelper
import com.example.doaharianmuslimin.ui.screens.HomeScreen
import com.example.doaharianmuslimin.ui.screens.SplashScreen
import com.example.doaharianmuslimin.ui.theme.DoaHarianMusliminTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission granted or denied, prayer alarms are already scheduled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request POST_NOTIFICATIONS on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize Adhan Notification Channel & Schedule Prayers in Background
        AdhanReminderHelper.createNotificationChannel(this)
        AdhanReminderHelper.scheduleAllPrayerAlarms(this)

        setContent {
            val prefs = remember { PreferencesManager(this@MainActivity) }
            var themeMode by remember { mutableStateOf(prefs.getThemeMode()) }
            var showSplash by remember { mutableStateOf(true) }

            DoaHarianMusliminTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = showSplash, label = "splashTransition") { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        } else {
                            HomeScreen(
                                currentThemeMode = themeMode,
                                onThemeModeChange = { newMode ->
                                    themeMode = newMode
                                    prefs.setThemeMode(newMode)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
