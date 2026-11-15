package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.IslamicDateHelper
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.TrackerRepository
import com.example.doaharianmuslimin.data.model.PrayerCheckItem
import com.example.doaharianmuslimin.data.model.PrayerCheckStatus
import com.example.doaharianmuslimin.ui.theme.AccentGold
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class TrackerTab(val title: String) {
    TILAWAH("Ngaji"),
    SHOLAT("Salat"),
    SUNNAH("Tahajud & Dhuha"),
    DZIKIR("Dzikir"),
    PUASA("Kalender Islam")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    onBack: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToTasbih: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { TrackerRepository(context) }
    val prefs = remember { PreferencesManager(context) }

    var selectedTab by remember { mutableStateOf(TrackerTab.TILAWAH) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val currentStreak = remember(refreshKey) { repository.getCurrentStreak() }
    val todayKey = remember { repository.getTodayDateKey() }

    // Dialog state
    var showLogNgajiDialog by remember { mutableStateOf(false) }
    var showPrayerStatusDialog by remember { mutableStateOf<PrayerCheckItem?>(null) }
    var showPuasaDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == TrackerTab.PUASA) "Kalender Islam" else "Tracker",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Flame Streak Badge (matching UI screenshot)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFECE0),
                        border = BorderStroke(1.dp, Color(0xFFFFCC80)),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$currentStreak hari",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD84315)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ==================== HORIZONTAL TAB PILLS ====================
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                indicator = {},
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                TrackerTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { selectedTab = tab }
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // ==================== TAB CONTENT ====================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTab) {
                    TrackerTab.TILAWAH -> {
                        TilawahTabContent(
                            repository = repository,
                            refreshKey = refreshKey,
                            onLogNgajiClick = { showLogNgajiDialog = true },
                            onNavigateToQuran = onNavigateToQuran
                        )
                    }
                    TrackerTab.SHOLAT -> {
                        SholatTabContent(
                            repository = repository,
                            refreshKey = refreshKey,
                            onSelectPrayer = { showPrayerStatusDialog = it }
                        )
                    }
                    TrackerTab.SUNNAH -> {
                        SunnahTabContent(
                            repository = repository,
                            todayKey = todayKey,
                            refreshKey = refreshKey,
                            onDataChanged = { refreshKey++ }
                        )
                    }
                    TrackerTab.DZIKIR -> {
                        DzikirTabContent(
                            repository = repository,
                            todayKey = todayKey,
                            refreshKey = refreshKey,
                            onNavigateToTasbih = onNavigateToTasbih,
                            onDataChanged = { refreshKey++ }
                        )
                    }
                    TrackerTab.PUASA -> {
                        KalenderIslamTabContent(
                            repository = repository,
                            refreshKey = refreshKey,
                            onRecordPuasaClick = { showPuasaDialog = true }
                        )
                    }
                }
            }
        }
    }

    // ==================== DIALOG: CATAT SESI NGAJI ====================
    if (showLogNgajiDialog) {
        var pagesInput by remember { mutableStateOf("5") }
        AlertDialog(
            onDismissRequest = { showLogNgajiDialog = false },
            title = { Text("Catat Sesi Ngaji", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Berapa halaman Al-Qur'an yang telah Anda baca hari ini?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = pagesInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) pagesInput = it },
                        label = { Text("Jumlah Halaman") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 5, 10, 20).forEach { count ->
                            OutlinedButton(
                                onClick = { pagesInput = count.toString() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+$count", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = pagesInput.toIntOrNull() ?: 1
                        repository.addTilawahPages(count)
                        refreshKey++
                        showLogNgajiDialog = false
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogNgajiDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // ==================== DIALOG: UBAH STATUS SHOLAT ====================
    if (showPrayerStatusDialog != null) {
        val prayer = showPrayerStatusDialog!!
        AlertDialog(
            onDismissRequest = { showPrayerStatusDialog = null },
            title = { Text("Status Salat ${prayer.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        PrayerCheckStatus.TEPAT_WAKTU to Color(0xFF4CAF50),
                        PrayerCheckStatus.BERJAMAAH to Color(0xFF8E24AA),
                        PrayerCheckStatus.TERLAMBAT to Color(0xFFF57C00),
                        PrayerCheckStatus.TERLEWAT to Color(0xFF757575),
                        PrayerCheckStatus.BELUM to MaterialTheme.colorScheme.outline
                    ).forEach { (status, color) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (prayer.status == status) color.copy(alpha = 0.15f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (prayer.status == status) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    repository.setPrayerStatus(todayKey, prayer.id, status)
                                    refreshKey++
                                    showPrayerStatusDialog = null
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = status.label,
                                    fontWeight = if (prayer.status == status) FontWeight.Bold else FontWeight.Medium,
                                    color = if (prayer.status == status) color else MaterialTheme.colorScheme.onSurface
                                )
                                if (prayer.status == status) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = color)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ==================== DIALOG: CATAT PUASA ====================
    if (showPuasaDialog) {
        AlertDialog(
            onDismissRequest = { showPuasaDialog = false },
            title = { Text("Catat Puasa Hari Ini", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Puasa Senin - Kamis",
                        "Puasa Ayyamul Bidh",
                        "Puasa Ramadhan",
                        "Puasa Daud",
                        "Puasa Qadha / Nadzar",
                        "Puasa Sunnah Lainnya"
                    ).forEach { type ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    repository.setPuasaChecked(todayKey, true, type)
                                    refreshKey++
                                    showPuasaDialog = false
                                }
                        ) {
                            Text(
                                text = type,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showPuasaDialog = false }) {
                    Text("Batal")
                }
            },
            confirmButton = {}
        )
    }
}

// ======================================================================================
// TAB 1: TILAWAH (NGAJI) - Sesuai Gambar 2
// ======================================================================================

@Composable
private fun TilawahTabContent(
    repository: TrackerRepository,
    refreshKey: Int,
    onLogNgajiClick: () -> Unit,
    onNavigateToQuran: () -> Unit
) {
    val targetPages = remember(refreshKey) { repository.getDailyTilawahTarget() }
    val todayPages = remember(refreshKey) { repository.getTodayTilawahPages() }
    val totalThisMonth = remember(refreshKey) { repository.getTotalTilawahPagesThisMonth() }

    val cal = remember { Calendar.getInstance() }
    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)
    val heatmap = remember(refreshKey) { repository.getMonthTilawahHeatmap(currentYear, currentMonth) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. KARTU TARGET HARIAN (Circular Ring)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Ring Progress
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = (todayPages.toFloat() / targetPages.toFloat()).coerceIn(0f, 1f)
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 12f
                        drawCircle(
                            color = Color(0xFFFFE0B2),
                            style = Stroke(stroke)
                        )
                        drawArc(
                            color = Color(0xFFFFA000),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(stroke, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = todayPages.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "/$targetPages",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "halaman",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Target harian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (todayPages >= targetPages) {
                            "Target hari ini tercapai — barakallahu fiik!"
                        } else {
                            "Kurang ${targetPages - todayPages} halaman lagi untuk mencapai target hari ini."
                        },
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onLogNgajiClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("+ Catat sesi ngaji", fontSize = 12.sp, color = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }

        // 2. KARTU KONSISTENSI BULANAN (HEATMAP GRID)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                val monthName = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")).format(cal.time)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konsistensi $monthName",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("bulan", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Days of week header (S S R K J S M)
                val daysOfWeek = listOf("S", "S", "R", "K", "J", "S", "M")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Heatmap Grid: 5 weeks of 7 days
                val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val firstDayOfWeek = run {
                    val c = Calendar.getInstance()
                    c.set(Calendar.DAY_OF_MONTH, 1)
                    val d = c.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
                    // Convert so Mon=0, Sun=6
                    if (d == Calendar.SUNDAY) 6 else d - 2
                }

                val primaryColor = MaterialTheme.colorScheme.primary

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    var dayCounter = 1
                    for (week in 0 until 5) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (dow in 0 until 7) {
                                val cellIndex = week * 7 + dow
                                if (cellIndex >= firstDayOfWeek && dayCounter <= totalDays) {
                                    val currentDay = dayCounter
                                    val pages = heatmap[currentDay] ?: 0
                                    val cellColor = when {
                                        pages >= 10 -> primaryColor
                                        pages >= 5 -> primaryColor.copy(alpha = 0.75f)
                                        pages >= 2 -> primaryColor.copy(alpha = 0.5f)
                                        pages >= 1 -> primaryColor.copy(alpha = 0.25f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(cellColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (currentDay == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .border(1.5.dp, Color(0xFFFFA000), RoundedCornerShape(6.dp))
                                            )
                                        }
                                    }
                                    dayCounter++
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sedikit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(0.15f, 0.35f, 0.65f, 1f).forEach { alpha ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(primaryColor.copy(alpha = alpha))
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Banyak", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pill Total Halaman
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = primaryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Total $totalThisMonth halaman bulan ini",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 3. KARTU PERJALANAN KHATAM
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perjalanan khatam",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Juz 1 dari 30",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { 1f / 30f },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Perkiraan khatam: 60 hari lagi dengan ritme sekarang",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ======================================================================================
// TAB 2: SHOLAT FARDHU - Sesuai Gambar 3
// ======================================================================================

@Composable
private fun SholatTabContent(
    repository: TrackerRepository,
    refreshKey: Int,
    onSelectPrayer: (PrayerCheckItem) -> Unit
) {
    val prayers = remember(refreshKey) { repository.getTodayPrayers() }
    val cal = remember { Calendar.getInstance() }
    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)
    val prayerStats = remember(refreshKey) { repository.getMonthPrayerStats(currentYear, currentMonth) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. KARTU KONSISTENSI SHOLAT (Heatmap & Badges)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                val monthName = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")).format(cal.time)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konsistensi $monthName",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("bulan", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary status pills (Tepat, Berjamaah, Terlambat, Terlewat)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tepatCount = prayerStats[PrayerCheckStatus.TEPAT_WAKTU] ?: 0
                    val berjamaahCount = prayerStats[PrayerCheckStatus.BERJAMAAH] ?: 0
                    val terlambatCount = prayerStats[PrayerCheckStatus.TERLAMBAT] ?: 0
                    val terlewatCount = prayerStats[PrayerCheckStatus.TERLEWAT] ?: 0

                    StatusCountPill("Tepat $tepatCount", Color(0xFFC8E6C9), Color(0xFF2E7D32), Modifier.weight(1f))
                    StatusCountPill("Berjamaah $berjamaahCount", Color(0xFFE1BEE7), Color(0xFF6A1B9A), Modifier.weight(1f))
                    StatusCountPill("Terlambat $terlambatCount", Color(0xFFFFE0B2), Color(0xFFE65100), Modifier.weight(1f))
                    StatusCountPill("Terlewat $terlewatCount", Color(0xFFEEEEEE), Color(0xFF616161), Modifier.weight(1f))
                }
            }
        }

        // 2. KARTU RIWAYAT & ABSEN SHOLAT HARI INI
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Riwayat salat hari ini",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    prayers.forEach { prayer ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectPrayer(prayer) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (prayer.name) {
                                                "Subuh" -> Icons.Default.WbTwilight
                                                "Dzuhur" -> Icons.Default.WbSunny
                                                "Ashar" -> Icons.Default.Brightness5
                                                "Maghrib" -> Icons.Default.NightsStay
                                                else -> Icons.Default.Bedtime
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = prayer.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${prayer.defaultTime} WIB",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Interactive Status Badge Pill
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (prayer.status) {
                                        PrayerCheckStatus.TEPAT_WAKTU -> Color(0xFFC8E6C9)
                                        PrayerCheckStatus.BERJAMAAH -> Color(0xFFE1BEE7)
                                        PrayerCheckStatus.TERLAMBAT -> Color(0xFFFFE0B2)
                                        PrayerCheckStatus.TERLEWAT -> Color(0xFFEEEEEE)
                                        PrayerCheckStatus.BELUM -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        if (prayer.status == PrayerCheckStatus.TEPAT_WAKTU || prayer.status == PrayerCheckStatus.BERJAMAAH) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF2E7D32))
                                            Spacer(modifier = Modifier.width(3.dp))
                                        }
                                        Text(
                                            text = prayer.status.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (prayer.status) {
                                                PrayerCheckStatus.TEPAT_WAKTU -> Color(0xFF1B5E20)
                                                PrayerCheckStatus.BERJAMAAH -> Color(0xFF4A148C)
                                                PrayerCheckStatus.TERLAMBAT -> Color(0xFFE65100)
                                                PrayerCheckStatus.TERLEWAT -> Color(0xFF424242)
                                                PrayerCheckStatus.BELUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCountPill(title: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

// ======================================================================================
// TAB 3: TAHAJUD & DHUHA (SHOLAT SUNNAH)
// ======================================================================================

@Composable
private fun SunnahTabContent(
    repository: TrackerRepository,
    todayKey: String,
    refreshKey: Int,
    onDataChanged: () -> Unit
) {
    val isTahajud = remember(refreshKey) { repository.isSunnahChecked(todayKey, "tahajud") }
    val tahajudRakaat = remember(refreshKey) { repository.getSunnahRakaat(todayKey, "tahajud") }

    val isDhuha = remember(refreshKey) { repository.isSunnahChecked(todayKey, "dhuha") }
    val dhuhaRakaat = remember(refreshKey) { repository.getSunnahRakaat(todayKey, "dhuha") }

    val isWitir = remember(refreshKey) { repository.isSunnahChecked(todayKey, "witir") }
    val isRawatib = remember(refreshKey) { repository.isSunnahChecked(todayKey, "rawatib") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Motivasi Sunnah
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "✨", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Keutamaan Qiyamul Lail & Dhuha",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "\"Salat yang paling utama setelah salat fardhu adalah salat malam (Tahajjud).\" (HR. Muslim)",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sholat Tahajjud Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Salat Tahajjud", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Sepertiga malam terakhir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isTahajud,
                        onCheckedChange = {
                            repository.setSunnahChecked(todayKey, "tahajud", it, tahajudRakaat)
                            onDataChanged()
                        }
                    )
                }

                if (isTahajud) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Jumlah Raka'at:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2, 4, 8, 11).forEach { r ->
                            val isSel = tahajudRakaat == r
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    repository.setSunnahChecked(todayKey, "tahajud", true, r)
                                    onDataChanged()
                                },
                                label = { Text("$r Raka'at") }
                            )
                        }
                    }
                }
            }
        }

        // Sholat Dhuha Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Salat Dhuha", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Waktu pagi menjelang siang", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isDhuha,
                        onCheckedChange = {
                            repository.setSunnahChecked(todayKey, "dhuha", it, dhuhaRakaat)
                            onDataChanged()
                        }
                    )
                }

                if (isDhuha) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Jumlah Raka'at:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2, 4, 8).forEach { r ->
                            val isSel = dhuhaRakaat == r
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    repository.setSunnahChecked(todayKey, "dhuha", true, r)
                                    onDataChanged()
                                },
                                label = { Text("$r Raka'at") }
                            )
                        }
                    }
                }
            }
        }

        // Sholat Sunnah Lainnya
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Sunnah Mu'akkadah Lainnya", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Salat Witir (Penutup Salat Malam)", fontSize = 13.sp)
                    Checkbox(
                        checked = isWitir,
                        onCheckedChange = {
                            repository.setSunnahChecked(todayKey, "witir", it)
                            onDataChanged()
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Salat Sunnah Rawatib (Qabliyah / Ba'diyah)", fontSize = 13.sp)
                    Checkbox(
                        checked = isRawatib,
                        onCheckedChange = {
                            repository.setSunnahChecked(todayKey, "rawatib", it)
                            onDataChanged()
                        }
                    )
                }
            }
        }
    }
}

