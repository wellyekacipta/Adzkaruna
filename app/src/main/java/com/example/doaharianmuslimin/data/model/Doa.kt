package com.example.doaharianmuslimin.data.model

data class Doa(
    val id: Int,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val source: String,
    val category: DoaCategory,
    val note: String? = null,
    val repeatCount: Int = 1
)
