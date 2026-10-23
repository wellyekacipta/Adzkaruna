package com.example.doaharianmuslimin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.doaharianmuslimin.data.AppThemeMode
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.service.AdhanReminderHelper
import com.example.doaharianmuslimin.ui.screens.HomeScreen
import com.example.doaharianmuslimin.ui.screens.SplashScreen
import com.example.doaharianmuslimin.ui.theme.DoaHarianMusliminTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
