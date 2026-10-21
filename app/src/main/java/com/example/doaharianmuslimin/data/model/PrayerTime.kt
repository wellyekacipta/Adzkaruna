package com.example.doaharianmuslimin.data.model

data class PrayerTimeItem(
    val name: String,
    val time: String,
    val arabicName: String,
    val isNext: Boolean = false
)

data class PrayerSchedule(
    val city: String,
    val date: String,
    val hijriDate: String,
    val items: List<PrayerTimeItem>
)
