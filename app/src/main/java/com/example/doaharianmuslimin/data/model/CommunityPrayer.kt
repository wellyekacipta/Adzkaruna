package com.example.doaharianmuslimin.data.model

data class CommunityPrayer(
    val id: String,
    val senderName: String,
    val location: String,
    val prayerText: String,
    val timestamp: String,
    var aamiinCount: Int = 0,
    var likeCount: Int = 0,
    var isAamiined: Boolean = false,
    var isLiked: Boolean = false
)
