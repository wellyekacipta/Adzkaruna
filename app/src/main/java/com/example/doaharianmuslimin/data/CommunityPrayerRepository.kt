package com.example.doaharianmuslimin.data

import android.content.Context
import com.example.doaharianmuslimin.data.model.CommunityPrayer
import org.json.JSONArray
import org.json.JSONObject

class CommunityPrayerRepository(context: Context) {
    private val prefs = context.getSharedPreferences("community_prayers_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PRAYERS_JSON = "prayers_json_list"
    }

    private val initialPrayers = listOf(
        CommunityPrayer(
            id = "1",
            senderName = "Hamba Allah",
            location = "Jakarta Selatan",
            prayerText = "Ya Allah, lancarkanlah ikhtiar rezeki dan pekerjaan kami sekeluarga, serta berikan kesembuhan untuk kedua orang tua kami yang sedang sakit. Aamiin.",
            timestamp = "10 menit yang lalu",
            aamiinCount = 142,
            likeCount = 38
        ),
        CommunityPrayer(
            id = "2",
            senderName = "Fathir & Keluarga",
            location = "Bandung",
            prayerText = "Mohon doanya sahabat muslim, anak kami sedang menghadapi ujian tahfidz dan sekolah. Semoga diberi kemudahan, ketenangan hati, dan ilmu yang barakah.",
            timestamp = "25 menit yang lalu",
            aamiinCount = 89,
            likeCount = 24
        ),
        CommunityPrayer(
            id = "3",
            senderName = "Siti Aisyah",
            location = "Surabaya",
            prayerText = "Ya Rabb, kuatkanlah iman dan kesabaran kami dalam menghadapi cobaan hidup, jauhkan dari kegelisahan hutang, dan berkahilah rumah tangga kami.",
            timestamp = "1 jam yang lalu",
            aamiinCount = 210,
            likeCount = 65
        )
    )

    fun getPrayers(): List<CommunityPrayer> {
        val jsonStr = prefs.getString(KEY_PRAYERS_JSON, null)
        if (jsonStr.isNullOrBlank()) {
            savePrayers(initialPrayers)
            return initialPrayers
        }
        return try {
            val list = mutableListOf<CommunityPrayer>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CommunityPrayer(
                        id = obj.getString("id"),
                        senderName = obj.getString("senderName"),
                        location = obj.getString("location"),
                        prayerText = obj.getString("prayerText"),
                        timestamp = obj.getString("timestamp"),
                        aamiinCount = obj.getInt("aamiinCount"),
                        likeCount = obj.getInt("likeCount"),
                        isAamiined = obj.optBoolean("isAamiined", false),
                        isLiked = obj.optBoolean("isLiked", false)
                    )
                )
            }
            list
        } catch (_: Exception) {
            initialPrayers
        }
    }

    private fun savePrayers(list: List<CommunityPrayer>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("senderName", item.senderName)
                put("location", item.location)
                put("prayerText", item.prayerText)
                put("timestamp", item.timestamp)
                put("aamiinCount", item.aamiinCount)
                put("likeCount", item.likeCount)
                put("isAamiined", item.isAamiined)
                put("isLiked", item.isLiked)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PRAYERS_JSON, array.toString()).apply()
    }

    fun addPrayer(name: String, location: String, text: String): CommunityPrayer {
        val current = getPrayers().toMutableList()
        val newPrayer = CommunityPrayer(
            id = System.currentTimeMillis().toString(),
            senderName = if (name.isBlank()) "Hamba Allah" else name.trim(),
            location = if (location.isBlank()) "Indonesia" else location.trim(),
            prayerText = text.trim(),
            timestamp = "Baru saja",
            aamiinCount = 1,
            likeCount = 0,
            isAamiined = true
        )
        current.add(0, newPrayer)
        savePrayers(current)
        return newPrayer
    }

    fun toggleAamiin(prayerId: String): List<CommunityPrayer> {
        val current = getPrayers().map {
            if (it.id == prayerId) {
                val newStatus = !it.isAamiined
                val newCount = if (newStatus) it.aamiinCount + 1 else maxOf(0, it.aamiinCount - 1)
                it.copy(isAamiined = newStatus, aamiinCount = newCount)
            } else it
        }
        savePrayers(current)
        return current
    }

    fun toggleLike(prayerId: String): List<CommunityPrayer> {
        val current = getPrayers().map {
            if (it.id == prayerId) {
                val newStatus = !it.isLiked
                val newCount = if (newStatus) it.likeCount + 1 else maxOf(0, it.likeCount - 1)
                it.copy(isLiked = newStatus, likeCount = newCount)
            } else it
        }
        savePrayers(current)
        return current
    }
}
