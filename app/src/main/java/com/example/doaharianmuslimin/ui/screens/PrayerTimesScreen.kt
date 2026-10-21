package com.example.doaharianmuslimin.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.doaharianmuslimin.data.AdhanVoiceOption
import com.example.doaharianmuslimin.data.CityOption
import com.example.doaharianmuslimin.data.PrayerTimesRepository
import com.example.doaharianmuslimin.data.model.PrayerSchedule
import com.example.doaharianmuslimin.data.model.PrayerTimeItem
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import com.example.doaharianmuslimin.ui.theme.PrimaryToscaLight
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedCity by remember { mutableStateOf(PrayerTimesRepository.popularCities.first()) }
    var schedule by remember { mutableStateOf<PrayerSchedule?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLocatingGps by remember { mutableStateOf(false) }

    // Dialog state
    var showCityDialog by remember { mutableStateOf(false) }
    var citySearchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<CityOption>>(emptyList()) }

    // Live Countdown state
    var countdownText by remember { mutableStateOf("00:00:00") }
    var nextPrayerName by remember { mutableStateOf("Subuh") }

    // Adhan Reminders & Voice Settings
    var isAdhanEnabled by remember { mutableStateOf(true) }
    var selectedAdhanVoice by remember { mutableStateOf(AdhanVoiceOption.MAKKAH) }
    var currentlyPlayingVoice by remember { mutableStateOf<AdhanVoiceOption?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var prayerReminderToggles by remember {
        mutableStateOf(
            mapOf(
                "Subuh" to true,
                "Dzuhur" to true,
                "Ashar" to true,
                "Maghrib" to true,
                "Isya" to true
            )
        )
    }

    // Auto load schedule on city change
    LaunchedEffect(selectedCity) {
        isLoading = true
        schedule = PrayerTimesRepository.fetchKemenagSchedule(selectedCity.id, selectedCity.name)
        isLoading = false
    }

    // Live 1-second ticking countdown timer
    LaunchedEffect(schedule) {
        while (true) {
            val (cd, name) = PrayerTimesRepository.getCountdownToNextPrayer(schedule)
            countdownText = cd
            nextPrayerName = name
            delay(1000)
        }
    }

    // Cleanup MediaPlayer on exit
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (_: Exception) {}
            mediaPlayer = null
        }
    }

    fun playAudioPreview(option: AdhanVoiceOption) {
        if (currentlyPlayingVoice == option) {
            // Hentikan jika sedang memutar opsi yang sama
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (_: Exception) {}
            mediaPlayer = null
            currentlyPlayingVoice = null
            return
        }

        // Hentikan pemutaran sebelumnya
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        currentlyPlayingVoice = option
        selectedAdhanVoice = option

        coroutineScope.launch {
            snackbarHostState.showSnackbar("Memutar: ${option.displayName}...")
        }

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
                    try {
                        start()
                    } catch (_: Exception) {}
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
                    // Fallback ke nada sistem jika jaringan lambat
                    try {
                        val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(context, alertUri)
                        r?.play()
                    } catch (_: Exception) {}
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            currentlyPlayingVoice = null
            try {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(context, alertUri)
                r?.play()
            } catch (_: Exception) {}
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
                    snackbarHostState.showSnackbar("Lokasi berhasil diperbarui: ${detected.name}")
                } else {
                    snackbarHostState.showSnackbar("Gagal mendeteksi nama kota dari GPS.")
                }
                isLocatingGps = false
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Izin lokasi diperlukan untuk deteksi otomatis.")
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
                    snackbarHostState.showSnackbar("Lokasi berhasil diperbarui: ${detected.name}")
                } else {
                    snackbarHostState.showSnackbar("GPS aktif tapi kota belum terdeteksi. Silakan pilih dari daftar.")
                }
                isLocatingGps = false
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Auto detect GPS on first mount
    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            val detected = PrayerTimesRepository.detectCityFromLocation(context)
            if (detected != null) {
                selectedCity = detected
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Jadwal Sholat Kemenag RI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hitung Mundur & Data Resmi 518 Kota",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
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
                    // Tombol GPS otomatis
                    IconButton(onClick = { requestGpsLocation() }) {
                        Icon(
                            imageVector = if (isLocatingGps) Icons.Default.Sync else Icons.Default.MyLocation,
                            contentDescription = "Deteksi Lokasi GPS",
                            tint = AccentGold
                        )
                    }

                    // Tombol ganti kota
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Card: Live countdown timer and next prayer info (Dinamis bukan Maghrib terus!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(PrimaryTosca, PrimaryToscaLight)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            // City switcher and API badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.clickable {
                                        citySearchQuery = ""
                                        showCityDialog = true
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = selectedCity.name,
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "API Kemenag",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Next Prayer & Live Countdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Waktu Sholat Berikutnya:",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = nextPrayerName,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = schedule?.date ?: "Memuat...",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                }

                                // Live Countdown Ticker
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "- $countdownText",
                                            color = AccentGold,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Waktu: ${schedule?.items?.find { it.isNext }?.time ?: "--:--"}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==================== DAFTAR WAKTU SHOLAT ====================
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                schedule?.let { s ->
                    items(s.items) { item ->
                        val isReminderActive = prayerReminderToggles[item.name] ?: false
                        val isMajorPrayer = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya").contains(item.name)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isNext) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (item.isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (item.isNext) PrimaryTosca else MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = when (item.name) {
                                                    "Subuh", "Imsak" -> Icons.Default.WbTwilight
                                                    "Terbit", "Dhuha" -> Icons.Default.WbSunny
                                                    "Dzuhur" -> Icons.Default.LightMode
                                                    "Ashar" -> Icons.Default.WbCloudy
                                                    "Maghrib" -> Icons.Default.NightsStay
                                                    else -> Icons.Default.Bedtime
                                                },
                                                contentDescription = null,
                                                tint = if (item.isNext) Color.White else PrimaryTosca,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (item.isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (item.isNext) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = PrimaryTosca,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Berikutnya",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = item.arabicName,
                                            fontFamily = UthmanicFontFamily,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.time,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = if (item.isNext) PrimaryTosca else MaterialTheme.colorScheme.onSurface
                                    )

                                    if (isMajorPrayer) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        IconButton(
                                            onClick = {
                                                val nextVal = !isReminderActive
                                                prayerReminderToggles = prayerReminderToggles.toMutableMap().apply {
                                                    put(item.name, nextVal)
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (nextVal) "Pengingat adzan ${item.name} diaktifkan" else "Pengingat ${item.name} dinonaktifkan"
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isReminderActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                                contentDescription = "Pengingat",
                                                tint = if (isReminderActive) PrimaryTosca else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==================== PENGATURAN SUARA ADZAN ====================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = PrimaryTosca,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Pengingat Suara Adzan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Pilih muadzin & dengarkan contoh suara",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isAdhanEnabled,
                                onCheckedChange = { isAdhanEnabled = it }
                            )
                        }

                        if (isAdhanEnabled) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            Text(
                                text = "Pilihan Muadzin / Nada Pengingat:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            AdhanVoiceOption.entries.forEach { option ->
                                val isSelected = option == selectedAdhanVoice
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedAdhanVoice = option },
                                    color = if (isSelected) PrimaryTosca.copy(alpha = 0.1f) else Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) PrimaryTosca else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedAdhanVoice = option }
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = option.displayName,
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }

                                        FilledTonalButton(
                                            onClick = { playAudioPreview(option) },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = if (currentlyPlayingVoice == option) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = if (currentlyPlayingVoice == option) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = if (currentlyPlayingVoice == option) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (currentlyPlayingVoice == option) "Stop" else "Dengar",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tombol Tes Azan Langsung
                        Button(
                            onClick = { playAudioPreview(selectedAdhanVoice) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentlyPlayingVoice != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (currentlyPlayingVoice != null) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentlyPlayingVoice != null) "Hentikan Suara Azan" else "Tes Bunyikan Azan Sekarang",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== DIALOG PILIH DARI 518 KOTA / KABUPATEN ====================
    if (showCityDialog) {
        // Trigger live search when query changes
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
                    // Search Bar
                    OutlinedTextField(
                        value = citySearchQuery,
                        onValueChange = { citySearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ketik nama kota / kabupaten...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryTosca)
                        },
                        trailingIcon = {
                            if (citySearchQuery.isNotEmpty()) {
                                IconButton(onClick = { citySearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Auto GPS Location Button inside Dialog
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                showCityDialog = false
                                requestGpsLocation()
                            },
                        color = PrimaryTosca.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, PrimaryTosca.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = PrimaryTosca, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gunakan Lokasi Terkini Saya (GPS)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTosca
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // List of cities
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(searchResults) { city ->
                            val isSelected = city.id == selectedCity.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedCity = city
                                        showCityDialog = false
                                    },
                                color = if (isSelected) PrimaryTosca.copy(alpha = 0.15f) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = city.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PrimaryTosca else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryTosca, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
