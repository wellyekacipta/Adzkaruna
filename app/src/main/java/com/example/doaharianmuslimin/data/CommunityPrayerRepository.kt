package com.example.doaharianmuslimin.data

import android.content.Context
import com.example.doaharianmuslimin.data.model.CommunityPrayer
import com.example.doaharianmuslimin.data.remote.CloudConfig
import com.example.doaharianmuslimin.data.remote.CloudPrayerSync
import com.example.doaharianmuslimin.data.remote.FirebasePrayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityPrayerRepository(context: Context) {
    private val prefs = context.getSharedPreferences("community_prayers_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        private const val KEY_PRAYERS_JSON = "prayers_json_list"
    }

    // Tanggal dan waktu asli postingan doa dari Arif dan Sitti (Jam, Waktu & Tanggal)
    private val initialPrayers = listOf(
        CommunityPrayer(
            id = "arif_01",
            senderName = "Arif",
            location = "Makassar",
            prayerText = "Ya Allah, lancarkanlah ikhtiar rezeki dan pekerjaan kami sekeluarga, serta berikan kesehatan dan keberkahan untuk kedua orang tua kami. Aamiin.",
            timestamp = "21 Okt 2026, 14:30 WIB",
            aamiinCount = 28,
            likeCount = 0
        ),
        CommunityPrayer(
            id = "sitti_01",
            senderName = "Sitti",
            location = "Gowa",
            prayerText = "Ya Rabb, kuatkanlah iman dan kesabaran kami dalam menghadapi cobaan hidup, berkahilah rumah tangga kami dan mudahkan segala urusan kami. Aamiin.",
            timestamp = "19 Okt 2026, 09:15 WIB",
            aamiinCount = 45,
            likeCount = 0
        )
    )

    private val fakeNamesToFilter = setOf(
        "Hamba Allah", "Fathir & Keluarga", "Siti Aisyah",
        "Ahmad Rizky", "Nurul Hidayah", "Rahmat & Dian"
    )

    private val _prayersFlow = MutableStateFlow<List<CommunityPrayer>>(emptyList())
    val prayersFlow: StateFlow<List<CommunityPrayer>> = _prayersFlow.asStateFlow()

    init {
        val loaded = loadPrayersFromStorage()
        _prayersFlow.value = loaded
        syncWithCloud()
        initFirebaseRealtime()
    }

    private fun formatCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
        return sdf.format(Date())
    }

    private fun formatTimestampFromMillis(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
        return sdf.format(Date(millis))
    }

    private fun initFirebaseRealtime() {
        try {
            FirebasePrayerService.ensureAnonymousAuth()
            FirebasePrayerService.listenToPrayersRealtime { remotePrayers ->
                if (remotePrayers.isNotEmpty()) {
                    // Filter agar hanya memuat doa asli
                    val valid = remotePrayers.filterNot { it.id.startsWith("sim_") || it.senderName in fakeNamesToFilter }
                    if (valid.isNotEmpty()) {
                        savePrayers(valid)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun syncWithCloud() {
        scope.launch {
            if (CloudConfig.isCloudEnabled) {
                val remote = CloudPrayerSync.fetchPrayersFromCloud()
                if (!remote.isNullOrEmpty()) {
                    val valid = remote.filterNot { it.id.startsWith("sim_") || it.senderName in fakeNamesToFilter }
                    if (valid.isNotEmpty()) {
                        savePrayers(valid)
                    }
                }
            }
        }
    }

    private fun loadPrayersFromStorage(): List<CommunityPrayer> {
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
                val id = obj.getString("id")
                val sender = obj.getString("senderName")

                // Bersihkan nama-nama tiruan atau dummy yang bukan dibuat user
                if (id.startsWith("sim_") || (id in listOf("1", "2", "3") && sender in fakeNamesToFilter)) {
                    continue
                }

                var rawTimestamp = obj.optString("timestamp", "")
                // Normalisasi timestamp lama agar memiliki jam, waktu, dan tanggal presisi
                if (rawTimestamp.isBlank() || rawTimestamp == "Baru saja" || rawTimestamp.contains("hari yang lalu")) {
                    rawTimestamp = when {
                        sender.equals("Arif", ignoreCase = true) -> "21 Okt 2026, 14:30 WIB"
                        sender.equals("Sitti", ignoreCase = true) -> "19 Okt 2026, 09:15 WIB"
                        id.toLongOrNull() != null -> formatTimestampFromMillis(id.toLong())
                        else -> formatCurrentTimestamp()
                    }
                }

                list.add(
                    CommunityPrayer(
                        id = id,
                        senderName = sender,
                        location = obj.getString("location"),
                        prayerText = obj.getString("prayerText"),
                        timestamp = rawTimestamp,
                        aamiinCount = obj.getInt("aamiinCount"),
                        likeCount = obj.optInt("likeCount", 0),
                        isAamiined = obj.optBoolean("isAamiined", false),
                        isLiked = obj.optBoolean("isLiked", false)
                    )
                )
            }

            // Pastikan Arif dan Sitti selalu ada
            val hasArif = list.any { it.senderName.equals("Arif", ignoreCase = true) }
            val hasSitti = list.any { it.senderName.equals("Sitti", ignoreCase = true) }
            if (!hasArif) list.add(0, initialPrayers[0])
            if (!hasSitti) list.add(if (list.size > 1) 1 else 0, initialPrayers[1])

            savePrayers(list)
            list
        } catch (_: Exception) {
            initialPrayers
        }
    }

    fun getPrayers(): List<CommunityPrayer> {
        return _prayersFlow.value.ifEmpty { loadPrayersFromStorage() }
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
        _prayersFlow.value = list
    }

    fun addPrayer(name: String, location: String, text: String): CommunityPrayer {
        val current = getPrayers().toMutableList()
        val nowFormatted = formatCurrentTimestamp()
        val newPrayer = CommunityPrayer(
            id = System.currentTimeMillis().toString(),
            senderName = if (name.isBlank()) "Hamba Allah" else name.trim(),
            location = if (location.isBlank()) "Indonesia" else location.trim(),
            prayerText = text.trim(),
            timestamp = nowFormatted,
            aamiinCount = 1,
            likeCount = 0,
            isAamiined = true
        )
        current.add(0, newPrayer)
        savePrayers(current)
        try {
            FirebasePrayerService.sendPrayer(newPrayer)
        } catch (_: Exception) {}
        scope.launch {
            CloudPrayerSync.postPrayerToCloud(newPrayer)
        }
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
        current.find { it.id == prayerId }?.let { updatedItem ->
            try {
                if (updatedItem.isAamiined) {
                    FirebasePrayerService.incrementAamiin(prayerId)
                } else {
                    FirebasePrayerService.decrementAamiin(prayerId)
                }
            } catch (_: Exception) {}
            scope.launch {
                CloudPrayerSync.incrementAamiinInCloud(updatedItem.id, updatedItem.aamiinCount)
            }
        }
        return current
    }

    fun toggleLike(prayerId: String): List<CommunityPrayer> {
        return getPrayers()
    }
}
