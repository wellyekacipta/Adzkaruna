package com.example.doaharianmuslimin.data

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode(val title: String) {
    LIGHT("Mode Terang"),
    DARK("Mode Gelap"),
    SYSTEM("Ikuti Sistem")
}


data class QuranHistoryItem(
    val surahNumber: Int,
    val surahName: String,
    val pageNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class DoaHistoryItem(
    val doaId: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

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
        private const val KEY_USER_NAME = "key_profile_user_name"
        private const val KEY_QURAN_HISTORY = "key_quran_reading_history_json"
        private const val KEY_QURAN_FAVORITES = "key_quran_favorites_json"
        private const val KEY_DOA_HISTORY = "key_doa_reading_history_json"
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

    // --- Profil & Edit Nama ---
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Hamba Allah") ?: "Hamba Allah"
    fun setUserName(name: String) = prefs.edit().putString(KEY_USER_NAME, name.trim().ifBlank { "Hamba Allah" }).apply()

    // --- Riwayat & Favorit Al-Qur'an ---
    fun getQuranReadingHistory(): List<QuranHistoryItem> {
        val jsonStr = prefs.getString(KEY_QURAN_HISTORY, null) ?: return emptyList()
        return try {
            val list = mutableListOf<QuranHistoryItem>()
            val array = org.json.JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    QuranHistoryItem(
                        surahNumber = obj.getInt("surahNumber"),
                        surahName = obj.getString("surahName"),
                        pageNumber = obj.getInt("pageNumber"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (_: Exception) { emptyList() }
    }

    fun addQuranReadingHistory(surahNumber: Int, surahName: String, pageNumber: Int) {
        val current = getQuranReadingHistory().filterNot { it.surahNumber == surahNumber && it.pageNumber == pageNumber }.toMutableList()
        current.add(0, QuranHistoryItem(surahNumber, surahName, pageNumber, System.currentTimeMillis()))
        val trimmed = current.take(30)
        val array = org.json.JSONArray()
        for (item in trimmed) {
            array.put(org.json.JSONObject().apply {
                put("surahNumber", item.surahNumber)
                put("surahName", item.surahName)
                put("pageNumber", item.pageNumber)
                put("timestamp", item.timestamp)
            })
        }
        prefs.edit().putString(KEY_QURAN_HISTORY, array.toString()).apply()
    }

    fun getQuranFavorites(): List<QuranHistoryItem> {
        val jsonStr = prefs.getString(KEY_QURAN_FAVORITES, null) ?: return emptyList()
        return try {
            val list = mutableListOf<QuranHistoryItem>()
            val array = org.json.JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    QuranHistoryItem(
                        surahNumber = obj.getInt("surahNumber"),
                        surahName = obj.getString("surahName"),
                        pageNumber = obj.getInt("pageNumber"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (_: Exception) { emptyList() }
    }

    fun toggleQuranFavorite(surahNumber: Int, surahName: String, pageNumber: Int): Boolean {
        val current = getQuranFavorites().toMutableList()
        val existingIndex = current.indexOfFirst { it.surahNumber == surahNumber && it.pageNumber == pageNumber }
        val isNowFav: Boolean
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
            isNowFav = false
        } else {
            current.add(0, QuranHistoryItem(surahNumber, surahName, pageNumber, System.currentTimeMillis()))
            isNowFav = true
        }
        val array = org.json.JSONArray()
        for (item in current) {
            array.put(org.json.JSONObject().apply {
                put("surahNumber", item.surahNumber)
                put("surahName", item.surahName)
                put("pageNumber", item.pageNumber)
                put("timestamp", item.timestamp)
            })
        }
        prefs.edit().putString(KEY_QURAN_FAVORITES, array.toString()).apply()
        return isNowFav
    }

    fun isQuranFavorite(pageNumber: Int): Boolean {
        return getQuranFavorites().any { it.pageNumber == pageNumber }
    }

    // --- Riwayat Bacaan Doa ---
    fun getDoaReadingHistory(): List<DoaHistoryItem> {
        val jsonStr = prefs.getString(KEY_DOA_HISTORY, null) ?: return emptyList()
        return try {
            val list = mutableListOf<DoaHistoryItem>()
            val array = org.json.JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DoaHistoryItem(
                        doaId = obj.getInt("doaId"),
                        title = obj.getString("title"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (_: Exception) { emptyList() }
    }

    fun addDoaReadingHistory(doaId: Int, title: String) {
        val current = getDoaReadingHistory().filterNot { it.doaId == doaId }.toMutableList()
        current.add(0, DoaHistoryItem(doaId, title, System.currentTimeMillis()))
        val trimmed = current.take(30)
        val array = org.json.JSONArray()
        for (item in trimmed) {
            array.put(org.json.JSONObject().apply {
                put("doaId", item.doaId)
                put("title", item.title)
                put("timestamp", item.timestamp)
            })
        }
        prefs.edit().putString(KEY_DOA_HISTORY, array.toString()).apply()
    }
}
