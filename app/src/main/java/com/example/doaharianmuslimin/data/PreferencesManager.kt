package com.example.doaharianmuslimin.data

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode(val title: String) {
    LIGHT("Mode Terang"),
    DARK("Mode Gelap"),
    SYSTEM("Ikuti Sistem")
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("doa_muslimin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_doa_ids"
        private const val KEY_ARABIC_FONT_SIZE = "arabic_font_size"
        private const val KEY_SHOW_LATIN = "show_latin"
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_ADHAN_ENABLED = "key_adhan_enabled"
        private const val KEY_ADHAN_VOICE = "key_adhan_voice"
        private const val KEY_PRAYER_REMINDERS = "key_prayer_reminders"
        private const val KEY_CITY_ID = "key_selected_city_id"
        private const val KEY_CITY_NAME = "key_selected_city_name"
        private const val KEY_MUSHAF_DOWNLOAD_PROMPTED = "key_mushaf_download_prompted"
        private const val KEY_MUSHAF_ALL_DOWNLOADED = "key_mushaf_all_downloaded"
    }

    // --- Adhan & Sholat Preferences ---
    fun isAdhanEnabled(): Boolean = prefs.getBoolean(KEY_ADHAN_ENABLED, true)
    fun setAdhanEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_ADHAN_ENABLED, enabled).apply()

    fun getSelectedAdhanVoice(): String = prefs.getString(KEY_ADHAN_VOICE, "MAKKAH") ?: "MAKKAH"
    fun setSelectedAdhanVoice(voiceName: String) = prefs.edit().putString(KEY_ADHAN_VOICE, voiceName).apply()

    fun getPrayerReminderToggles(): Map<String, Boolean> {
        val defaultPrayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        return defaultPrayers.associateWith { prayerName ->
            prefs.getBoolean("${KEY_PRAYER_REMINDERS}_$prayerName", true)
        }
    }

    fun setPrayerReminderToggle(prayerName: String, enabled: Boolean) {
        prefs.edit().putBoolean("${KEY_PRAYER_REMINDERS}_$prayerName", enabled).apply()
    }

    fun getSavedCityId(): String = prefs.getString(KEY_CITY_ID, "1301") ?: "1301"
    fun getSavedCityName(): String = prefs.getString(KEY_CITY_NAME, "KOTA JAKARTA") ?: "KOTA JAKARTA"
    fun setSavedCity(id: String, name: String) {
        prefs.edit().putString(KEY_CITY_ID, id).putString(KEY_CITY_NAME, name).apply()
    }

    // --- Mushaf Offline Download ---
    fun isMushafDownloadPrompted(): Boolean = prefs.getBoolean(KEY_MUSHAF_DOWNLOAD_PROMPTED, false)
    fun setMushafDownloadPrompted(prompted: Boolean) = prefs.edit().putBoolean(KEY_MUSHAF_DOWNLOAD_PROMPTED, prompted).apply()

    fun isMushafAllDownloaded(): Boolean = prefs.getBoolean(KEY_MUSHAF_ALL_DOWNLOADED, false)
    fun setMushafAllDownloaded(downloaded: Boolean) = prefs.edit().putBoolean(KEY_MUSHAF_ALL_DOWNLOADED, downloaded).apply()

    fun getFavoriteIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleFavorite(doaId: Int): Boolean {
        val currentFavorites = getFavoriteIds().toMutableSet()
        val isNowFavorite = if (currentFavorites.contains(doaId)) {
            currentFavorites.remove(doaId)
            false
        } else {
            currentFavorites.add(doaId)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, currentFavorites.map { it.toString() }.toSet()).apply()
        return isNowFavorite
    }

    fun isFavorite(doaId: Int): Boolean {
        return getFavoriteIds().contains(doaId)
    }

    fun getArabicFontSize(): Float {
        return prefs.getFloat(KEY_ARABIC_FONT_SIZE, 24f)
    }

    fun setArabicFontSize(sizeSp: Float) {
        prefs.edit().putFloat(KEY_ARABIC_FONT_SIZE, sizeSp).apply()
    }

    fun isShowLatin(): Boolean {
        return prefs.getBoolean(KEY_SHOW_LATIN, true)
    }

    fun setShowLatin(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LATIN, show).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val modeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return try {
            AppThemeMode.valueOf(modeStr)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}
