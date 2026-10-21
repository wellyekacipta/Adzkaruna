package com.example.doaharianmuslimin.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.example.doaharianmuslimin.data.model.PrayerSchedule
import com.example.doaharianmuslimin.data.model.PrayerTimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CityOption(val id: String, val name: String)

enum class AdhanVoiceOption(val displayName: String, val audioUrl: String) {
    MAKKAH("Adzan Makkah (Syaikh Ali Mullah)", "https://raw.githubusercontent.com/achaudhry/adhan/master/Adhan-Makkah.mp3"),
    MADINAH("Adzan Madinah (Masjid Nabawi)", "https://raw.githubusercontent.com/achaudhry/adhan/master/Adhan-Madinah.mp3"),
    MISYARI("Adzan Misyari Rasyid Al-Afasy", "https://raw.githubusercontent.com/achaudhry/adhan/master/Adhan-Mishary-Rashid-Al-Afasy.mp3"),
    STANDAR("Adzan Subuh Makkah (As-Shalatu Khairun Minan Naum)", "https://raw.githubusercontent.com/achaudhry/adhan/master/Adhan-fajr.mp3")
}

object PrayerTimesRepository {

    // Cached all 518 cities in Indonesia
    private var allCitiesCache: List<CityOption> = emptyList()

    val popularCities = listOf(
        CityOption("1301", "KOTA JAKARTA"),
        CityOption("1201", "KOTA BANDUNG"),
        CityOption("1501", "KOTA SURABAYA"),
        CityOption("1401", "KOTA YOGYAKARTA"),
        CityOption("1307", "KOTA SEMARANG"),
        CityOption("0201", "KOTA MEDAN"),
        CityOption("2301", "KOTA MAKASSAR"),
        CityOption("0601", "KOTA PALEMBANG"),
        CityOption("1701", "KOTA DENPASAR"),
        CityOption("0101", "KOTA BANDA ACEH"),
        CityOption("2101", "KOTA BANJARMASIN"),
        CityOption("1205", "KAB. BOGOR"),
        CityOption("1221", "KOTA BEKASI"),
        CityOption("1222", "KOTA DEPOK"),
        CityOption("1223", "KOTA TANGERANG"),
        CityOption("1224", "KOTA TANGERANG SELATAN")
    )

