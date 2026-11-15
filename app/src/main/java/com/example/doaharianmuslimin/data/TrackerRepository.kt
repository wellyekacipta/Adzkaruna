package com.example.doaharianmuslimin.data

import android.content.Context
import android.content.SharedPreferences
import com.example.doaharianmuslimin.data.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

class TrackerRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adzkaruna_tracker_prefs", Context.MODE_PRIVATE)

    private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateKey(): String = sdfDate.format(Date())

    // ==================== TILAWAH (NGAJI) TRACKER ====================

    fun getDailyTilawahTarget(): Int = prefs.getInt("tilawah_daily_target", 5)

    fun setDailyTilawahTarget(target: Int) {
        prefs.edit().putInt("tilawah_daily_target", max(1, target)).apply()
    }

    fun getTodayTilawahPages(): Int {
        val todayKey = getTodayDateKey()
        return prefs.getInt("tilawah_pages_$todayKey", 0)
    }

    fun addTilawahPages(pages: Int) {
        val todayKey = getTodayDateKey()
        val current = getTodayTilawahPages()
        prefs.edit().putInt("tilawah_pages_$todayKey", current + max(1, pages)).apply()
    }

    fun getMonthTilawahHeatmap(year: Int, month: Int): Map<Int, Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val map = mutableMapOf<Int, Int>()

        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateKey = sdfDate.format(cal.time)
            val pages = prefs.getInt("tilawah_pages_$dateKey", 0)
            if (pages > 0) {
                map[day] = pages
            }
        }
        return map
    }

    fun getTotalTilawahPagesThisMonth(): Int {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)
        return getMonthTilawahHeatmap(currentYear, currentMonth).values.sum()
    }

    // ==================== SHOLAT FARDHU TRACKER ====================

    fun getPrayerStatus(dateKey: String, prayerId: String): PrayerCheckStatus {
        val raw = prefs.getString("prayer_status_${dateKey}_$prayerId", null) ?: return PrayerCheckStatus.BELUM
        return try {
            PrayerCheckStatus.valueOf(raw)
        } catch (_: Exception) {
            PrayerCheckStatus.BELUM
        }
    }

    fun setPrayerStatus(dateKey: String, prayerId: String, status: PrayerCheckStatus) {
        prefs.edit().putString("prayer_status_${dateKey}_$prayerId", status.name).apply()
        // Also sync with main PreferencesManager checkin
        val mainPrefs = PreferencesManager(context)
        mainPrefs.setPrayerCheckedIn(dateKey, prayerId, status != PrayerCheckStatus.BELUM && status != PrayerCheckStatus.TERLEWAT)
    }

    fun getTodayPrayers(): List<PrayerCheckItem> {
        val todayKey = getTodayDateKey()
        val defaults = listOf(
            PrayerCheckItem("Subuh", "Subuh", "04:15"),
            PrayerCheckItem("Dzuhur", "Dzuhur", "11:45"),
            PrayerCheckItem("Ashar", "Ashar", "15:05"),
            PrayerCheckItem("Maghrib", "Maghrib", "17:55"),
            PrayerCheckItem("Isya", "Isya", "19:08")
        )
        return defaults.map { item ->
            item.copy(status = getPrayerStatus(todayKey, item.id))
        }
    }

    fun getMonthPrayerStats(year: Int, month: Int): Map<PrayerCheckStatus, Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val counts = mutableMapOf(
            PrayerCheckStatus.TEPAT_WAKTU to 0,
            PrayerCheckStatus.BERJAMAAH to 0,
            PrayerCheckStatus.TERLAMBAT to 0,
            PrayerCheckStatus.TERLEWAT to 0
        )
        val prayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateKey = sdfDate.format(cal.time)
            for (p in prayers) {
                val st = getPrayerStatus(dateKey, p)
                if (counts.containsKey(st)) {
                    counts[st] = (counts[st] ?: 0) + 1
                }
            }
        }
        return counts
    }

    // ==================== SHOLAT SUNNAH (TAHAJUD & DHUHA) ====================

    fun isSunnahChecked(dateKey: String, id: String): Boolean {
        return prefs.getBoolean("sunnah_${dateKey}_$id", false)
    }

    fun setSunnahChecked(dateKey: String, id: String, checked: Boolean, rakaat: Int = 2) {
        prefs.edit()
            .putBoolean("sunnah_${dateKey}_$id", checked)
            .putInt("sunnah_rakaat_${dateKey}_$id", rakaat)
            .apply()
    }

    fun getSunnahRakaat(dateKey: String, id: String): Int {
        return prefs.getInt("sunnah_rakaat_${dateKey}_$id", 2)
    }

    // ==================== DZIKIR TRACKER ====================

    fun isDzikirChecked(dateKey: String, id: String): Boolean {
        return prefs.getBoolean("dzikir_${dateKey}_$id", false)
    }

    fun setDzikirChecked(dateKey: String, id: String, checked: Boolean) {
        prefs.edit().putBoolean("dzikir_${dateKey}_$id", checked).apply()
    }

    fun getTasbihCount(dateKey: String): Int {
        return prefs.getInt("tasbih_count_$dateKey", 0)
    }

    fun addTasbihCount(dateKey: String, count: Int = 33) {
        val current = getTasbihCount(dateKey)
        prefs.edit().putInt("tasbih_count_$dateKey", current + count).apply()
    }

    // ==================== PUASA TRACKER & KALENDER ====================

    fun isPuasaChecked(dateKey: String): Boolean {
        return prefs.getBoolean("puasa_checked_$dateKey", false)
    }

    fun setPuasaChecked(dateKey: String, checked: Boolean, type: String = "Puasa Sunnah") {
        prefs.edit()
            .putBoolean("puasa_checked_$dateKey", checked)
            .putString("puasa_type_$dateKey", type)
            .apply()
    }

    fun getPuasaType(dateKey: String): String {
        return prefs.getString("puasa_type_$dateKey", "Puasa Sunnah") ?: "Puasa Sunnah"
    }

    // ==================== STREAK CALCULATOR ====================

    fun getCurrentStreak(): Int {
        var streak = 0
        val cal = Calendar.getInstance()
        val prayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

        while (true) {
            val dateKey = sdfDate.format(cal.time)
            val hasTilawah = prefs.getInt("tilawah_pages_$dateKey", 0) > 0
            val hasPrayer = prayers.any {
                val st = getPrayerStatus(dateKey, it)
                st == PrayerCheckStatus.TEPAT_WAKTU || st == PrayerCheckStatus.BERJAMAAH
            }
            val hasPuasa = isPuasaChecked(dateKey)
            val hasDzikir = isDzikirChecked(dateKey, "pagi") || isDzikirChecked(dateKey, "petang")

            if (hasTilawah || hasPrayer || hasPuasa || hasDzikir) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                // If today has no activity yet, check yesterday before breaking
                if (streak == 0 && dateKey == getTodayDateKey()) {
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    val yestKey = sdfDate.format(cal.time)
                    val yestActivity = prefs.getInt("tilawah_pages_$yestKey", 0) > 0 ||
                            prayers.any {
                                val st = getPrayerStatus(yestKey, it)
                                st == PrayerCheckStatus.TEPAT_WAKTU || st == PrayerCheckStatus.BERJAMAAH
                            }
                    if (yestActivity) {
                        // Keep checking backwards from yesterday
                        streak = 0
                        continue
                    }
                }
                break
            }
        }
        return max(1, streak)
    }
}
