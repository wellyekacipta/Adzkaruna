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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.doaharianmuslimin.data.model.MushafPageData
import com.example.doaharianmuslimin.data.model.QuranAyat
import com.example.doaharianmuslimin.data.model.QuranSurah
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily
import com.example.doaharianmuslimin.ui.theme.LpmqFontFamily
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
    var blockedAyat by remember { mutableStateOf<QuranAyat?>(null) }
    var currentMushafPage by remember(selectedSurah) { mutableIntStateOf(selectedSurah?.let { QuranRepository.getStartPageForSurah(it.number) } ?: 1) }
    var blockedVerseKey by remember { mutableStateOf<String?>(null) }
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
                                        fontFamily = LpmqFontFamily,
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
                                                fontFamily = LpmqFontFamily,
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
                                            fontFamily = LpmqFontFamily,
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
                        // ==================== MODE 2: MUSHAF 15 BARIS PER HALAMAN RESMI (604 HALAMAN) ====================
                        var pageData by remember(currentMushafPage) { mutableStateOf<MushafPageData?>(null) }
                        var isLoadingPage by remember(currentMushafPage) { mutableStateOf(true) }

                        LaunchedEffect(currentMushafPage) {
                            isLoadingPage = true
                            pageData = QuranRepository.getMushafPage(currentMushafPage)
                            isLoadingPage = false
                            blockedVerseKey = null
                        }

                        val mushafBg = Color(0xFFFFF9F0)
                        val goldColor = AccentGold
                        val greenDark = Color(0xFF2E7D32)
                        val greenLight = Color(0xFF4CAF50)
                        val rosetteBg = Color(0xFFFFF8E1)
                        val rosetteStroke = Color(0xFFE65100)
                        val rosetteTxt = Color(0xFFBF360C)
                        val isEvenPage = currentMushafPage % 2 == 0

                        // Kartu Mushaf Mengisi Seluruh Sisa Ruang Layar (Full Height Antara App Bar & Bottom Nav)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(currentMushafPage) {
                                        var totalDrag = 0f
                                        detectHorizontalDragGestures(
                                            onDragStart = { totalDrag = 0f },
                                            onHorizontalDrag = { change, dragAmount ->
                                                change.consume()
                                                totalDrag += dragAmount
                                            },
                                            onDragEnd = {
                                                if (totalDrag < -50f && currentMushafPage < 604) {
                                                    currentMushafPage++ // Geser kiri -> halaman selanjutnya (RTL)
                                                } else if (totalDrag > 50f && currentMushafPage > 1) {
                                                    currentMushafPage-- // Geser kanan -> halaman sebelumnya
                                                }
                                            }
                                        )
                                    }
                            ) {
                                // Background & Border Mushaf Standar Indonesia
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = mushafBg,
                                    border = BorderStroke(2.dp, goldColor),
                                    shadowElevation = 4.dp
                                ) {}

                                Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                                    drawRect(
                                        color = goldColor.copy(alpha = 0.45f),
                                        topLeft = Offset(0f, 0f),
                                        size = size,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8.dp.toPx())
                                    )
                                }

                                Canvas(modifier = Modifier.fillMaxSize().padding(7.dp)) {
                                    drawRect(
                                        color = greenDark,
                                        topLeft = Offset(0f, 0f),
                                        size = size,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx())
                                    )
                                    val cr = 3.dp.toPx()
                                    drawCircle(greenDark, radius = cr, center = Offset(cr, cr))
                                    drawCircle(greenDark, radius = cr, center = Offset(size.width - cr, cr))
                                    drawCircle(greenDark, radius = cr, center = Offset(cr, size.height - cr))
                                    drawCircle(greenDark, radius = cr, center = Offset(size.width - cr, size.height - cr))
                                }

                                // Medallion 'Ain Ruku' di Margin Kiri/Kanan bergantian
                                Box(
                                    modifier = Modifier
                                        .align(if (isEvenPage) Alignment.CenterStart else Alignment.CenterEnd)
                                        .padding(if (isEvenPage) PaddingValues(start = 2.dp) else PaddingValues(end = 2.dp))
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = mushafBg,
                                        border = BorderStroke(1.2.dp, greenDark),
                                        shadowElevation = 2.dp,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "\u0639",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = LpmqFontFamily,
                                                color = greenDark
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // 1. HEADER 3 TABLET (~8% tinggi)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(28.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = mushafBg,
                                            shape = RoundedCornerShape(5.dp),
                                            border = BorderStroke(1.dp, greenDark)
                                        ) {
                                            Text(
                                                text = pageData?.mainSurahName ?: surah.name,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = greenDark,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            color = mushafBg,
                                            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 3.dp, bottomEnd = 3.dp),
                                            border = BorderStroke(1.5.dp, goldColor)
                                        ) {
                                            Text(
                                                text = "$currentMushafPage",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = greenDark,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            color = mushafBg,
                                            shape = RoundedCornerShape(5.dp),
                                            border = BorderStroke(1.dp, greenDark)
                                        ) {
                                            val juzNum = pageData?.juzNumber ?: QuranRepository.getJuzForPage(currentMushafPage)
                                            Text(
                                                text = "JUZ $juzNum",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = greenDark,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                                        drawLine(
                                            color = greenDark.copy(alpha = 0.25f),
                                            start = Offset(0f, size.height / 2),
                                            end = Offset(size.width, size.height / 2),
                                            strokeWidth = 0.7.dp.toPx()
                                        )
                                    }

                                    // 2. KONTEN 15 BARIS MUSHAF (Masing-masing baris mendapat weight(1f) = proporsional merata)
                                    if (isLoadingPage || pageData == null) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                CircularProgressIndicator(color = PrimaryTosca, strokeWidth = 2.5.dp, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "Memuat Halaman $currentMushafPage Mushaf Kemenag RI...",
                                                    fontSize = 11.5.sp,
                                                    color = greenDark
                                                )
                                            }
                                        }
                                    } else {
                                        val pData = pageData!!
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            pData.lines.forEach { line ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (line.isSurahHeader) {
                                                        // Kubah Kepala Surah
                                                        Surface(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .fillMaxHeight(0.92f),
                                                            color = Color(0xFFF1F8E9),
                                                            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                                            border = BorderStroke(1.2.dp, goldColor)
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.fillMaxSize(),
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.Center
                                                            ) {
                                                                Text(
                                                                    text = "\u0633\u064f\u0648\u0631\u064e\u0629\u064f ${line.surahNameArabic}",
                                                                    fontFamily = LpmqFontFamily,
                                                                    fontSize = 16.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = greenDark,
                                                                    textAlign = TextAlign.Center
                                                                )
                                                                if (line.surahNumber != 9) {
                                                                    Text(
                                                                        text = "\u0628\u0650\u0633\u0652\u0645\u0650 \u0627\u0644\u0644\u0651\u064e\u0647\u0650 \u0627\u0644\u0631\u0651\u064e\u062d\u0652\u0645\u064e\u0670\u0646\u0650 \u0627\u0644\u0631\u0651\u064e\u062d\u0650\u064a\u0645\u0650",
                                                                        fontFamily = LpmqFontFamily,
                                                                        fontSize = 13.5.sp,
                                                                        color = Color(0xFF1B5E20),
                                                                        textAlign = TextAlign.Center
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else if (line.words.isNotEmpty()) {
                                                        // 1 Baris Mushaf Presisi (Rata Penuh Justified, Tinggi Pas Tidak Terpotong)
                                                        Row(
                                                            modifier = Modifier.fillMaxSize(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            // Words in RTL order
                                                            line.words.reversed().forEach { word ->
                                                                val isBlocked = blockedVerseKey == word.verseKey
                                                                if (word.charTypeName == "end") {
                                                                    // Roset Tanda Ayat
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .padding(horizontal = 1.dp)
                                                                            .size(22.dp)
                                                                            .clickable {
                                                                                blockedVerseKey = if (blockedVerseKey == word.verseKey) null else word.verseKey
                                                                            },
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                                                            val r = size.minDimension / 2f
                                                                            drawCircle(color = if (isBlocked) AccentGold.copy(alpha = 0.35f) else rosetteBg, radius = r)
                                                                            drawCircle(color = rosetteStroke, radius = r, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                                                                            for (i in 0 until 8) {
                                                                                val angle = Math.toRadians(i * 45.0)
                                                                                val cx = (r + 2.dp.toPx()) * Math.cos(angle).toFloat() + r
                                                                                val cy = (r + 2.dp.toPx()) * Math.sin(angle).toFloat() + r
                                                                                drawCircle(color = rosetteStroke.copy(alpha = 0.5f), radius = 1.dp.toPx(), center = Offset(cx, cy))
                                                                            }
                                                                        }
                                                                        Text(
                                                                            text = toArabicDigits(word.verseNumber),
                                                                            fontSize = 7.5.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = rosetteTxt
                                                                        )
                                                                    }
                                                                } else {
                                                                    // Kata Mushaf Utsmani
                                                                    Surface(
                                                                        color = if (isBlocked) Color(0xFFFFF9C4) else Color.Transparent,
                                                                        shape = RoundedCornerShape(3.dp),
                                                                        modifier = Modifier.clickable {
                                                                            blockedVerseKey = if (blockedVerseKey == word.verseKey) null else word.verseKey
                                                                        }
                                                                    ) {
                                                                        Text(
                                                                            text = word.textUthmani,
                                                                            fontFamily = LpmqFontFamily,
                                                                            fontSize = 20.sp,
                                                                            fontWeight = FontWeight.Normal,
                                                                            color = if (isBlocked) Color(0xFF1B5E20) else Color(0xFF0F172A),
                                                                            modifier = Modifier.padding(horizontal = 0.5.dp)
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

                                    // Floating Toolbar Saat Ayat Diblok
                                    if (blockedVerseKey != null && pageData != null) {
                                        val vKey = blockedVerseKey!!
                                        val ayatInfo = pageData!!.versesMap[vKey]
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, greenLight.copy(alpha = 0.5f)),
                                            shadowElevation = 3.dp
                                        ) {
                                            Column(modifier = Modifier.padding(7.dp)) {
                                                Row(verticalAlignment = Alignment.Top) {
                                                    Surface(
                                                        color = greenDark,
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.padding(top = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "QS $vKey",
                                                            fontSize = 8.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = ayatInfo?.translation ?: "Terjemahan Kemenag RI",
                                                        fontSize = 10.sp,
                                                        lineHeight = 14.sp,
                                                        color = Color(0xFF1B5E20),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))
                                                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                                                Spacer(modifier = Modifier.height(3.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        FilledTonalButton(
                                                            onClick = {
                                                                if (ayatInfo != null) {
                                                                    selectedAyatForTafsir = ayatInfo
                                                                }
                                                            },
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                            modifier = Modifier.height(26.dp),
                                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                                containerColor = PrimaryTosca,
                                                                contentColor = Color.White
                                                            )
                                                        ) {
                                                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text("Tafsir", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                                        }

                                                        if (ayatInfo != null && ayatInfo.audioUrl.isNotBlank()) {
                                                            OutlinedButton(
                                                                onClick = { playAyatAudio(ayatInfo.audioUrl) },
                                                                shape = RoundedCornerShape(6.dp),
                                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                                modifier = Modifier.height(26.dp)
                                                            ) {
                                                                Icon(
                                                                    if (isPlayingAyatAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(12.dp),
                                                                    tint = greenDark
                                                                )
                                                                Spacer(modifier = Modifier.width(3.dp))
                                                                Text(if (isPlayingAyatAudio) "Stop" else "Putar", fontSize = 9.sp, color = greenDark)
                                                            }
                                                        }
                                                    }

                                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        if (ayatInfo != null) {
                                                            IconButton(
                                                                onClick = { copyAyat(ayatInfo, surah.name) },
                                                                modifier = Modifier.size(26.dp)
                                                            ) {
                                                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = greenDark, modifier = Modifier.size(13.dp))
                                                            }
                                                            IconButton(
                                                                onClick = { shareAyat(ayatInfo, surah.name) },
                                                                modifier = Modifier.size(26.dp)
                                                            ) {
                                                                Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = greenDark, modifier = Modifier.size(13.dp))
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = { blockedVerseKey = null },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.Gray, modifier = Modifier.size(13.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                                        drawLine(greenDark.copy(alpha = 0.22f), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 0.6.dp.toPx())
                                    }

                                    // 3. FOOTER HALAMAN (~5% tinggi)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Juz ${pageData?.juzNumber ?: QuranRepository.getJuzForPage(currentMushafPage)}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = greenDark.copy(alpha = 0.75f)
                                        )
                                        Text(
                                            text = "Hal. $currentMushafPage / 604",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = greenDark.copy(alpha = 0.75f)
                                        )
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
                        fontFamily = LpmqFontFamily,
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
        val textColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
        withStyle(SpanStyle(color = textColor, fontWeight = FontWeight.Normal)) {
            append(text)
        }
    }
}

/**
 * Paginates verses into fixed pages for true Mushaf Madinah / Kemenag reading.
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
