package com.example.doaharianmuslimin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.model.ArabicFontStyle

// 1. Font Rasm Utsmani (Khat Naskhi khas Madinah)
val RasmUtsmaniFontFamily = FontFamily(
    Font(R.font.uthmanic, FontWeight.Normal)
)

// 2. Font KFGQPC Uthman Taha Naskh (Mushaf Madinah V2)
val KfgqpcFontFamily = FontFamily(
    Font(R.font.kfgqpc_hafs, FontWeight.Normal)
)

// 3. Font Isep Misbah (LPMQ Kemenag RI Standar Indonesia)
val IsepMisbahFontFamily = FontFamily(
    Font(R.font.lpmq, FontWeight.Normal),
    Font(R.font.lpmq, FontWeight.Bold),
    Font(R.font.lpmq, FontWeight.SemiBold)
)

// Alias kompatibilitas
val UthmanicFontFamily = RasmUtsmaniFontFamily
val MadinahFontFamily = KfgqpcFontFamily
val LpmqFontFamily = IsepMisbahFontFamily

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)
