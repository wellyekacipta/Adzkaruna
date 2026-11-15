package com.example.doaharianmuslimin.data.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class PrayerCheckStatus(val label: String, val badgeColor: Long) {
    TEPAT_WAKTU("Tepat waktu", 0xFF4CAF50),
    BERJAMAAH("Berjamaah", 0xFF8E24AA),
    TERLAMBAT("Terlambat", 0xFFF57C00),
    TERLEWAT("Terlewat", 0xFF757575),
    BELUM("Belum", 0xFF9E9E9E)
}

@Immutable
data class PrayerCheckItem(
    val id: String,
    val name: String,
    val defaultTime: String,
    val status: PrayerCheckStatus = PrayerCheckStatus.BELUM
)

enum class PuasaType(val displayName: String) {
    SENIN_KAMIS("Puasa Senin - Kamis"),
    AYYAMUL_BIDH("Puasa Ayyamul Bidh"),
    RAMADHAN("Puasa Ramadhan"),
    DAUD("Puasa Daud"),
    QADHA("Puasa Qadha / Nadzar"),
    SUNNAH_LAIN("Puasa Sunnah Lainnya")
}

@Immutable
data class IslamicCalendarDay(
    val dayOfMonth: Int,
    val hijriDay: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isAyyamulBidh: Boolean,
    val isPuasaSeninKamis: Boolean,
    val isHariBesar: Boolean,
    val eventName: String? = null,
    val isFastingChecked: Boolean = false
)

@Immutable
data class DzikirTargetItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false
)

@Immutable
data class SunnahSholatItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val rakaat: Int = 2,
    val isCompleted: Boolean = false
)
