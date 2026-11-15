package com.example.doaharianmuslimin.data.model

data class QuoteItem(
    val id: String,
    val text: String,
    val source: String,
    val category: String = "HADIS",
    val isCustom: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
