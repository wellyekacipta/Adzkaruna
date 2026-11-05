package com.example.doaharianmuslimin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.model.QuranDayStat
import com.example.doaharianmuslimin.data.model.QuranWeeklyStats
import com.example.doaharianmuslimin.ui.theme.AccentGold

@Composable
fun QuranWeeklyStatsCard(
    weeklyStats: QuranWeeklyStats,
    onNavigateToQuran: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val todayStat = weeklyStats.days.find { it.isToday } ?: weeklyStats.days.firstOrNull()
    var selectedDayDetail by remember(weeklyStats) { mutableStateOf<QuranDayStat?>(null) }
    val activeDay = selectedDayDetail ?: todayStat

    // Skala tinggi grafik batang
    val maxPages = (weeklyStats.days.maxOfOrNull { it.pagesRead } ?: 0).coerceAtLeast(1)
    val chartMaxHeight = 90.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header Card: Judul & Badge Keaktifan Mingguan (X/7 Hari Aktif)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Statistik Membaca Al-Qur'an",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Senin - Ahad",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Teks Ringkas Status Membaca (Contoh: "Rabu sudah membaca 15 halaman")
            val statusText = when {
                activeDay != null && activeDay.pagesRead > 0 -> "${activeDay.dayName} sudah membaca ${activeDay.pagesRead} halaman"
                activeDay != null && activeDay.isToday -> "${activeDay.dayName} belum membaca Al-Qur'an hari ini"
                activeDay != null && activeDay.isFuture -> "${activeDay.dayName} belum tiba"
                activeDay != null -> "${activeDay.dayName} membaca 0 halaman"
                else -> "Belum ada catatan membaca"
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (activeDay != null && activeDay.pagesRead > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Visualisasi Grafik Batang (Bar Chart) 7 Hari (Senin — Ahad)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyStats.days.forEach { dayStat ->
                    val isSelected = selectedDayDetail?.dayIndex == dayStat.dayIndex || (selectedDayDetail == null && dayStat.isToday)
                    val fraction = if (maxPages > 0 && dayStat.pagesRead > 0) {
                        (dayStat.pagesRead.toFloat() / maxPages.toFloat()).coerceIn(0.12f, 1f)
                    } else 0f

                    BarChartColumn(
                        dayStat = dayStat,
                        fraction = fraction,
                        maxHeight = chartMaxHeight,
                        isSelected = isSelected,
                        onClick = {
                            selectedDayDetail = dayStat
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BarChartColumn(
    dayStat: QuranDayStat,
    fraction: Float,
    maxHeight: androidx.compose.ui.unit.Dp,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val barColor = when {
        dayStat.isToday -> MaterialTheme.colorScheme.primary
        dayStat.pagesRead > 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        else -> Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Angka Halaman di Atas Batang
        Text(
            text = if (dayStat.pagesRead > 0) "${dayStat.pagesRead}" else "-",
            fontSize = 10.sp,
            fontWeight = if (isSelected || dayStat.isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (dayStat.pagesRead > 0)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Batang Vertikal (Bar)
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(maxHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (fraction > 0f) {
                val filledHeight = (maxHeight.value * fraction).dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(filledHeight)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(barColor)
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(AccentGold.copy(alpha = 0.15f))
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Label Nama Hari (Sen, Sel, Rab, Kam, Jum, Sab, Ahd)
        Text(
            text = dayStat.shortName,
            fontSize = 11.sp,
            fontWeight = if (dayStat.isToday || isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (dayStat.isToday)
                MaterialTheme.colorScheme.primary
            else if (isSelected)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Indikator Titik Hari Ini
        if (dayStat.isToday) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
