package com.example.doaharianmuslimin.data.model

data class MushafWord(
    val id: Int,
    val position: Int,
    val lineNumber: Int,
    val charTypeName: String, // "word" or "end" (ayat marker)
    val textUthmani: String,
    val verseKey: String,     // e.g. "2:255"
    val surahNumber: Int,
    val verseNumber: Int
)

data class MushafLine(
    val lineNumber: Int,
    val words: List<MushafWord> = emptyList(),
    val isSurahHeader: Boolean = false,
    val surahNumber: Int = 0,
    val surahNameArabic: String = "",
    val surahNameLatin: String = "",
    val isBismillah: Boolean = false
)

data class MushafPageData(
    val pageNumber: Int,
    val juzNumber: Int,
    val mainSurahName: String,
    val mainSurahNumber: Int,
    val lines: List<MushafLine>, // Max 15 lines per standard Mushaf page
    val versesMap: Map<String, QuranAyat> = emptyMap() // verseKey -> QuranAyat (translation, tafsir, audio)
)
