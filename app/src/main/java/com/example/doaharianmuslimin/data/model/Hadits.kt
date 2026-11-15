package com.example.doaharianmuslimin.data.model

data class Hadits(
    val id: Int,
    val title: String,
    val narrator: String,
    val source: String,
    val category: String,
    val arabic: String,
    val latin: String = "",
    val translation: String,
    val faedah: String
)
