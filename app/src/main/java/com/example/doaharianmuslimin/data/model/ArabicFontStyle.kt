package com.example.doaharianmuslimin.data.model

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.doaharianmuslimin.R

/**
 * Pilihan Font Kaligrafi Teks Arab untuk Al-Qur'an per baris dan Doa-doa harian.
 *
 * 1. RASM_UTSMANI : Khat Naskhi khas Mushaf Al Madinah Standar Timur Tengah
 * 2. KFGQPC       : KFGQPC Uthman Taha Naskh (Percetakan Al-Qur'an Raja Fahd)
 * 3. ISEP_MISBAH  : Khat Mushaf Standar Indonesia (LPMQ Kemenag RI oleh H. Isep Misbah)
 */
enum class ArabicFontStyle(
    val title: String,
    val subtitle: String,
    val sampleText: String
) {
    RASM_UTSMANI(
        title = "Rasm Utsmani (Khat Naskhi)",
        subtitle = "Khas Madinah Standar Timur Tengah",
        sampleText = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ"
    ),
    KFGQPC(
        title = "KFGQPC Uthman Taha Naskh",
        subtitle = "Font Resmi Percetakan Mushaf Raja Fahd Madinah",
        sampleText = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ"
    ),
    ISEP_MISBAH(
        title = "Isep Misbah",
        subtitle = "Khat Mushaf Standar Indonesia (LPMQ Kemenag RI)",
        sampleText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ"
    );

    val fontFamily: FontFamily
        get() = when (this) {
            RASM_UTSMANI -> FontFamily(Font(R.font.uthmanic, FontWeight.Normal))
            KFGQPC -> FontFamily(Font(R.font.kfgqpc_hafs, FontWeight.Normal))
            ISEP_MISBAH -> FontFamily(
                Font(R.font.lpmq, FontWeight.Normal),
                Font(R.font.lpmq, FontWeight.Bold),
                Font(R.font.lpmq, FontWeight.SemiBold)
            )
        }
}
