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
    }

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
