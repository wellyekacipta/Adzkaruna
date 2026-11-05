package com.example.doaharianmuslimin.data.model

import androidx.compose.runtime.Stable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Stable
data class CommunityPrayer(
    val id: String,
    val senderName: String,
    val location: String,
    val prayerText: String,
    val timestamp: String,
    val createdAt: Long = 0L,
    var aamiinCount: Int = 0,
    var likeCount: Int = 0,
    var isAamiined: Boolean = false,
    var isLiked: Boolean = false
) {
    /**
     * Menghasilkan teks waktu yang dinamis dan beradaptasi secara alami:
     * - < 1 menit: "Baru saja"
     * - < 60 menit: "X menit lalu"
     * - Hari ini: "Hari ini, HH:mm WIB"
     * - Kemarin: "Kemarin, HH:mm WIB"
     * - Lebih lama: "dd MMM yyyy, HH:mm WIB"
     *
     * Dioptimalkan dengan ThreadLocal Formatter untuk performa scrolling 120 FPS tanpa GC freeze.
     */
    val displayTime: String
        get() {
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
                diff < 60_000L -> "Baru saja"
                minutes < 60L -> "$minutes menit lalu"
                isSameDay -> "Hari ini, ${timeFormat.format(Date(millis))}"
                isYesterday -> "Kemarin, ${timeFormat.format(Date(millis))}"
                else -> fullFormat.format(Date(millis))
            }
        }

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
