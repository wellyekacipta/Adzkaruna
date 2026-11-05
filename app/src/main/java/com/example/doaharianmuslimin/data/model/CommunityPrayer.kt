package com.example.doaharianmuslimin.data.model

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Immutable
data class CommunityPrayer(
    val id: String,
    val senderName: String,
    val location: String,
    val prayerText: String,
    val timestamp: String,
    val createdAt: Long = 0L,
    val aamiinCount: Int = 0,
    val likeCount: Int = 0,
    val isAamiined: Boolean = false,
    val isLiked: Boolean = false,
    val displayTime: String = computeDisplayTime(createdAt, timestamp, id)
) {
    companion object {
        private val timeFormatter = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
            }
        }

        private val fullFormatter = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
            }
        }

        /**
         * Dihitung sekali saja saat pembuatan objek untuk menjamin scrolling 120 FPS tanpa lag / GC freeze.
         */
        fun computeDisplayTime(createdAt: Long, timestamp: String, id: String): String {
            val now = System.currentTimeMillis()
            val millis = when {
                createdAt > 0L -> createdAt
                else -> parseTimestampToMillis(timestamp, id) ?: now
            }

            val diff = now - millis
            if (diff < 0L) {
                return "Baru saja"
            }

            val minutes = diff / 60_000L
            if (diff < 60_000L) {
                return "Baru saja"
            }
            if (minutes < 60L) {
                return "$minutes menit lalu"
            }

            val calNow = Calendar.getInstance()
            val calPrayer = Calendar.getInstance().apply { timeInMillis = millis }

            val isSameDay = calNow.get(Calendar.YEAR) == calPrayer.get(Calendar.YEAR) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calPrayer.get(Calendar.DAY_OF_YEAR)

            calNow.add(Calendar.DAY_OF_YEAR, -1)
            val isYesterday = calNow.get(Calendar.YEAR) == calPrayer.get(Calendar.YEAR) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calPrayer.get(Calendar.DAY_OF_YEAR)

            val timeFormat = timeFormatter.get() ?: SimpleDateFormat("HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
            val fullFormat = fullFormatter.get() ?: SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))

            return when {
                isSameDay -> "Hari ini, ${timeFormat.format(Date(millis))}"
                isYesterday -> "Kemarin, ${timeFormat.format(Date(millis))}"
                else -> fullFormat.format(Date(millis))
            }
        }

        fun parseTimestampToMillis(raw: String?, id: String?): Long? {
            if (!raw.isNullOrBlank()) {
                val patterns = arrayOf(
                    "dd MMM yyyy, HH:mm 'WIB'",
                    "dd MMM yyyy, HH:mm",
                    "dd MMM yyyy"
                )
                for (pattern in patterns) {
                    try {
                        val sdf = SimpleDateFormat(pattern, Locale.forLanguageTag("id-ID"))
                        sdf.parse(raw.trim())?.time?.let { return it }
                    } catch (_: Exception) {}
                    try {
                        val sdfEn = SimpleDateFormat(pattern, Locale.ENGLISH)
                        sdfEn.parse(raw.trim())?.time?.let { return it }
                    } catch (_: Exception) {}
                }
            }
            val idMillis = id?.toLongOrNull()
            if (idMillis != null && idMillis > 1000000000000L) {
                return idMillis
            }
            return null
        }
    }
}
