package com.example.doaharianmuslimin.data

import android.content.Context
import android.content.SharedPreferences
import com.example.doaharianmuslimin.data.model.QuranDayStat
import com.example.doaharianmuslimin.data.model.QuranWeeklyStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

enum class AppThemeMode(val title: String) {
    LIGHT("Hijau Tosca & Putih (Default)"),
    DARK("Mode Gelap"),
    RED("Merah & Putih"),
    BLUE("Biru & Putih"),
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

class PreferencesManager(private val context: Context) {
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
        private const val KEY_MUSHAF_DOWNLOAD_PROMPTED = "key_mushaf_tajweed_download_prompted_v1"
        private const val KEY_MUSHAF_ALL_DOWNLOADED = "key_mushaf_tajweed_all_downloaded_v1"
        private const val KEY_USER_NAME = "key_profile_user_name"
        private const val KEY_QURAN_HISTORY = "key_quran_reading_history_json"
        private const val KEY_QURAN_FAVORITES = "key_quran_favorites_json"
        private const val KEY_DOA_HISTORY = "key_doa_reading_history_json"
        private const val KEY_QURAN_DAILY_READ_LOG = "key_quran_daily_read_log"
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
        recordQuranDailyPage(surahNumber, surahName, pageNumber)
    }

    fun recordQuranDailyPage(surahNumber: Int, surahName: String, pageNumber: Int) {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("id-ID"))
        val todayStr = sdfDate.format(Date())

        val currentLogJson = prefs.getString(KEY_QURAN_DAILY_READ_LOG, "{}") ?: "{}"
        try {
            val root = JSONObject(currentLogJson)
            val dayObj = root.optJSONObject(todayStr) ?: JSONObject()
            val pagesArr = dayObj.optJSONArray("pages") ?: JSONArray()
            val surahsArr = dayObj.optJSONArray("surahs") ?: JSONArray()

            var pageFound = false
            for (i in 0 until pagesArr.length()) {
                if (pagesArr.getInt(i) == pageNumber) {
                    pageFound = true
                    break
                }
            }
            if (!pageFound) pagesArr.put(pageNumber)

            var surahFound = false
            for (i in 0 until surahsArr.length()) {
                if (surahsArr.getString(i) == surahName) {
                    surahFound = true
                    break
                }
            }
            if (!surahFound) surahsArr.put(surahName)

            dayObj.put("pages", pagesArr)
            dayObj.put("surahs", surahsArr)
            dayObj.put("lastTimestamp", System.currentTimeMillis())

            root.put(todayStr, dayObj)
            prefs.edit().putString(KEY_QURAN_DAILY_READ_LOG, root.toString()).apply()
        } catch (_: Exception) {}
    }

    fun getQuranWeeklyStats(): QuranWeeklyStats {
        val now = Calendar.getInstance()
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val diffToMonday = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek

        val mondayCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, diffToMonday)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("id-ID"))
        val sdfDisplay = SimpleDateFormat("d MMM", Locale.forLanguageTag("id-ID"))
        val todayStr = sdfDate.format(now.time)

        val logJsonStr = prefs.getString(KEY_QURAN_DAILY_READ_LOG, "{}") ?: "{}"
        val logRoot = try { JSONObject(logJsonStr) } catch (_: Exception) { JSONObject() }
        val historyList = getQuranReadingHistory()

        val dayNames = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Ahad")
        val shortNames = arrayOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Ahd")

        val daysList = mutableListOf<QuranDayStat>()
        var activeDays = 0
        var totalPages = 0
        val allSurahs = mutableSetOf<String>()

        for (i in 0..6) {
            val dayCal = (mondayCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i)
            }
            val dateStr = sdfDate.format(dayCal.time)
            val dateDisp = sdfDisplay.format(dayCal.time)
            val isToday = (dateStr == todayStr)
            val isFuture = dayCal.after(now) && !isToday

            val dayObj = logRoot.optJSONObject(dateStr)
            val pagesSet = mutableSetOf<Int>()
            val surahsSet = mutableSetOf<String>()

            if (dayObj != null) {
                val pagesArr = dayObj.optJSONArray("pages")
                if (pagesArr != null) {
                    for (p in 0 until pagesArr.length()) {
                        pagesSet.add(pagesArr.getInt(p))
                    }
                }
                val surahsArr = dayObj.optJSONArray("surahs")
                if (surahsArr != null) {
                    for (s in 0 until surahsArr.length()) {
                        surahsSet.add(surahsArr.getString(s))
                    }
                }
            }

            for (hist in historyList) {
                if (sdfDate.format(Date(hist.timestamp)) == dateStr) {
                    pagesSet.add(hist.pageNumber)
                    surahsSet.add(hist.surahName)
                }
            }

            val pagesCount = pagesSet.size
            if (pagesCount > 0) {
                activeDays++
                totalPages += pagesCount
                allSurahs.addAll(surahsSet)
            }

            daysList.add(
                QuranDayStat(
                    dayIndex = i,
                    dayName = dayNames[i],
                    shortName = shortNames[i],
                    dateDisplay = dateDisp,
                    fullDateString = dateStr,
                    pagesRead = pagesCount,
                    surahs = surahsSet.toList(),
                    isToday = isToday,
                    isFuture = isFuture
                )
            )
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        while (true) {
            val checkDateStr = sdfDate.format(checkCal.time)
            val dayObj = logRoot.optJSONObject(checkDateStr)
            val hasInLog = dayObj != null && (dayObj.optJSONArray("pages")?.length() ?: 0) > 0
            val hasInHist = historyList.any { sdfDate.format(Date(it.timestamp)) == checkDateStr }
            if (hasInLog || hasInHist) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        val todayIdx = daysList.indexOfFirst { it.isToday }
        val currentDayOfWeek = if (todayIdx >= 0) todayIdx + 1 else 1

        return QuranWeeklyStats(
            days = daysList,
            activeDaysCount = activeDays,
            totalPagesRead = totalPages,
            totalSurahsRead = allSurahs.size,
            currentStreak = streak,
            currentDayOfWeek = currentDayOfWeek
        )
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
        val existingIndex = current.indexOfFirst { it.pageNumber == pageNumber }
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

    // --- Reset & Hapus Riwayat Bacaan (Aman: Favorit Tetap Terjaga) ---
    fun clearQuranReadingHistory(clearDailyStats: Boolean = false) {
        val editor = prefs.edit().remove(KEY_QURAN_HISTORY)
        if (clearDailyStats) {
            editor.remove(KEY_QURAN_DAILY_READ_LOG)
        }
        editor.apply()

        // Reset penanda halaman terakhir dibaca pada engine bacaan
        val adzkarunaPrefs = context.getSharedPreferences("adzkaruna_quran_prefs", Context.MODE_PRIVATE)
        adzkarunaPrefs.edit()
            .remove("last_read_page")
            .remove("last_read_surah")
            .remove("last_read_surah_number")
            .apply()
    }

    fun clearDoaReadingHistory() {
        prefs.edit().remove(KEY_DOA_HISTORY).apply()
    }

    fun clearAllReadingHistory(clearDailyStats: Boolean = false) {
        clearQuranReadingHistory(clearDailyStats)
        clearDoaReadingHistory()
    }
}
