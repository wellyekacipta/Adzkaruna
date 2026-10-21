package com.example.doaharianmuslimin.data

import com.example.doaharianmuslimin.data.model.PrayerSchedule
import com.example.doaharianmuslimin.data.model.PrayerTimeItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PrayerTimesRepository {

    fun getTodaySchedule(city: String = "Jakarta & Sekitarnya (WIB)"): PrayerSchedule {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))
        val todayStr = dateFormat.format(Calendar.getInstance().time)

        val items = listOf(
            PrayerTimeItem("Imsak", "04:12", "الإمساك"),
            PrayerTimeItem("Subuh", "04:22", "الفجر"),
            PrayerTimeItem("Terbit", "05:38", "الشروق"),
            PrayerTimeItem("Dzuhur", "11:46", "الظهر"),
            PrayerTimeItem("Ashar", "14:58", "العصر"),
            PrayerTimeItem("Maghrib", "17:52", "المغرب", isNext = true),
            PrayerTimeItem("Isya", "19:02", "العشاء")
        )

        return PrayerSchedule(
            city = city,
            date = todayStr,
            hijriDate = "4 Dzulqa'dah 1447 H",
            items = items
        )
    }
}
