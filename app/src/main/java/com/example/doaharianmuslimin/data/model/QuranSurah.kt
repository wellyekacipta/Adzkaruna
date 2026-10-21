package com.example.doaharianmuslimin.data.model

data class QuranAyat(
    val number: Int,
    val arabic: String,
    val latin: String,
    val translation: String,
    val audioUrl: String = "",
    val tafsir: String = ""
)

data class QuranSurah(
    val number: Int,
    val name: String,
    val arabicName: String,
    val englishMeaning: String,
    val totalAyat: Int,
    val revelation: String,
    val ayats: List<QuranAyat>
)
