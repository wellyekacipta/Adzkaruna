package com.example.doaharianmuslimin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.model.QuranDayStat
import com.example.doaharianmuslimin.data.model.QuranWeeklyStats
import com.example.doaharianmuslimin.ui.theme.AccentGold

@Composable
fun QuranWeeklyStatsCard(
    weeklyStats: QuranWeeklyStats,
    onNavigateToQuran: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayDetail by remember { mutableStateOf<QuranDayStat?>(null) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header Card: Judul & Badge Keaktifan Mingguan
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
                            text = "Aktivitas Mingguan (Senin — Ahad)",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Badge Ringkasan Keaktifan
                Surface(
                    color = if (weeklyStats.activeDaysCount > 0)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${weeklyStats.activeDaysCount}/7 Hari Aktif",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (weeklyStats.activeDaysCount > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Baris 7 Hari (Senin, Selasa, Rabu, Kamis, Jumat, Sabtu, Ahad)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weeklyStats.days.forEach { dayStat ->
                    DayStatColumn(
                        dayStat = dayStat,
                        isSelected = selectedDayDetail?.dayIndex == dayStat.dayIndex,
                        onClick = {
                            selectedDayDetail = if (selectedDayDetail?.dayIndex == dayStat.dayIndex) null else dayStat
                        }
                    )
                }
            }

            // 3. Detail interaktif jika salah satu hari diketuk
            AnimatedVisibility(
                visible = selectedDayDetail != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedDayDetail?.let { day ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (day.pagesRead > 0) {
                                    val surahText = if (day.surahs.isNotEmpty()) " (${day.surahs.joinToString(", ")})" else ""
                                    "${day.dayName}, ${day.dateDisplay}: Membaca ${day.pagesRead} halaman$surahText"
                                } else if (day.isToday) {
                                    "Hari ini belum membaca Al-Qur'an. Yuk tilawah sekarang!"
                                } else if (day.isFuture) {
                                    "${day.dayName}, ${day.dateDisplay}: Jadwal tilawah mendatang."
                                } else {
                                    "${day.dayName}, ${day.dateDisplay}: Tidak ada catatan membaca."
                                },
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Footer Info: Streak & Total Halaman Mingguan & Tombol Navigasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${weeklyStats.currentStreak} Hari Beruntun",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${weeklyStats.totalPagesRead} Hal minggu ini",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.clickable { onNavigateToQuran() }
                ) {
                    Text(
                        text = "Buka Al-Qur'an ›",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DayStatColumn(
    dayStat: QuranDayStat,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Nama Hari (Sen, Sel, Rab, Kam, Jum, Sab, Ahd)
        Text(
            text = dayStat.shortName,
            fontSize = 11.sp,
            fontWeight = if (dayStat.isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (dayStat.isToday)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Indikator Kotak/Bulatan Hari
        val hasRead = dayStat.pagesRead > 0
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = when {
                hasRead -> MaterialTheme.colorScheme.primary
                dayStat.isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            },
            border = when {
                dayStat.isToday -> BorderStroke(1.8.dp, MaterialTheme.colorScheme.primary)
                hasRead -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                isSelected -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                else -> null
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (hasRead) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Sudah Dibaca",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (dayStat.isToday) {
                    Text(
                        text = "–",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = if (dayStat.isFuture) "•" else "–",
                        fontSize = if (dayStat.isFuture) 10.sp else 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Keterangan Bawah (Hari Ini / Jumlah Halaman / Tanggal)
        if (dayStat.isToday) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Hari Ini",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        } else if (hasRead) {
            Text(
                text = "${dayStat.pagesRead} Hal",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = dayStat.dateDisplay,
                fontSize = 8.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