// ======================================================================================
// TAB 4: DZIKIR
// ======================================================================================

@Composable
private fun DzikirTabContent(
    repository: TrackerRepository,
    todayKey: String,
    refreshKey: Int,
    onNavigateToTasbih: () -> Unit,
    onDataChanged: () -> Unit
) {
    val isPagi = remember(refreshKey) { repository.isDzikirChecked(todayKey, "pagi") }
    val isPetang = remember(refreshKey) { repository.isDzikirChecked(todayKey, "petang") }
    val isSholat = remember(refreshKey) { repository.isDzikirChecked(todayKey, "sholat") }
    val tasbihCount = remember(refreshKey) { repository.getTasbihCount(todayKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Tasbih Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tasbih & Istighfar Hari Ini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$tasbihCount",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("kali bacaan dzikir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            repository.addTasbihCount(todayKey, 33)
                            onDataChanged()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+33 Tasbih")
                    }
                    OutlinedButton(
                        onClick = onNavigateToTasbih,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Buka Tasbih Digital")
                    }
                }
            }
        }

        // Checklist Dzikir Rutin
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rutinitas Dzikir Harian", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                // Dzikir Pagi
                DzikirCheckRow(
                    title = "Dzikir Pagi",
                    subtitle = "Dibaca setelah Subuh hingga terbit matahari",
                    isChecked = isPagi,
                    onToggle = {
                        repository.setDzikirChecked(todayKey, "pagi", !isPagi)
                        onDataChanged()
                    }
                )

                // Dzikir Petang
                DzikirCheckRow(
                    title = "Dzikir Petang",
                    subtitle = "Dibaca setelah Ashar hingga terbenam matahari",
                    isChecked = isPetang,
                    onToggle = {
                        repository.setDzikirChecked(todayKey, "petang", !isPetang)
                        onDataChanged()
                    }
                )

                // Dzikir Setelah Salat
                DzikirCheckRow(
                    title = "Dzikir Setelah Salat Fardhu",
                    subtitle = "Istighfar, Tasbih, Tahmid, Takbir",
                    isChecked = isSholat,
                    onToggle = {
                        repository.setDzikirChecked(todayKey, "sholat", !isSholat)
                        onDataChanged()
                    }
                )
            }
        }
    }
}

