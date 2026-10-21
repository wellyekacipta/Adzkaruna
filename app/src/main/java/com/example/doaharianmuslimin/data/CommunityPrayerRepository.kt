package com.example.doaharianmuslimin.data

import android.content.Context
import com.example.doaharianmuslimin.data.model.CommunityPrayer
import com.example.doaharianmuslimin.data.remote.CloudConfig
import com.example.doaharianmuslimin.data.remote.CloudPrayerSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CommunityPrayerRepository(context: Context) {
    private val prefs = context.getSharedPreferences("community_prayers_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Default)

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

    private val _prayersFlow = MutableStateFlow<List<CommunityPrayer>>(emptyList())
    val prayersFlow: StateFlow<List<CommunityPrayer>> = _prayersFlow.asStateFlow()

    init {
        val loaded = loadPrayersFromStorage()
        _prayersFlow.value = loaded
        startRealtimeSimulation()
        syncWithCloud()
    }

    private fun syncWithCloud() {
        scope.launch {
            if (CloudConfig.isCloudEnabled) {
                val remote = CloudPrayerSync.fetchPrayersFromCloud()
                if (!remote.isNullOrEmpty()) {
                    savePrayers(remote)
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
        // Masukkan langsung ke urutan paling atas secara realtime
        current.add(0, newPrayer)
        savePrayers(current)
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
            scope.launch {
                CloudPrayerSync.incrementAamiinInCloud(updatedItem.id, updatedItem.aamiinCount)
            }
        }
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

    /**
     * Simulasi Realtime: menambahkan doa baru atau menambah aamiin sesekali
     * agar feed doa terasa hidup dan langsung update otomatis saat aplikasi berjalan.
     */
    private fun startRealtimeSimulation() {
        val incomingTemplates = listOf(
            Triple("Ahmad Rizky", "Makassar", "Bismillah, mohon doa restu sahabat muslimin, esok hari saya akan menempuh wawancara kerja pertama setelah 8 bulan menanti. Semoga Allah takdirkan yang terbaik."),
            Triple("Nurul Hidayah", "Yogyakarta", "Ya Allah, karuniakanlah keturunan yang shalih dan shalihah kepada kami yang telah 5 tahun menanti buah hati. Aamiin ya Mujibassailin."),
            Triple("Rahmat & Dian", "Medan", "Mohon kiriman doa untuk kesembuhan ayahanda kami yang sedang dirawat di ruang ICU. Semoga Allah angkat segala penyakitnya.")
        )

        scope.launch {
            delay(45_000) // 45 detik setelah aplikasi dibuka
            var templateIndex = 0
            while (true) {
                val current = getPrayers().toMutableList()
                if (templateIndex < incomingTemplates.size) {
                    val (name, loc, txt) = incomingTemplates[templateIndex]
                    // Cek apakah doa ini belum ada di list
                    if (current.none { it.prayerText == txt }) {
                        val simulatedPrayer = CommunityPrayer(
                            id = "sim_${System.currentTimeMillis()}",
                            senderName = name,
                            location = loc,
                            prayerText = txt,
                            timestamp = "Baru saja",
                            aamiinCount = (10..35).random(),
                            likeCount = (3..12).random()
                        )
                        current.add(0, simulatedPrayer)
                        savePrayers(current)
                    }
                    templateIndex++
                } else {
                    // Update jumlah aamiin secara acak pada doa yang sudah ada
                    if (current.isNotEmpty()) {
                        val randomIndex = (0 until minOf(4, current.size)).random()
                        val target = current[randomIndex]
                        current[randomIndex] = target.copy(aamiinCount = target.aamiinCount + (1..3).random())
                        savePrayers(current)
                    }
                }
                delay(90_000) // Setiap 1.5 menit ada update realtime
            }
        }
    }
}
