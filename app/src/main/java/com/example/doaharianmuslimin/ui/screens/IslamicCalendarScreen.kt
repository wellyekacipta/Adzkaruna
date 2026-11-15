package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.IslamicDateHelper
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.TrackerRepository
import com.example.doaharianmuslimin.ui.theme.AccentGold
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { TrackerRepository(context) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val todayKey = remember { repository.getTodayDateKey() }

    var showPuasaDialog by remember { mutableStateOf(false) }
    val isFastingToday = remember(refreshKey) { repository.isPuasaChecked(todayKey) }
    val puasaTypeToday = remember(refreshKey) { repository.getPuasaType(todayKey) }

    // State navigasi bulan Hijriah
    var monthOffset by remember { mutableIntStateOf(0) } // 0 = Bulan Sekarang
    val hijriMonths = listOf(
        Pair("Jumadil Akhir 1448 H", "November – Desember 2026"),
        Pair("Rajab 1448 H", "Desember 2026 – Januari 2027"),
        Pair("Sya'ban 1448 H", "Januari – Februari 2027"),
        Pair("Ramadhan 1448 H", "Februari – Maret 2027")
    )
    val currentMonthIdx = (monthOffset).coerceIn(0, hijriMonths.size - 1)
    val currentMonthInfo = hijriMonths[currentMonthIdx]

    // Live countdown to Ramadan
    var countdownSeconds by remember { mutableIntStateOf(39) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (countdownSeconds > 0) countdownSeconds-- else countdownSeconds = 59
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kalender Islam",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Penanggalan Hijriah & Jadwal Puasa Sunnah",
                            fontSize = 9.5.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Month Header Selector
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (monthOffset > 0) monthOffset-- },
                        enabled = monthOffset > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentMonthInfo.first,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentMonthInfo.second,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { if (monthOffset < hijriMonths.size - 1) monthOffset++ },
                        enabled = monthOffset < hijriMonths.size - 1
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya")
                    }
                }
            }

            // Calendar Grid Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val weekDays = listOf("SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "MIN")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        weekDays.forEach { d ->
                            Text(
                                text = d,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val daysInMonth = if (currentMonthIdx == 1) 29 else 30
                    val startDayOffset = (currentMonthIdx * 2 + 3) % 7

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        var dayNum = 1
                        for (row in 0 until 5) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (col in 0 until 7) {
                                    val cellIdx = row * 7 + col
                                    if (cellIdx >= startDayOffset && dayNum <= daysInMonth) {
                                        val currentDay = dayNum
                                        val isAyyamulBidh = currentDay in 13..15
                                        val isHariIni = (currentMonthIdx == 0 && currentDay == 5)
                                        val isHariBesar = (currentDay == 27)

                                        val cellBg = when {
                                            isAyyamulBidh -> Color(0xFFFBE9E7)
                                            isHariBesar -> Color(0xFFFFECB3)
                                            else -> Color.Transparent
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(cellBg)
                                                .then(
                                                    if (isHariIni) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                                                    else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$currentDay",
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isAyyamulBidh || isHariIni || isHariBesar) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isHariIni) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${(currentDay + 9) % 31 + 1}",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }

                                            if (isAyyamulBidh) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(Color(0xFF7E57C2), CircleShape)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                            if (isHariBesar) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(Color(0xFFF4511E), CircleShape)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                        }
                                        dayNum++
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legend: Puasa/hari besar, Ayyamul Bidh, Hari ini
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendBadge(color = Color(0xFFF4511E), text = "Puasa/Hari Besar")
                        LegendBadge(color = Color(0xFF7E57C2), text = "Ayyamul Bidh")
                        LegendBadge(color = MaterialTheme.colorScheme.primary, text = "Hari Ini")
                    }
                }
            }

            // Card Event Puasa Hari Ini
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "JADWAL & CATATAN PUASA HARI INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isFastingToday) "Alhamdulillah, Sedang Berpuasa: $puasaTypeToday" else "Hari baik untuk menjaga ibadah sunnah & membaca Al-Qur'an",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showPuasaDialog = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isFastingToday) "Ubah Catatan Puasa" else "+ Catat Puasa Hari Ini", fontSize = 12.sp)
                    }
                }
            }

            // Card Countdown Menuju Ramadhan 1448 H
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MOMEN BERIKUTNYA",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Awal Ramadhan 1448 H",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CountdownBox(number = "84", label = "Hari")
                        CountdownBox(number = "02", label = "Jam")
                        CountdownBox(number = "17", label = "Menit")
                        CountdownBox(number = "$countdownSeconds", label = "Detik")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Perkiraan: Ahad, 7 Februari 2027",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Dialog Catat Puasa
    if (showPuasaDialog) {
        val puasaOptions = listOf(
            "Puasa Senin - Kamis",
            "Puasa Ayyamul Bidh",
            "Puasa Ramadhan",
            "Puasa Daud",
            "Puasa Qadha / Nadzar",
            "Puasa Sunnah Lainnya"
        )
        AlertDialog(
            onDismissRequest = { showPuasaDialog = false },
            title = { Text("Catat Puasa Hari Ini", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    puasaOptions.forEach { type ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    repository.setPuasaChecked(todayKey, true, type)
                                    refreshKey++
                                    showPuasaDialog = false
                                }
                        ) {
                            Text(
                                text = type,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (isFastingToday) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = {
                                repository.setPuasaChecked(todayKey, false, "")
                                refreshKey++
                                showPuasaDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Batalkan / Hapus Catatan Puasa Hari Ini", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPuasaDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun LegendBadge(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = text, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CountdownBox(number: String, label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.width(68.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