@Composable
private fun DzikirCheckRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Checkbox(checked = isChecked, onCheckedChange = { onToggle() })
        }
    }
}

// ======================================================================================
// TAB 5: PUASA & KALENDER ISLAM - Sesuai Gambar 1
// ======================================================================================

@Composable
private fun KalenderIslamTabContent(
    repository: TrackerRepository,
    refreshKey: Int,
    onRecordPuasaClick: () -> Unit
) {
    val cal = remember { Calendar.getInstance() }
    val isFastingToday = remember(refreshKey) { repository.isPuasaChecked(repository.getTodayDateKey()) }
    val puasaTypeToday = remember(refreshKey) { repository.getPuasaType(repository.getTodayDateKey()) }

    // Live countdown to Ramadan
    var countdownText by remember { mutableStateOf(Triple(84, 2, 17)) } // days, hours, mins
    var countdownSeconds by remember { mutableIntStateOf(39) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (countdownSeconds > 0) {
                countdownSeconds--
            } else {
                countdownSeconds = 59
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Header Selector (Rajab 1448 H / 10 Des - 8 Jan)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Jumadil Akhir 1448 H",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "November – Desember 2026",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Calendar Grid Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header Days: SEN, SEL, RAB, KAM, JUM, SAB, MIN
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

                // Calendar Dates (Simulated month matching screenshot with Ayyamul Bidh on 13, 14, 15 & Hari Besar)
                val daysInMonth = 30
                val startDayOffset = 3 // starts on Thursday

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    var dayNum = 1
                    for (row in 0 until 5) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (col in 0 until 7) {
                                val cellIdx = row * 7 + col
                                if (cellIdx >= startDayOffset && dayNum <= daysInMonth) {
                                    val currentDay = dayNum
                                    val isAyyamulBidh = currentDay in 13..15
                                    val isHariIni = currentDay == 5
                                    val isHariBesar = currentDay == 27 // Isra Mi'raj

                                    val cellBg = when {
                                        isAyyamulBidh -> Color(0xFFFBE9E7)
                                        isHariBesar -> Color(0xFFFFECB3)
                                        else -> Color.Transparent
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(0.9f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(cellBg)
                                            .border(
                                                width = if (isHariIni) 1.5.dp else 0.dp,
                                                color = if (isHariIni) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.Top) {
                                                Text(
                                                    text = currentDay.toString(),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (isHariIni || isAyyamulBidh) FontWeight.Bold else FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isAyyamulBidh) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(Color(0xFF7E57C2), CircleShape)
                                                    )
                                                }
                                                if (isHariBesar) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(Color(0xFFF4511E), CircleShape)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${(currentDay + 9) % 30 + 1}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(Color(0xFFF4511E), "Puasa/hari besar")
                    LegendItem(Color(0xFF7E57C2), "Ayyamul Bidh")
                    LegendItem(MaterialTheme.colorScheme.primary, "Hari ini")
                }
            }
        }

        // Event Card (Hari ini)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "MINGGU • 5 JUMADIL AKHIR • 15 NOVEMBER",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBF360C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isFastingToday) "Sedang Berpuasa: $puasaTypeToday" else "Tidak ada event khusus",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hari baik untuk menjaga rutinitas salat, tilawah, dan memperbanyak istighfar.",
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRecordPuasaClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(if (isFastingToday) "Ubah Catatan Puasa" else "+ Catat Puasa Hari Ini", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Countdown Card ("MOMEN BERIKUTNYA")
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEEFB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFB3E5FC), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color(0xFF0277BD), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("MOMEN BERIKUTNYA", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                        Text("Awal Ramadhan 1448 H", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF01579B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4 Digit Boxes: Hari : Jam : Menit : Detik
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownBox(countdownText.first.toString(), "Hari", Modifier.weight(1f))
                    Text(":", fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                    CountdownBox(String.format(Locale.US, "%02d", countdownText.second), "Jam", Modifier.weight(1f))
                    Text(":", fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                    CountdownBox(String.format(Locale.US, "%02d", countdownText.third), "Menit", Modifier.weight(1f))
                    Text(":", fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                    CountdownBox(String.format(Locale.US, "%02d", countdownSeconds), "Detik", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { 0.65f },
                    color = Color(0xFF0288D1),
                    trackColor = Color(0xFFB3E5FC),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Jatuh pada Senin, 8 Februari 2027",
                    fontSize = 11.sp,
                    color = Color(0xFF0277BD)
                )
            }
        }
    }
}

@Composable
private fun CountdownBox(number: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFB3E5FC),
        modifier = modifier.padding(horizontal = 3.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(text = number, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF01579B))
            Text(text = label, fontSize = 9.5.sp, color = Color(0xFF0277BD))
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
