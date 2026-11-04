package com.example.doaharianmuslimin.data

import android.content.Context
import android.util.Log
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
        private const val TAG = "CommunityPrayerRepo"
        private const val KEY_PRAYERS_JSON = "prayers_json_list"
    }

    // Doa awal resmi (Arif & Sitti) dengan timestamp asli Oktober 2026
    private val initialPrayers = listOf(
        CommunityPrayer(
            id = "arif_01",
            senderName = "Arif",
            location = "Makassar",
            prayerText = "Ya Allah, lancarkanlah ikhtiar rezeki dan pekerjaan kami sekeluarga, serta berikan kesehatan dan keberkahan untuk kedua orang tua kami. Aamiin.",
            timestamp = "21 Okt 2026, 14:30 WIB",
            createdAt = 1792567800000L,
            aamiinCount = 28,
            likeCount = 0
        ),
        CommunityPrayer(
            id = "sitti_01",
            senderName = "Sitti",
            location = "Gowa",
            prayerText = "Ya Rabb, kuatkanlah iman dan kesabaran kami dalam menghadapi cobaan hidup, berkahilah rumah tangga kami dan mudahkan segala urusan kami. Aamiin.",
            timestamp = "19 Okt 2026, 09:15 WIB",
            createdAt = 1792376100000L,
            aamiinCount = 45,
            likeCount = 0
        )
    )

    private val _prayersFlow = MutableStateFlow<List<CommunityPrayer>>(emptyList())
    val prayersFlow: StateFlow<List<CommunityPrayer>> = _prayersFlow.asStateFlow()

    init {
        val loaded = loadPrayersFromStorage()
        _prayersFlow.value = loaded
        syncWithCloud()
        initFirebaseRealtime()
    }

    private fun formatTimestampFromMillis(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
        return sdf.format(Date(millis))
    }

    private fun isDummyPrayer(prayer: CommunityPrayer): Boolean {
        return prayer.id.startsWith("sim_") || prayer.id.startsWith("mock_")
    }

    private fun initFirebaseRealtime() {
        try {
            FirebasePrayerService.ensureAnonymousAuth()
            FirebasePrayerService.listenToPrayersRealtime { remotePrayers ->
                if (remotePrayers.isNotEmpty()) {
                    val valid = remotePrayers.filterNot { isDummyPrayer(it) }
                    if (valid.isNotEmpty()) {
                        mergeAndSavePrayers(valid)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal inisialisasi Firebase Realtime", e)
        }
    }

    private fun syncWithCloud() {
        scope.launch {
            if (CloudConfig.isCloudEnabled) {
                try {
                    val remote = CloudPrayerSync.fetchPrayersFromCloud()
                    if (!remote.isNullOrEmpty()) {
                        val valid = remote.filterNot { isDummyPrayer(it) }
                        if (valid.isNotEmpty()) {
                            mergeAndSavePrayers(valid)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal sync dengan Cloud", e)
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

                // Hanya bersihkan ID dummy simulator jika ada
                if (id.startsWith("sim_") || id.startsWith("mock_")) {
                    continue
                }

                val rawTimestamp = obj.optString("timestamp", "")
                val savedCreatedAt = obj.optLong("createdAt", 0L)
                val createdAt = if (savedCreatedAt > 0L) {
                    savedCreatedAt
                } else {
                    CommunityPrayer.parseTimestampToMillis(rawTimestamp, id) ?: System.currentTimeMillis()
                }

                list.add(
                    CommunityPrayer(
                        id = id,
                        senderName = sender,
                        location = obj.optString("location", "Indonesia"),
                        prayerText = obj.optString("prayerText", ""),
                        timestamp = rawTimestamp.ifBlank { formatTimestampFromMillis(createdAt) },
                        createdAt = createdAt,
                        aamiinCount = obj.optInt("aamiinCount", 1),
                        likeCount = obj.optInt("likeCount", 0),
                        isAamiined = obj.optBoolean("isAamiined", false),
                        isLiked = obj.optBoolean("isLiked", false)
                    )
                )
            }

            // Pastikan Arif dan Sitti selalu tersedia
            val hasArif = list.any { it.id == "arif_01" || it.senderName.equals("Arif", ignoreCase = true) }
            val hasSitti = list.any { it.id == "sitti_01" || it.senderName.equals("Sitti", ignoreCase = true) }
            if (!hasArif) list.add(initialPrayers[0])
            if (!hasSitti) list.add(initialPrayers[1])

            val sorted = list.distinctBy { it.id }.sortedByDescending { it.createdAt }
            savePrayers(sorted)
            sorted
        } catch (_: Exception) {
            initialPrayers
        }
    }

    fun getPrayers(): List<CommunityPrayer> {
        return _prayersFlow.value.ifEmpty { loadPrayersFromStorage() }
    }

    private fun savePrayers(list: List<CommunityPrayer>) {
        val distinctList = list.distinctBy { it.id }.sortedByDescending { it.createdAt }
        val array = JSONArray()
        for (item in distinctList) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("senderName", item.senderName)
                put("location", item.location)
                put("prayerText", item.prayerText)
                put("timestamp", item.timestamp)
                put("createdAt", item.createdAt)
                put("aamiinCount", item.aamiinCount)
                put("likeCount", item.likeCount)
                put("isAamiined", item.isAamiined)
                put("isLiked", item.isLiked)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PRAYERS_JSON, array.toString()).apply()
        _prayersFlow.value = distinctList
    }

    /**
     * Menggabungkan (merge) doa dari remote/Firebase dengan doa lokal.
     * Tidak pernah menghapus doa buatan user yang sudah tersimpan secara lokal.
     */
    private fun mergeAndSavePrayers(remotePrayers: List<CommunityPrayer>) {
        if (remotePrayers.isEmpty()) return
        val currentLocal = getPrayers().toMutableList()
        val mergedMap = LinkedHashMap<String, CommunityPrayer>()

        // 1. Simpan semua doa lokal terlebih dahulu
        for (local in currentLocal) {
            mergedMap[local.id] = local
        }

        // 2. Gabungkan doa dari remote
        for (remote in remotePrayers) {
            if (isDummyPrayer(remote)) continue
            val existing = mergedMap[remote.id]
            if (existing != null) {
                // Pertahankan status aamiin lokal user dan perbarui jumlah counter
                mergedMap[remote.id] = remote.copy(
                    isAamiined = existing.isAamiined,
                    aamiinCount = maxOf(existing.aamiinCount, remote.aamiinCount),
                    createdAt = if (existing.createdAt > 0L) existing.createdAt else remote.createdAt
                )
            } else {
                mergedMap[remote.id] = remote
            }
        }

        // Pastikan doa pembuka Arif & Sitti selalu ada
        if (!mergedMap.containsKey("arif_01")) mergedMap["arif_01"] = initialPrayers[0]
        if (!mergedMap.containsKey("sitti_01")) mergedMap["sitti_01"] = initialPrayers[1]

        val sorted = mergedMap.values.distinctBy { it.id }.sortedByDescending { it.createdAt }
        savePrayers(sorted)
    }

    /**
     * Menambahkan doa baru dari pengguna
     */
    fun addPrayer(name: String, location: String, text: String): CommunityPrayer {
        val current = getPrayers().toMutableList()
        val nowMillis = System.currentTimeMillis()
        val nowFormatted = formatTimestampFromMillis(nowMillis)
        val newPrayer = CommunityPrayer(
            id = nowMillis.toString(),
            senderName = if (name.isBlank()) "Hamba Allah" else name.trim(),
            location = if (location.isBlank()) "Indonesia" else location.trim(),
            prayerText = text.trim(),
            timestamp = nowFormatted,
            createdAt = nowMillis,
            aamiinCount = 1,
            likeCount = 0,
            isAamiined = true
        )
        // Tambahkan di awal daftar (paling baru)
        current.add(0, newPrayer)
        savePrayers(current)

        // Kirimkan ke Firebase & Cloud (asinkron tanpa menghalangi tampilan lokal)
        try {
            FirebasePrayerService.sendPrayer(newPrayer)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal kirim doa baru ke Firebase", e)
        }
        scope.launch {
            try {
                CloudPrayerSync.postPrayerToCloud(newPrayer)
            } catch (_: Exception) {}
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
                try {
                    CloudPrayerSync.incrementAamiinInCloud(updatedItem.id, updatedItem.aamiinCount)
                } catch (_: Exception) {}
            }
        }
        return current
    }

    fun toggleLike(prayerId: String): List<CommunityPrayer> {
        return getPrayers()
    }
}
