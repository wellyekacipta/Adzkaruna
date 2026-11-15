package com.example.doaharianmuslimin.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.ContextCompat
import com.example.doaharianmuslimin.data.AdhanVoiceOption
import com.example.doaharianmuslimin.data.CityOption
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.PrayerTimesRepository
import com.example.doaharianmuslimin.data.TrackerRepository
import com.example.doaharianmuslimin.data.model.PrayerCheckStatus
import com.example.doaharianmuslimin.data.model.PrayerSchedule
import com.example.doaharianmuslimin.data.model.PrayerTimeItem
import com.example.doaharianmuslimin.service.AdhanReminderHelper
import com.example.doaharianmuslimin.ui.theme.AccentGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val prefs = remember { PreferencesManager(context) }
    val trackerRepo = remember { TrackerRepository(context) }
    val isDark = isSystemInDarkTheme()

    var selectedCity by remember {
        mutableStateOf(CityOption(prefs.getSavedCityId(), prefs.getSavedCityName()))
    }
    var schedule by remember { mutableStateOf<PrayerSchedule?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLocatingGps by remember { mutableStateOf(false) }

    // Dialog state
    var showCityDialog by remember { mutableStateOf(false) }
    var showAdhanVoiceDialog by remember { mutableStateOf(false) }
    var citySearchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<CityOption>>(emptyList()) }

    // Selected prayer for marking status (Modal Sheet)
    var markingPrayerName by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    // Date Navigation State (0 = Hari ini, -1 = Kemarin, 1 = Besok)
    var dayOffset by remember { mutableIntStateOf(0) }
    val targetCalendar = remember(dayOffset) {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
    }
    val targetDateKey = remember(targetCalendar) {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(targetCalendar.time)
    }
    val targetDateDisplay = remember(targetCalendar) {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        sdf.format(targetCalendar.time)
    }
    val targetRelativeText = remember(dayOffset) {
        when (dayOffset) {
            0 -> "Hari ini"
            -1 -> "Kemarin"
            1 -> "Besok"
            else -> if (dayOffset < 0) "${-dayOffset} hari lalu" else "${dayOffset} hari lagi"
        }
    }

    // Live Countdown state
    var countdownText by remember { mutableStateOf("00:00:00") }
    var nextPrayerName by remember { mutableStateOf("Subuh") }

    // Adhan Reminders & Voice Settings
    var isAdhanEnabled by remember { mutableStateOf(prefs.isAdhanEnabled()) }
    var selectedAdhanVoice by remember {
        mutableStateOf(
            try {
                AdhanVoiceOption.valueOf(prefs.getSelectedAdhanVoice())
            } catch (_: Exception) {
                AdhanVoiceOption.MAKKAH
            }
        )
    }
    var currentlyPlayingVoice by remember { mutableStateOf<AdhanVoiceOption?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var prayerReminderToggles by remember {
        mutableStateOf(prefs.getPrayerReminderToggles())
    }

    // Auto load schedule on city change
    LaunchedEffect(selectedCity) {
        isLoading = true
        schedule = PrayerTimesRepository.fetchKemenagSchedule(selectedCity.id, selectedCity.name)
        isLoading = false
    }

    // Live countdown update loop
    LaunchedEffect(schedule) {
        while (true) {
            val sched = schedule
            if (sched != null) {
                val nowCal = Calendar.getInstance()
                val nowMinutes = nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)
                val nowSeconds = nowCal.get(Calendar.SECOND)

                val prayersWithMinutes = sched.items.mapNotNull { item ->
                    val parts = item.time.split(":")
                    if (parts.size == 2) {
                        val h = parts[0].trim().toIntOrNull() ?: 0
                        val m = parts[1].trim().toIntOrNull() ?: 0
                        Triple(item.name, h * 60 + m, item)
                    } else null
                }.sortedBy { it.second }

                val next = prayersWithMinutes.find { it.second > nowMinutes }
                    ?: prayersWithMinutes.firstOrNull()

                if (next != null) {
                    nextPrayerName = next.first

                    var diffMinutes = next.second - nowMinutes
                    if (diffMinutes <= 0) diffMinutes += 24 * 60
                    var remainingSecs = 60 - nowSeconds
                    if (remainingSecs == 60) remainingSecs = 0 else diffMinutes -= 1

                    val hours = diffMinutes / 60
                    val mins = diffMinutes % 60
                    countdownText = String.format("%02d:%02d:%02d", hours, mins, remainingSecs)
                }
            }
            delay(1000)
        }
    }

    fun playAudioPreview(option: AdhanVoiceOption) {
        if (currentlyPlayingVoice == option) {
            try { mediaPlayer?.stop(); mediaPlayer?.release() } catch (_: Exception) {}
            mediaPlayer = null
            currentlyPlayingVoice = null
            return
        }

        try { mediaPlayer?.stop(); mediaPlayer?.release() } catch (_: Exception) {}
        mediaPlayer = null
        currentlyPlayingVoice = option

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(option.audioUrl)
                setOnPreparedListener {
                    try { start() } catch (_: Exception) {}
                }
                setOnCompletionListener {
                    currentlyPlayingVoice = null
                    try { release() } catch (_: Exception) {}
                    mediaPlayer = null
                }
                setOnErrorListener { _, _, _ ->
                    currentlyPlayingVoice = null
                    try { release() } catch (_: Exception) {}
                    mediaPlayer = null
                    try {
                        val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(context, alertUri)
                        r?.play()
                    } catch (_: Exception) {}
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            currentlyPlayingVoice = null
        }
    }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            coroutineScope.launch {
                isLocatingGps = true
                val detected = PrayerTimesRepository.detectCityFromLocation(context)
                if (detected != null) {
                    selectedCity = detected
                    prefs.setSavedCity(detected.id, detected.name)
                    AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                    snackbarHostState.showSnackbar("Lokasi berhasil disinkronkan: ${detected.name}")
                } else {
                    snackbarHostState.showSnackbar("Gagal mendeteksi kota dari GPS.")
                }
                isLocatingGps = false
            }
        }
    }

    fun requestGpsLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            coroutineScope.launch {
                isLocatingGps = true
                val detected = PrayerTimesRepository.detectCityFromLocation(context)
                if (detected != null) {
                    selectedCity = detected
                    prefs.setSavedCity(detected.id, detected.name)
                    AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                    snackbarHostState.showSnackbar("Lokasi diperbarui: ${detected.name}")
                }
                isLocatingGps = false
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Formatted City Name
    val displayCityName = remember(selectedCity) {
        selectedCity.name
            .replace("KAB. ", "Kabupaten ")
            .replace("KABUPATEN ", "Kabupaten ")
            .replace("KOTA ", "Kota ")
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }

    // Fardhu prayers list for tracking count
    val fardhuNames = remember { listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya") }
    val recordedFardhuCount = remember(refreshKey, targetDateKey) {
        fardhuNames.count {
            trackerRepo.getPrayerStatus(targetDateKey, it) != PrayerCheckStatus.BELUM
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Jadwal Sholat",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "API Kemenag RI",
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
                actions = {
                    IconButton(onClick = { requestGpsLocation() }) {
                        Icon(
                            imageVector = if (isLocatingGps) Icons.Default.Sync else Icons.Default.MyLocation,
                            contentDescription = "Deteksi Lokasi GPS",
                            tint = AccentGold
                        )
                    }
                    IconButton(onClick = {
                        citySearchQuery = ""
                        showCityDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = "Pilih Kota",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ==================== 1. HEADER: JUDUL "Salat" & PILL KOTA ====================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Salat",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Jadwal & absen salat wajibmu.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pill Lokasi Kota
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                citySearchQuery = ""
                                showCityDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = displayCityName,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // ==================== 2. DATE SELECTOR CARD (NAVIGASI TANGGAL) ====================
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { dayOffset-- }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Hari Sebelumnya", modifier = Modifier.size(20.dp))
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { dayOffset = 0 }
                        ) {
                            Text(
                                text = targetDateDisplay,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = targetRelativeText,
                                fontSize = 11.5.sp,
                                color = if (dayOffset == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (dayOffset == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { dayOffset++ }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Hari Berikutnya", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // ==================== 3. ABSEN SALAT TRACKER BAR ====================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ABSEN SALAT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "$recordedFardhuCount / 5 tercatat",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (recordedFardhuCount == 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (recordedFardhuCount / 5f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // ==================== 4. LIST 7 WAKTU SHOLAT ====================
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                schedule?.let { s ->
                    val prayerItems = s.items
                    items(prayerItems, key = { it.name }) { item ->
                        val isFardhu = fardhuNames.contains(item.name)
                        val isReminderActive = prayerReminderToggles[item.name] ?: true
                        val status = remember(refreshKey, targetDateKey, item.name) {
                            if (isFardhu) trackerRepo.getPrayerStatus(targetDateKey, item.name) else PrayerCheckStatus.BELUM
                        }
                        val isNext = (item.name == nextPrayerName) && (dayOffset == 0)

                        PrayerRowCard(
                            item = item,
                            isFardhu = isFardhu,
                            isNext = isNext,
                            countdownText = countdownText,
                            status = status,
                            isReminderActive = isReminderActive,
                            isDark = isDark,
                            onRowClick = {
                                if (isFardhu) {
                                    markingPrayerName = item.name
                                }
                            },
                            onReminderToggle = {
                                val nextVal = !isReminderActive
                                prayerReminderToggles = prayerReminderToggles.toMutableMap().apply {
                                    put(item.name, nextVal)
                                }
                                prefs.setPrayerReminderToggle(item.name, nextVal)
                                AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (nextVal) "Pengingat ${item.name} diaktifkan 🔔" else "Pengingat ${item.name} dinonaktifkan 🔕"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // ==================== 5. HINT TEXT ====================
            item {
                Text(
                    text = "Ketuk baris untuk catat status · ketuk ikon untuk atur notifikasi",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // ==================== 6. BANNER PENGATURAN ADZAN & BATERAI ====================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showAdhanVoiceDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pengaturan Suara Adzan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Suara: ${selectedAdhanVoice.displayName} — Ketuk untuk pilih ›",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            try {
                                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Buka Pengaturan Baterai di HP Anda untuk mengizinkan aplikasi berjalan di latar belakang.")
                                }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lepas pembatasan baterai",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Agar adzan dan alarm sholat tepat waktu saat HP terkunci ›",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // ==================== 7. MODAL BOTTOM SHEET: TANDAI STATUS SALAT ====================
    if (markingPrayerName != null) {
        val prayerName = markingPrayerName!!
        ModalBottomSheet(
            onDismissRequest = { markingPrayerName = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Tandai $prayerName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                // 1. Tepat Waktu (Hijau)
                val greenBg = if (isDark) Color(0xFF1B5E20).copy(alpha = 0.35f) else Color(0xFFE8F5E9)
                val greenContent = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                PrayerStatusOptionPill(
                    icon = Icons.Default.Check,
                    label = "Tepat waktu",
                    containerColor = greenBg,
                    contentColor = greenContent,
                    onClick = {
                        trackerRepo.setPrayerStatus(targetDateKey, prayerName, PrayerCheckStatus.TEPAT_WAKTU)
                        refreshKey++
                        markingPrayerName = null
                    }
                )

                // 2. Berjamaah (Ungu)
                val purpleBg = if (isDark) Color(0xFF311B92).copy(alpha = 0.35f) else Color(0xFFEDE7F6)
                val purpleContent = if (isDark) Color(0xFFB39DDB) else Color(0xFF5E35B1)
                PrayerStatusOptionPill(
                    icon = Icons.Default.Groups,
                    label = "Berjamaah",
                    containerColor = purpleBg,
                    contentColor = purpleContent,
                    onClick = {
                        trackerRepo.setPrayerStatus(targetDateKey, prayerName, PrayerCheckStatus.BERJAMAAH)
                        refreshKey++
                        markingPrayerName = null
                    }
                )

                // 3. Terlambat (Kuning / Oranye)
                val orangeBg = if (isDark) Color(0xFFE65100).copy(alpha = 0.3f) else Color(0xFFFFF3E0)
                val orangeContent = if (isDark) Color(0xFFFFB74D) else Color(0xFFE65100)
                PrayerStatusOptionPill(
                    icon = Icons.Default.Schedule,
                    label = "Terlambat",
                    containerColor = orangeBg,
                    contentColor = orangeContent,
                    onClick = {
                        trackerRepo.setPrayerStatus(targetDateKey, prayerName, PrayerCheckStatus.TERLAMBAT)
                        refreshKey++
                        markingPrayerName = null
                    }
                )

                // 4. Terlewat (Netral / Abu-abu)
                val neutralBg = if (isDark) Color(0xFF424242).copy(alpha = 0.35f) else Color(0xFFEFEBE9)
                val neutralContent = if (isDark) Color(0xFFBDBDBD) else Color(0xFF616161)
                PrayerStatusOptionPill(
                    icon = Icons.Default.Remove,
                    label = "Terlewat",
                    containerColor = neutralBg,
                    contentColor = neutralContent,
                    onClick = {
                        trackerRepo.setPrayerStatus(targetDateKey, prayerName, PrayerCheckStatus.TERLEWAT)
                        refreshKey++
                        markingPrayerName = null
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Hapus Tanda
                TextButton(
                    onClick = {
                        trackerRepo.setPrayerStatus(targetDateKey, prayerName, PrayerCheckStatus.BELUM)
                        refreshKey++
                        markingPrayerName = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hapus tanda",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // ==================== 8. DIALOG SUARA ADZAN ====================
    if (showAdhanVoiceDialog) {
        AlertDialog(
            onDismissRequest = {
                try { mediaPlayer?.stop(); mediaPlayer?.release() } catch (_: Exception) {}
                mediaPlayer = null
                currentlyPlayingVoice = null
                showAdhanVoiceDialog = false
            },
            title = {
                Text("Pilih Suara Adzan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdhanVoiceOption.entries.forEach { option ->
                        val isSelected = option == selectedAdhanVoice
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedAdhanVoice = option
                                    prefs.setSelectedAdhanVoice(option.name)
                                    AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedAdhanVoice = option
                                            prefs.setSelectedAdhanVoice(option.name)
                                            AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = option.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                IconButton(onClick = { playAudioPreview(option) }) {
                                    Icon(
                                        imageVector = if (currentlyPlayingVoice == option) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Dengar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    try { mediaPlayer?.stop(); mediaPlayer?.release() } catch (_: Exception) {}
                    mediaPlayer = null
                    currentlyPlayingVoice = null
                    showAdhanVoiceDialog = false
                }) {
                    Text("Selesai")
                }
            }
        )
    }

    // ==================== 9. DIALOG PILIH DARI 518 KOTA ====================
    if (showCityDialog) {
        LaunchedEffect(citySearchQuery) {
            searchResults = PrayerTimesRepository.searchCities(citySearchQuery)
        }

        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text(
                    text = "Pilih Kota / Kabupaten (518 Wilayah)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    OutlinedTextField(
                        value = citySearchQuery,
                        onValueChange = { citySearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ketik nama kota/kabupaten...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (citySearchQuery.isNotEmpty()) {
                                IconButton(onClick = { citySearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(searchResults, key = { it.id }) { city ->
                            val isSelected = city.id == selectedCity.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedCity = city
                                        prefs.setSavedCity(city.id, city.name)
                                        showCityDialog = false
                                        AdhanReminderHelper.scheduleAllPrayerAlarms(context)
                                    },
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = city.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

// ==================== KOMPONEN KARTU WAKTU SALAT (SESUAI GAMBAR 2) ====================
@Composable
private fun PrayerRowCard(
    item: PrayerTimeItem,
    isFardhu: Boolean,
    isNext: Boolean,
    countdownText: String,
    status: PrayerCheckStatus,
    isReminderActive: Boolean,
    isDark: Boolean,
    onRowClick: () -> Unit,
    onReminderToggle: () -> Unit
) {
    val isChecked = status != PrayerCheckStatus.BELUM

    // Warna status
    val statusColor = when (status) {
        PrayerCheckStatus.TEPAT_WAKTU -> Color(0xFF2E7D32)
        PrayerCheckStatus.BERJAMAAH -> Color(0xFF5E35B1)
        PrayerCheckStatus.TERLAMBAT -> Color(0xFFE65100)
        PrayerCheckStatus.TERLEWAT -> Color(0xFF757575)
        PrayerCheckStatus.BELUM -> MaterialTheme.colorScheme.primary
    }

    // Background card: jika next prayer atau aktif, berikan container highlight lembut
    val cardBg = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.45f else 0.55f)
        isChecked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.35f else 0.45f)
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorder = when {
        isNext -> BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        isChecked -> BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = cardBorder,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = isFardhu, onClick = onRowClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox status (Khusus 5 Shalat Fardhu)
                if (isFardhu) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isChecked) {
                                    Modifier.background(statusColor.copy(alpha = 0.2f))
                                        .border(1.5.dp, statusColor, RoundedCornerShape(8.dp))
                                } else {
                                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(
                                imageVector = when (status) {
                                    PrayerCheckStatus.TEPAT_WAKTU -> Icons.Default.Check
                                    PrayerCheckStatus.BERJAMAAH -> Icons.Default.Groups
                                    PrayerCheckStatus.TERLAMBAT -> Icons.Default.Schedule
                                    PrayerCheckStatus.TERLEWAT -> Icons.Default.Remove
                                    PrayerCheckStatus.BELUM -> Icons.Default.Check
                                },
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Nama Sholat & Subtitle
                Column {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isFardhu) {
                        if (isChecked) {
                            Text(
                                text = "✓ ${status.label} · ketuk untuk ubah",
                                fontSize = 11.sp,
                                color = statusColor,
                                fontWeight = FontWeight.Medium
                            )
                        } else if (isNext) {
                            Text(
                                text = "$countdownText lagi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Waktu Sholat & Tombol Lonceng Pengingat
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.time,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Tombol Lonceng
                Surface(
                    shape = CircleShape,
                    color = if (isReminderActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onReminderToggle)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isReminderActive) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Pengingat ${item.name}",
                            tint = if (isReminderActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== PILIHAN OPSI STATUS (SESUAI GAMBAR 1) ====================
@Composable
private fun PrayerStatusOptionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