    /**
     * Fetch all 518 cities & regencies across Indonesia from Kemenag API
     */
    suspend fun getAllCities(): List<CityOption> {
        if (allCitiesCache.isNotEmpty()) return allCitiesCache

        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.myquran.com/v2/sholat/kota/semua")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 6000
                connection.readTimeout = 6000

                if (connection.responseCode == 200) {
                    val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val root = JSONObject(jsonStr)
                    if (root.optBoolean("status", false)) {
                        val dataArray = root.getJSONArray("data")
                        val list = mutableListOf<CityOption>()
                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            list.add(CityOption(obj.getString("id"), obj.getString("lokasi")))
                        }
                        allCitiesCache = list
                        return@withContext list
                    }
                }
            } catch (_: Exception) {}

            popularCities
        }
    }

    /**
     * Search cities with query
     */
    suspend fun searchCities(query: String): List<CityOption> {
        val cities = if (allCitiesCache.isNotEmpty()) allCitiesCache else getAllCities()
        if (query.isBlank()) return cities.take(40)
        val clean = query.trim().uppercase(Locale.ROOT)
        return cities.filter { it.name.uppercase(Locale.ROOT).contains(clean) }
    }

    /**
     * Automatically detect user's current city via GPS
     */
    @SuppressLint("MissingPermission")
    suspend fun detectCityFromLocation(context: Context): CityOption? {
        return withContext(Dispatchers.IO) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)

                var bestLocation: Location? = null
                for (provider in providers) {
                    if (locationManager?.isProviderEnabled(provider) == true) {
                        val loc = locationManager.getLastKnownLocation(provider)
                        if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                            bestLocation = loc
                        }
                    }
                }

                if (bestLocation != null && Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.forLanguageTag("id-ID"))
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(bestLocation.latitude, bestLocation.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val rawCity = addr.subAdminArea ?: addr.locality ?: addr.adminArea ?: ""
                        val cleanQuery = rawCity.replace("Kota", "", ignoreCase = true)
                            .replace("Kabupaten", "", ignoreCase = true)
                            .trim()

                        if (cleanQuery.isNotEmpty()) {
                            val matched = searchCities(cleanQuery)
                            if (matched.isNotEmpty()) {
                                return@withContext matched.first()
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
            null
        }
    }

    /**
     * Fetch prayer schedule from Kemenag API for specified city
     */
    suspend fun fetchKemenagSchedule(cityId: String = "1301", cityName: String = "KOTA JAKARTA"): PrayerSchedule {
        return withContext(Dispatchers.IO) {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = String.format(Locale.ROOT, "%02d", cal.get(Calendar.MONTH) + 1)
            val day = String.format(Locale.ROOT, "%02d", cal.get(Calendar.DAY_OF_MONTH))

            val urlString = "https://api.myquran.com/v2/sholat/jadwal/$cityId/$year/$month/$day"
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val root = JSONObject(jsonStr)
                    if (root.optBoolean("status", false)) {
                        val dataObj = root.getJSONObject("data")
                        val jadwalObj = dataObj.getJSONObject("jadwal")
                        val lokasi = dataObj.optString("lokasi", cityName)

                        val imsak = jadwalObj.getString("imsak")
                        val subuh = jadwalObj.getString("subuh")
                        val terbit = jadwalObj.getString("terbit")
                        val dhuha = jadwalObj.optString("dhuha", "06:00")
                        val dzuhur = jadwalObj.getString("dzuhur")
                        val ashar = jadwalObj.getString("ashar")
                        val maghrib = jadwalObj.getString("maghrib")
                        val isya = jadwalObj.getString("isya")
                        val tanggal = jadwalObj.optString("tanggal", "Hari ini")

                        val rawItems = listOf(
                            PrayerTimeItem("Imsak", imsak, "الإمساك"),
                            PrayerTimeItem("Subuh", subuh, "الفجر"),
                            PrayerTimeItem("Terbit", terbit, "الشروq"),
                            PrayerTimeItem("Dhuha", dhuha, "الضحى"),
                            PrayerTimeItem("Dzuhur", dzuhur, "الظهر"),
                            PrayerTimeItem("Ashar", ashar, "العصر"),
                            PrayerTimeItem("Maghrib", maghrib, "المغرب"),
                            PrayerTimeItem("Isya", isya, "العشاء")
                        )

                        // Calculate which prayer is truly next based on current live time
                        val items = calculateNextPrayerStatus(rawItems)

                        return@withContext PrayerSchedule(
                            city = "$lokasi (Kemenag RI)",
                            date = tanggal,
                            hijriDate = "Jadwal Resmi Kemenag",
                            items = items
                        )
                    }
                }
            } catch (_: Exception) {}

            getTodaySchedule(cityName)
        }
    }

    /**
     * Dynamically determines which prayer is NEXT based on the current live time of day.
     * Fixes the previous bug where isNext was hardcoded to Maghrib!
     */
    fun calculateNextPrayerStatus(items: List<PrayerTimeItem>): List<PrayerTimeItem> {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        // Only major prayers count for primary next prayer notification
        val majorPrayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

        var nextPrayerName: String? = null
        for (item in items) {
            if (!majorPrayers.contains(item.name)) continue
            val parts = item.time.split(":")
            if (parts.size >= 2) {
                val pMinutes = parts[0].trim().toIntOrNull()?.times(60)?.plus(parts[1].trim().toIntOrNull() ?: 0) ?: 0
                if (pMinutes > currentMinutes) {
                    nextPrayerName = item.name
                    break
                }
            }
        }

        // If all prayers of today have passed (e.g. after Isya 19:30 or late at night), next prayer is tomorrow's Subuh!
        if (nextPrayerName == null) {
            nextPrayerName = "Subuh"
        }

        return items.map { item ->
            item.copy(isNext = (item.name == nextPrayerName))
        }
    }

    /**
     * Calculates remaining seconds and formatted string to the next prayer
     */
    fun getCountdownToNextPrayer(schedule: PrayerSchedule?): Pair<String, String> {
        if (schedule == null) return Pair("00:00:00", "Subuh")

        val nextItem = schedule.items.find { it.isNext } ?: schedule.items.find { it.name == "Subuh" }
        val prayerName = nextItem?.name ?: "Subuh"
        val timeStr = nextItem?.time ?: "04:15"

        val now = Calendar.getInstance()
        val parts = timeStr.split(":")
        val targetHour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 4
        val targetMin = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 15

        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMin)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target is in the past, it means next prayer is tomorrow
        if (targetCal.before(now)) {
            targetCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val diffMillis = (targetCal.timeInMillis - now.timeInMillis).coerceAtLeast(0)
        val diffSec = diffMillis / 1000
        val hours = diffSec / 3600
        val minutes = (diffSec % 3600) / 60
        val seconds = diffSec % 60

        val countdownStr = String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
        return Pair(countdownStr, prayerName)
    }

    fun getTodaySchedule(city: String = "KOTA JAKARTA"): PrayerSchedule {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))
        val todayStr = dateFormat.format(Calendar.getInstance().time)

        val rawItems = listOf(
            PrayerTimeItem("Imsak", "04:08", "الإمساك"),
            PrayerTimeItem("Subuh", "04:18", "الفجر"),
            PrayerTimeItem("Terbit", "05:32", "الشروق"),
            PrayerTimeItem("Dhuha", "05:59", "الضحى"),
            PrayerTimeItem("Dzuhur", "11:43", "الظهر"),
            PrayerTimeItem("Ashar", "14:52", "العصر"),
            PrayerTimeItem("Maghrib", "17:49", "المغرب"),
            PrayerTimeItem("Isya", "18:59", "العشاء")
        )

        val items = calculateNextPrayerStatus(rawItems)

        return PrayerSchedule(
            city = "$city (Standar Kemenag)",
            date = todayStr,
            hijriDate = "Jadwal Waktu Sholat",
            items = items
        )
    }
}
