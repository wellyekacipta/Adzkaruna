package com.example.doaharianmuslimin.data

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

object PrayerTimesRepository {

    val popularCities = listOf(
        CityOption("1301", "Kota Jakarta"),
        CityOption("1201", "Kota Bandung"),
        CityOption("1501", "Kota Surabaya"),
        CityOption("1401", "Kota Yogyakarta"),
        CityOption("1307", "Kota Semarang"),
        CityOption("0201", "Kota Medan"),
        CityOption("2301", "Kota Makassar"),
        CityOption("0601", "Kota Palembang"),
        CityOption("1701", "Kota Denpasar"),
        CityOption("0101", "Kota Banda Aceh"),
        CityOption("2101", "Kota Banjarmasin")
    )

    suspend fun fetchKemenagSchedule(cityId: String = "1301", cityName: String = "Kota Jakarta"): PrayerSchedule {
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
                connection.connectTimeout = 4000
                connection.readTimeout = 4000

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

                        val items = listOf(
                            PrayerTimeItem("Imsak", imsak, "الإمساك"),
                            PrayerTimeItem("Subuh", subuh, "الفجر"),
                            PrayerTimeItem("Terbit", terbit, "الشروق"),
                            PrayerTimeItem("Dhuha", dhuha, "الضحى"),
                            PrayerTimeItem("Dzuhur", dzuhur, "الظهر"),
                            PrayerTimeItem("Ashar", ashar, "العصر"),
                            PrayerTimeItem("Maghrib", maghrib, "المغرب", isNext = true),
                            PrayerTimeItem("Isya", isya, "العشاء")
                        )

                        return@withContext PrayerSchedule(
                            city = "$lokasi (Kemenag RI)",
                            date = tanggal,
                            hijriDate = "Jadwal Resmi Kemenag",
                            items = items
                        )
                    }
                }
            } catch (_: Exception) {
                // Fallback to local offline schedule if no internet or timeout
            }

            getTodaySchedule(cityName)
        }
    }

    fun getTodaySchedule(city: String = "Kota Jakarta"): PrayerSchedule {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))
        val todayStr = dateFormat.format(Calendar.getInstance().time)

        val items = listOf(
            PrayerTimeItem("Imsak", "04:03", "الإمساك"),
            PrayerTimeItem("Subuh", "04:13", "الفجر"),
            PrayerTimeItem("Terbit", "05:26", "الشروق"),
            PrayerTimeItem("Dhuha", "05:54", "الضحى"),
            PrayerTimeItem("Dzuhur", "11:41", "الظهر"),
            PrayerTimeItem("Ashar", "14:50", "العصر"),
            PrayerTimeItem("Maghrib", "17:49", "المغرب", isNext = true),
            PrayerTimeItem("Isya", "18:59", "العشاء")
        )

        return PrayerSchedule(
            city = "$city (Offline / Standar)",
            date = todayStr,
            hijriDate = "Jadwal Waktu Sholat",
            items = items
        )
    }
}
