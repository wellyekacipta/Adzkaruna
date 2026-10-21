package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.QuranRepository
import com.example.doaharianmuslimin.data.model.QuranAyat
import com.example.doaharianmuslimin.data.model.QuranSurah
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily
import kotlinx.coroutines.launch

enum class QuranViewMode {
    PER_BARIS,    // Mode Ayat demi ayat + Terjemahan Kemenag
    PER_HALAMAN   // Mode Lembaran Halaman Mushaf Kemenag RI / Madinah (Per Halaman)
}

enum class QuranFilterCategory(val label: String) {
    SEMUA("Semua (114)"),
    MAKKIYAH("Makkiyah (86)"),
    MADANIYYAH("Madaniyyah (28)"),
    JUZ_30("Juz 30 (37)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()

    var selectedSurah by remember { mutableStateOf<QuranSurah?>(null) }
    var isLoadingSurah by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    var viewMode by remember { mutableStateOf(QuranViewMode.PER_HALAMAN) }
    var currentPageIndex by remember(selectedSurah) { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(QuranFilterCategory.SEMUA) }

    // State for Selected Verse Tafsir BottomSheet (Kemenag API)
    var selectedAyatForTafsir by remember { mutableStateOf<QuranAyat?>(null) }
    var tafsirContent by remember { mutableStateOf<String?>(null) }
    var isLoadingTafsir by remember { mutableStateOf(false) }

    // Audio Murottal Player for Ayat
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlayingAyatAudio by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun playAyatAudio(audioUrl: String) {
        if (isPlayingAyatAudio) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlayingAyatAudio = false
            return
        }

        if (audioUrl.isBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Audio ayat belum tersedia.")
            }
            return
        }

