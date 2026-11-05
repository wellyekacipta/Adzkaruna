package com.example.doaharianmuslimin.data

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object IslamicDateHelper {

    fun getTodayMasehi(): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))
        return sdf.format(Calendar.getInstance().time)
    }

    fun getTodayHijri(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val hijrahDate = java.time.chrono.HijrahDate.now()
                val day = hijrahDate.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
                val month = hijrahDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
                val year = hijrahDate.get(java.time.temporal.ChronoField.YEAR_OF_ERA)

                val monthNames = listOf(
                    "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
                    "Jumadil Awwal", "Jumadil Akhir", "Rajab", "Sya'ban",
                    "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
                )
                val mName = monthNames.getOrElse(month - 1) { "Hijriyah" }
                return "$day $mName $year H"
            } catch (_: Exception) {}
        }
        return getFallbackHijri()
    }

    private fun getFallbackHijri(): String {
        val cal = Calendar.getInstance()
        var day = cal.get(Calendar.DAY_OF_MONTH)
        var month = cal.get(Calendar.MONTH) + 1
        var year = cal.get(Calendar.YEAR)

        if (month < 3) {
            year -= 1
            month += 12
        }

        val a = year / 100
        val b = 2 - a + (a / 4)
        val jd = (365.25 * (year + 4716)).toLong() + (30.6001 * (month + 1)).toInt() + day + b - 1524.5

        val islamicDays = jd - 1948439.5
        val hijriYear = ((islamicDays - 1) / 354.367056).toInt() + 1
        val dayOfYear = islamicDays - ((hijriYear - 1) * 354.367056)
        val hijriMonth = (dayOfYear / 29.530588).toInt() + 1
        val hijriDay = (dayOfYear - ((hijriMonth - 1) * 29.530588)).toInt() + 1

        val monthNames = listOf(
            "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
            "Jumadil Awwal", "Jumadil Akhir", "Rajab", "Sya'ban",
            "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
        )
        val mName = monthNames.getOrElse((hijriMonth - 1).coerceIn(0, 11)) { "Hijriyah" }
        return "$hijriDay $mName $hijriYear H"
    }
}
