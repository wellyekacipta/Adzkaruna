package com.example.doaharianmuslimin.data.remote

import com.example.doaharianmuslimin.data.model.CommunityPrayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Konfigurasi Cloud Backend untuk Realtime Doa Jamaah (Adzkaruna)
 *
 * Mendukung Supabase (PostgreSQL Realtime) & Custom REST Backend tanpa perlu login user.
 * Cukup masukkan URL dan ANON_KEY dari dashboard Supabase Anda.
 */
object CloudConfig {
    // 1. Buat project gratis di https://supabase.com
    // 2. Salin Project URL & Anon Public Key dari menu Settings -> API
    // 3. Masukkan di bawah ini, maka seluruh doa otomatis tersinkronisasi ke seluruh pengguna di dunia!
    var SUPABASE_URL: String = "" 
    var SUPABASE_ANON_KEY: String = ""

    val isCloudEnabled: Boolean
        get() = SUPABASE_URL.isNotBlank() && SUPABASE_ANON_KEY.isNotBlank()
}

object CloudPrayerSync {

    /**
     * Mengambil daftar doa terbaru dari Supabase Cloud
     * Tabel: 'prayers'
     */
    suspend fun fetchPrayersFromCloud(): List<CommunityPrayer>? = withContext(Dispatchers.IO) {
        if (!CloudConfig.isCloudEnabled) return@withContext null

        try {
            val endpoint = "${CloudConfig.SUPABASE_URL}/rest/v1/prayers?select=*&order=created_at.desc&limit=50"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", CloudConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${CloudConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (connection.responseCode in 200..299) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val resultList = mutableListOf<CommunityPrayer>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    resultList.add(
                        CommunityPrayer(
                            id = obj.optString("id", System.currentTimeMillis().toString()),
                            senderName = obj.optString("sender_name", "Hamba Allah"),
                            location = obj.optString("location", "Indonesia"),
                            prayerText = obj.optString("prayer_text", ""),
                            timestamp = obj.optString("timestamp_label", "Baru saja"),
                            aamiinCount = obj.optInt("aamiin_count", 1),
                            likeCount = obj.optInt("like_count", 0)
                        )
                    )
                }
                resultList
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Mengirimkan doa baru ke Supabase Cloud secara instan
     */
    suspend fun postPrayerToCloud(prayer: CommunityPrayer): Boolean = withContext(Dispatchers.IO) {
        if (!CloudConfig.isCloudEnabled) return@withContext false

        try {
            val endpoint = "${CloudConfig.SUPABASE_URL}/rest/v1/prayers"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", CloudConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${CloudConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
                connectTimeout = 8000
                readTimeout = 8000
            }

            val payload = JSONObject().apply {
                put("id", prayer.id)
                put("sender_name", prayer.senderName)
                put("location", prayer.location)
                put("prayer_text", prayer.prayerText)
                put("timestamp_label", prayer.timestamp)
                put("aamiin_count", prayer.aamiinCount)
                put("like_count", prayer.likeCount)
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Mengirimkan pertambahan jumlah Aamiin ke Cloud
     */
    suspend fun incrementAamiinInCloud(prayerId: String, newCount: Int): Boolean = withContext(Dispatchers.IO) {
        if (!CloudConfig.isCloudEnabled) return@withContext false

        try {
            val endpoint = "${CloudConfig.SUPABASE_URL}/rest/v1/prayers?id=eq.$prayerId"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PATCH"
                setRequestProperty("apikey", CloudConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${CloudConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 8000
                readTimeout = 8000
            }

            val payload = JSONObject().apply {
                put("aamiin_count", newCount)
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        }
    }
}