        try {
            isPlayingAyatAudio = true
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    isPlayingAyatAudio = false
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, _, _ ->
                    isPlayingAyatAudio = false
                    release()
                    mediaPlayer = null
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            isPlayingAyatAudio = false
        }
    }

    // Load Tafsir from Kemenag API whenever an ayat is selected
    LaunchedEffect(selectedAyatForTafsir) {
        val ayat = selectedAyatForTafsir
        val surah = selectedSurah
        if (ayat != null && surah != null) {
            isLoadingTafsir = true
            tafsirContent = QuranRepository.getTafsir(surah.number, ayat.number)
            isLoadingTafsir = false
        } else {
            tafsirContent = null
            if (isPlayingAyatAudio) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                isPlayingAyatAudio = false
            }
        }
    }

    // Filtered list of 114 surahs
    val displayedSurahs = remember(searchQuery, selectedFilter) {
        QuranRepository.allSurahMeta.filter { surah ->
            val matchesFilter = when (selectedFilter) {
                QuranFilterCategory.SEMUA -> true
                QuranFilterCategory.MAKKIYAH -> surah.revelation == "Makkiyah"
                QuranFilterCategory.MADANIYYAH -> surah.revelation == "Madaniyyah"
                QuranFilterCategory.JUZ_30 -> surah.number >= 78
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                surah.name.contains(searchQuery, ignoreCase = true) ||
                        surah.englishMeaning.contains(searchQuery, ignoreCase = true) ||
                        surah.number.toString() == searchQuery.trim() ||
                        surah.arabicName.contains(searchQuery)
            }

            matchesFilter && matchesSearch
        }
    }

    fun openSurah(surah: QuranSurah) {
        coroutineScope.launch {
            isLoadingSurah = true
            loadError = null
            currentPageIndex = 0
            selectedSurah = surah

            val fullSurah = QuranRepository.getSurahDetail(surah.number)
            if (fullSurah != null && fullSurah.ayats.isNotEmpty()) {
                selectedSurah = fullSurah
            } else if (surah.ayats.isEmpty()) {
                loadError = "Gagal memuat ayat surah. Pastikan koneksi internet aktif untuk unduhan pertama."
            }
            isLoadingSurah = false
        }
    }

    fun copyAyat(ayat: QuranAyat, surahName: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = "QS. $surahName Ayat ${ayat.number}:\n\n${ayat.arabic}\n\n${ayat.latin}\n\n\"${ayat.translation}\"\n\n(Adzkaruna - Al-Qur'an Kemenag)"
        clipboard.setPrimaryClip(ClipData.newPlainText("Ayat Al-Qur'an", text))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Ayat disalin ke papan klip")
        }
    }

    fun shareAyat(ayat: QuranAyat, surahName: String) {
        val text = "QS. $surahName Ayat ${ayat.number}:\n\n${ayat.arabic}\n\n${ayat.latin}\n\n\"${ayat.translation}\"\n\n(Adzkaruna - Al-Qur'an Kemenag)"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Ayat Al-Qur'an"))
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedSurah == null) {
                // ==================== LIST OF ALL 114 SURAHS ====================
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Al-Qur'anul Karim (114 Surat)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Mushaf Standar Kemenag RI & Tafsir Resmi",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Banner Info
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = PrimaryTosca,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Mushaf Standar Kemenag RI & Warna Tajwid",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Ketuk ayat untuk membuka Tafsir Lengkap Kemenag & audio murottal",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cari surat (contoh: Al-Baqarah, 36, Yasin)...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Category Filter Chips
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuranFilterCategory.entries.forEach { category ->
                                val isSelected = category == selectedFilter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilter = category },
                                    label = { Text(category.label, fontSize = 11.5.sp) },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Surahs list items with stable key
                    items(displayedSurahs, key = { it.number }) { surah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { openSurah(surah) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${surah.number}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = surah.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${surah.englishMeaning} • ${surah.revelation} (${surah.totalAyat} Ayat)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = surah.arabicName,
                                        fontFamily = UthmanicFontFamily,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== SURAH DETAIL READER ====================
                val surah = selectedSurah!!

                // Compact Sub-Header Bar (Directly beneath main top header)
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 12.dp, top = 2.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    selectedSurah = null
                                    loadError = null
                                    selectedAyatForTafsir = null
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali ke Daftar Surat",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = surah.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${surah.revelation} • ${surah.totalAyat} Ayat",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Toggle Mode Icon (Per Baris vs Per Halaman Mushaf Kemenag)
                        IconButton(
                            onClick = {
                                viewMode = if (viewMode == QuranViewMode.PER_BARIS) QuranViewMode.PER_HALAMAN else QuranViewMode.PER_BARIS
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = if (viewMode == QuranViewMode.PER_BARIS) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Filled.ViewList,
                                contentDescription = "Ganti Mode",
                                tint = AccentGold
                            )
                        }
                    }
                }

                if (isLoadingSurah) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryTosca)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Memuat Surat ${surah.name}...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Mengambil lafadz Uthmanic & terjemahan Kemenag RI",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (loadError != null && surah.ayats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gagal Memuat Surat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = loadError ?: "",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { openSurah(surah) }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coba Lagi")
                            }
                        }
                    }
                } else {
                    // Mode Toggle Chip Bar
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (viewMode == QuranViewMode.PER_BARIS) "Mode Ayat (Per Baris)" else "Mode Mushaf Kemenag RI (Tajwid)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row {
                                FilterChip(
                                    selected = viewMode == QuranViewMode.PER_BARIS,
                                    onClick = { viewMode = QuranViewMode.PER_BARIS },
                                    label = { Text("Per Baris", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Filled.ViewList, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                FilterChip(
                                    selected = viewMode == QuranViewMode.PER_HALAMAN,
                                    onClick = { viewMode = QuranViewMode.PER_HALAMAN },
                                    label = { Text("Mushaf Kemenag", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                )
                            }
                        }
                    }

                    if (viewMode == QuranViewMode.PER_BARIS) {
                        // ==================== MODE 1: PER BARIS ====================
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Basmalah Header (kecuali At-Taubah)
                            if (surah.number != 9) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, PrimaryTosca.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                fontFamily = UthmanicFontFamily,
                                                fontSize = 24.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Ayats list with stable key
                            items(surah.ayats, key = { it.number }) { ayat ->
                                val isSelected = selectedAyatForTafsir?.number == ayat.number

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { selectedAyatForTafsir = ayat },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) AccentGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) AccentGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    elevation = CardDefaults.cardElevation(1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header: Ayah badge and quick action buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = if (isSelected) AccentGold else MaterialTheme.colorScheme.primary,
                                                shape = CircleShape,
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${ayat.number}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Tombol Tafsir Kemenag
                                                FilledTonalButton(
                                                    onClick = { selectedAyatForTafsir = ayat },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Tafsir", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                IconButton(
                                                    onClick = { copyAyat(ayat, surah.name) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.ContentCopy,
                                                        contentDescription = "Salin",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = { shareAyat(ayat, surah.name) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Share,
                                                        contentDescription = "Bagikan",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Arabic Text with Tajweed Colors
                                        Text(
                                            text = buildTajweedAnnotatedString(ayat.arabic, isDark),
                                            fontFamily = UthmanicFontFamily,
                                            fontSize = 25.sp,
                                            lineHeight = 46.sp,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        if (ayat.latin.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = ayat.latin,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Indonesian Translation (Kemenag RI)
                                        Text(
                                            text = ayat.translation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // ==================== MODE 2: PERSIS MUSHAF QURAN KEMENAG RI DENGAN WARNA TAJWID ====================
                        val pages = remember(surah.ayats) { paginateAyats(surah.ayats) }
                        val safePageIndex = currentPageIndex.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                        val currentAyats = pages.getOrElse(safePageIndex) { emptyList() }
                        val isEvenPage = (safePageIndex + 1) % 2 == 0

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Full-width Illuminated Kemenag Quran Page Frame
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFFDF5), // Warna kertas Mushaf Kemenag RI
                                border = BorderStroke(2.dp, AccentGold),
                                shadowElevation = 3.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                ) {
                                    // Ornamental Frame Canvas (Islamic Geometric Illumination Border)
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeW = 1.5.dp.toPx()
                                        // Inner border line
                                        drawRect(
                                            color = Color(0xFF00897B).copy(alpha = 0.5f),
                                            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                                            size = size.copy(width = size.width - 8.dp.toPx(), height = size.height - 8.dp.toPx()),
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                        )
                                    }

                                    // Outer Margin Medallion (Ruku' indicator 'ع' or 'وقف' in margin as in Kemenag photo)
                                    Box(
                                        modifier = Modifier
                                            .align(if (isEvenPage) Alignment.CenterStart else Alignment.CenterEnd)
                                            .padding(if (isEvenPage) PaddingValues(start = 2.dp) else PaddingValues(end = 2.dp))
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFF00897B)),
                                            shadowElevation = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "ع",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = UthmanicFontFamily,
                                                    color = Color(0xFF00897B)
                                                )
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // ================= TOP HEADER TABLETS (PERSIS GAMBAR KEMENAG RI) =================
                                        Surface(
                                            color = Color(0xFFE8F5E9).copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF81C784)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Left Tablet: Symmetrical alternating between Surah and Juz
                                                Surface(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                                                    shadowElevation = 1.dp
                                                ) {
                                                    Text(
                                                        text = if (isEvenPage) "${surah.number}. ${surah.name}" else "JUZ 1",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1B5E20),
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                                    )
                                                }

                                                // Center Tablet: Dome arch with page number
                                                Surface(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                                    border = BorderStroke(1.2.dp, AccentGold),
                                                    shadowElevation = 1.dp
                                                ) {
                                                    Text(
                                                        text = "${safePageIndex + 1}",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF2E7D32),
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                                                    )
                                                }

                                                // Right Tablet: Symmetrical alternating
                                                Surface(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                                                    shadowElevation = 1.dp
                                                ) {
                                                    Text(
                                                        text = if (isEvenPage) "JUZ 1" else "${surah.number}. ${surah.name}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1B5E20),
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // ================= KUBAH UKIRAN HIASAN KEPALA SURAT (HALAMAN PERTAMA) =================
                                        if (safePageIndex == 0) {
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                color = Color(0xFFF1F8E9),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.5.dp, Color(0xFF388E3C)),
                                                shadowElevation = 2.dp
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(vertical = 4.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = "سُورَةُ ${surah.arabicName}",
                                                        fontFamily = UthmanicFontFamily,
                                                        fontSize = 21.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1B5E20)
                                                    )
                                                    if (surah.number != 9) {
                                                        Text(
                                                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                            fontFamily = UthmanicFontFamily,
                                                            fontSize = 18.sp,
                                                            color = Color(0xFF2E7D32)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // ================= AYAT-AYAT MUSHAF KEMENAG DENGAN WARNA TAJWID LENGKAP =================
                                        // Tap on any verse opens Tafsir Kemenag RI instantly!
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(currentAyats, key = { it.number }) { ayat ->
                                                    val isSelected = selectedAyatForTafsir?.number == ayat.number

                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .clickable { selectedAyatForTafsir = ayat },
                                                        color = if (isSelected) AccentGold.copy(alpha = 0.22f) else Color.Transparent,
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                                            horizontalArrangement = Arrangement.End,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = buildTajweedAnnotatedString(ayat.arabic, isDark),
                                                                fontFamily = UthmanicFontFamily,
                                                                fontSize = 22.sp,
                                                                lineHeight = 44.sp,
                                                                textAlign = TextAlign.End,
                                                                modifier = Modifier.weight(1f)
                                                            )

                                                            Spacer(modifier = Modifier.width(6.dp))

                                                            // Ornate Ayah rosette marker ۝
                                                            Surface(
                                                                shape = CircleShape,
                                                                color = Color(0xFFFFF3E0),
                                                                border = BorderStroke(1.dp, Color(0xFFE65100)),
                                                                modifier = Modifier.size(24.dp)
                                                            ) {
                                                                Box(contentAlignment = Alignment.Center) {
                                                                    Text(
                                                                        text = toArabicDigits(ayat.number),
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = Color(0xFFBF360C)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // Subtle horizontal page ruling line between verses
                                                    HorizontalDivider(
                                                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                                        thickness = 0.8.dp,
                                                        modifier = Modifier.padding(horizontal = 4.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // ================= FOOTER BINGKAI =================
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (currentAyats.isNotEmpty()) "Ayat ${currentAyats.first().number}-${currentAyats.last().number} (Ketuk untuk Tafsir)" else "",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF2E7D32)
                                            )

                                            Text(
                                                text = "Hal. ${safePageIndex + 1} / ${pages.size}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryTosca
                                            )
                                        }
                                    }
                                }
                            }

                            // ================= NAVIGASI HALAMAN MUSHAF (ARAH ARAB: KIRI = SELANJUTNYA, KANAN = SEBELUMNYA) =================
                            if (pages.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // KIRI: Halaman Selanjutnya (Maju membaca dalam kaidah mushaf Arab)
                                    Button(
                                        onClick = { if (safePageIndex < pages.size - 1) currentPageIndex++ },
                                        enabled = safePageIndex < pages.size - 1,
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Hal. Selanjutnya", fontSize = 11.5.sp)
                                    }

                                    // TENGAH: Indikator Halaman
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "${safePageIndex + 1} / ${pages.size}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                        )
                                    }

                                    // KANAN: Halaman Sebelumnya (Mundur ke lembar kanan mushaf)
                                    OutlinedButton(
                                        onClick = { if (safePageIndex > 0) currentPageIndex-- },
                                        enabled = safePageIndex > 0,
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("Hal. Sebelumnya", fontSize = 11.5.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== BOTTOM SHEET: TAFSIR LENGKAP KEMENAG RI & AUDIO MUROTTAL ====================
        if (selectedAyatForTafsir != null) {
            val ayat = selectedAyatForTafsir!!
            val surah = selectedSurah!!

            ModalBottomSheet(
                onDismissRequest = {
                    selectedAyatForTafsir = null
                    if (isPlayingAyatAudio) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        isPlayingAyatAudio = false
                    }
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header BottomSheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = PrimaryTosca,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${ayat.number}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tafsir QS. ${surah.name} : Ayat ${ayat.number}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Sumber: Kementerian Agama RI (Kemenag)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = {
                            selectedAyatForTafsir = null
                            if (isPlayingAyatAudio) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null
                                isPlayingAyatAudio = false
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Murottal Player & Quick Actions
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryTosca.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledTonalButton(
                                    onClick = { playAyatAudio(ayat.audioUrl) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAyatAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isPlayingAyatAudio) "Berhenti" else "Murottal Ayat", fontSize = 11.5.sp)
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { copyAyat(ayat, surah.name) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = PrimaryTosca, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { shareAyat(ayat, surah.name) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = PrimaryTosca, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Lafadz Ayat Arab dengan Tajwid
                    Text(
                        text = buildTajweedAnnotatedString(ayat.arabic, isDark),
                        fontFamily = UthmanicFontFamily,
                        fontSize = 26.sp,
                        lineHeight = 48.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (ayat.latin.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = ayat.latin,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Terjemahan Kemenag RI
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Terjemahan Kemenag RI:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = PrimaryTosca
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ayat.translation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section Header Tafsir Kemenag RI
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tafsir Lengkap Kemenag RI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(
                        color = AccentGold.copy(alpha = 0.4f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Tafsir Content (Live from API Kemenag)
                    if (isLoadingTafsir) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryTosca)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Mengambil tafsir resmi Kemenag RI...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        Text(
                            text = tafsirContent ?: "Tafsir belum tersedia.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Intelligent Tajweed Colorizer:
 * Matches official Kemenag RI Mushaf Tajweed color rules:
 * - Cyan / Sky Blue: Mad Wajib & Mad Jaiz (ٓ)
 * - Magenta / Pink: Ghunnah (نّ, مّ)
 * - Green: Ikhfa Haqiqi & Tanwin (نْ, ً ٍ ٌ)
 * - Reddish Orange: Qalqalah (قْ, طْ, بْ, جْ, دْ)
 */
fun buildTajweedAnnotatedString(text: String, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val baseColor = if (isDark) Color(0xFFE8E8E8) else Color(0xFF1A1A1A)
        val madColor = Color(0xFF0091EA)       // Cyan / Biru Langit (Mad Panjang 4-6 harakat)
        val ghunnahColor = Color(0xFFE91E63)   // Magenta / Pink (Ghunnah)
        val ikhfaColor = Color(0xFF2E7D32)     // Hijau (Ikhfa & Idgham)
        val qalqalahColor = Color(0xFFD84315)  // Oranye Kemerahan (Qalqalah)

        var i = 0
        while (i < text.length) {
            val char = text[i]
            if (char == '\u0653') { // Maddah ٓ
                withStyle(SpanStyle(color = madColor, fontWeight = FontWeight.Bold)) {
                    append(char)
                }
                i++
            } else if (char == 'ن' || char == 'م') {
                if (i + 1 < text.length && text[i + 1] == '\u0651') { // Shaddah ّ
                    withStyle(SpanStyle(color = ghunnahColor, fontWeight = FontWeight.Bold)) {
                        append(char)
                        append(text[i + 1])
                    }
                    i += 2
                } else {
                    withStyle(SpanStyle(color = baseColor)) {
                        append(char)
                    }
                    i++
                }
            } else if (listOf('ق', 'ط', 'ب', 'ج', 'د').contains(char)) {
                if (i + 1 < text.length && (text[i + 1] == '\u0652' || text[i + 1] == '\u06E1')) {
                    withStyle(SpanStyle(color = qalqalahColor, fontWeight = FontWeight.Bold)) {
                        append(char)
                        append(text[i + 1])
                    }
                    i += 2
                } else {
                    withStyle(SpanStyle(color = baseColor)) {
                        append(char)
                    }
                    i++
                }
            } else if (char == '\u064B' || char == '\u064C' || char == '\u064D') { // Tanwin
                withStyle(SpanStyle(color = ikhfaColor, fontWeight = FontWeight.Bold)) {
                    append(char)
                }
                i++
            } else {
                withStyle(SpanStyle(color = baseColor)) {
                    append(char)
                }
                i++
            }
        }
    }
}

/**
 * Paginates verses into fixed pages for true Mushaf Madinah / Kemenag reading.
 * Short surahs (<= 7 verses) fit on 1 single page without any scroll.
 */
fun paginateAyats(ayats: List<QuranAyat>): List<List<QuranAyat>> {
    if (ayats.isEmpty()) return emptyList()
    if (ayats.size <= 7) return listOf(ayats)

    val pages = mutableListOf<List<QuranAyat>>()
    var currentChunk = mutableListOf<QuranAyat>()
    var currentLength = 0
    val maxCharsPerPage = 360

    for (ayat in ayats) {
        val ayatLen = ayat.arabic.length + 15
        if (currentChunk.isNotEmpty() && (currentLength + ayatLen > maxCharsPerPage || currentChunk.size >= 8)) {
            pages.add(currentChunk.toList())
            currentChunk = mutableListOf()
            currentLength = 0
        }
        currentChunk.add(ayat)
        currentLength += ayatLen
    }
    if (currentChunk.isNotEmpty()) {
        pages.add(currentChunk.toList())
    }
    return pages
}

// Convert numbers to Arabic numeral digits (١, ٢, ٣...)
fun toArabicDigits(number: Int): String {
    val arabicNumerals = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { arabicNumerals[it - '0'] }.joinToString("")
}
