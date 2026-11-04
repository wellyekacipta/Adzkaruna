package com.example.doaharianmuslimin.data.model

data class QuranDayStat(
    val dayIndex: Int,          // 0 = Senin, 1 = Selasa, ..., 6 = Ahad
    val dayName: String,         // "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Ahad"
    val shortName: String,       // "Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Ahd"
    val dateDisplay: String,     // Contoh: "4 Nov"
    val fullDateString: String,  // Contoh: "2026-11-04"
    val pagesRead: Int,          // Jumlah halaman yang dibaca
    val surahs: List<String>,    // Daftar surat yang dibaca
    val isToday: Boolean,        // Apakah hari ini
    val isFuture: Boolean        // Apakah hari di masa depan
)

data class QuranWeeklyStats(
    val days: List<QuranDayStat>,
    val activeDaysCount: Int,
    val totalPagesRead: Int,
    val totalSurahsRead: Int,
    val currentStreak: Int
)
