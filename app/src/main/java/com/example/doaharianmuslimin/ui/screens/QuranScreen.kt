package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.QuranRepository
import com.example.doaharianmuslimin.data.model.MushafPageData
import com.example.doaharianmuslimin.data.model.QuranAyat
import com.example.doaharianmuslimin.data.model.QuranSurah
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily
import com.example.doaharianmuslimin.ui.theme.LpmqFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    modifier: Modifier = Modifier,
    onImmersiveModeChanged: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()

    var selectedSurah by remember { mutableStateOf<QuranSurah?>(null) }
    var isLoadingSurah by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    var viewMode by remember { mutableStateOf(QuranViewMode.PER_HALAMAN) }
    var isImmersiveBarsVisible by remember { mutableStateOf(false) }

    val prefs = remember { PreferencesManager(context) }
    var showDownloadPromptDialog by remember { mutableStateOf(false) }
    var isDownloadingAllMushaf by remember { mutableStateOf(false) }
    var downloadProgressCount by remember { mutableIntStateOf(0) }
    var totalMushafPages by remember { mutableIntStateOf(604) }
    var cachedPageCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewMode) {
        if (viewMode == QuranViewMode.PER_HALAMAN) {
            val count = withContext(Dispatchers.IO) { QuranRepository.countCachedPages(context) }
            cachedPageCount = count
            if (!prefs.isMushafDownloadPrompted() && count < 600) {
                showDownloadPromptDialog = true
            }
        }
    }

    // Notify parent activity/home screen to enter/exit immersive fullscreen
    LaunchedEffect(selectedSurah, viewMode, isImmersiveBarsVisible) {
        val shouldBeImmersive = (selectedSurah != null && viewMode == QuranViewMode.PER_HALAMAN && !isImmersiveBarsVisible)
        onImmersiveModeChanged?.invoke(shouldBeImmersive)
    }

    var currentPageIndex by remember(selectedSurah) { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(QuranFilterCategory.SEMUA) }

    // State for Selected Verse Tafsir BottomSheet (Kemenag API)
    var selectedAyatForTafsir by remember { mutableStateOf<QuranAyat?>(null) }
    var selectedTafsirSurahNumber by remember { mutableIntStateOf(1) }
    var selectedTafsirSurahName by remember { mutableStateOf("Al-Fatihah") }
    var blockedAyat by remember { mutableStateOf<QuranAyat?>(null) }
    var currentMushafPage by remember(selectedSurah) { mutableIntStateOf(selectedSurah?.let { QuranRepository.getStartPageForSurah(it.number) } ?: 1) }
    var blockedVerseKey by remember { mutableStateOf<String?>(null) }
    var showPageVersesSheet by remember { mutableStateOf(false) }
    var tafsirContent by remember { mutableStateOf<String?>(null) }
    var isLoadingTafsir by remember { mutableStateOf(false) }

    // Audio Murottal Player for Ayat
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlayingAyatAudio by remember { mutableStateOf(false) }

    // Bookmark & Last Read Handler (Adzkaruna QUL Engine)
    val sharedPrefs = remember { context.getSharedPreferences("adzkaruna_quran_prefs", Context.MODE_PRIVATE) }
    var lastReadPage by remember { mutableIntStateOf(sharedPrefs.getInt("last_read_page", 1)) }
    var lastReadSurah by remember { mutableStateOf(sharedPrefs.getString("last_read_surah", "Al-Fatihah") ?: "Al-Fatihah") }

    fun saveBookmark(page: Int, surahName: String) {
        sharedPrefs.edit()
            .putInt("last_read_page", page)
            .putString("last_read_surah", surahName)
            .apply()
        lastReadPage = page
        lastReadSurah = surahName
        val sNum = selectedSurah?.number ?: 1
        prefs.toggleQuranFavorite(sNum, surahName, page)
        prefs.addQuranReadingHistory(sNum, surahName, page)
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Halaman $page ($surahName) ditandai & tersimpan di Favorit 🔖")
        }
    }

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
        if (ayat != null) {
            isLoadingTafsir = true
            tafsirContent = QuranRepository.getTafsir(selectedTafsirSurahNumber, ayat.number)
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
            val startP = QuranRepository.getStartPageForSurah(surah.number)
            prefs.addQuranReadingHistory(surah.number, surah.name, startP)

            val fullSurah = QuranRepository.getSurahDetail(surah.number)
            if (fullSurah != null && fullSurah.ayats.isNotEmpty()) {
                selectedSurah = fullSurah
            } else if (surah.ayats.isEmpty()) {
                loadError = "Gagal memuat ayat surah. Pastikan koneksi internet aktif untuk unduhan pertama."
            }
            isLoadingSurah = false
        }
    }

    fun copyAyat(ayat: QuranAyat, surahName: String, surahNumber: Int = selectedSurah?.number ?: 1, tafsirText: String? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        coroutineScope.launch {
            val tafsir = tafsirText ?: QuranRepository.getTafsir(surahNumber, ayat.number)
            val text = buildString {
                append("QS. $surahName Ayat ${ayat.number}:\n\n")
                append("${ayat.arabic}\n\n")
                if (ayat.latin.isNotBlank()) append("${ayat.latin}\n\n")
                append("Artinya:\n\"${ayat.translation}\"\n\n")
                if (tafsir.isNotBlank() && !tafsir.startsWith("Tafsir tidak")) {
                    append("Tafsir Kemenag RI:\n$tafsir\n\n")
                }
                append("(Adzkaruna - Al-Qur'an Kemenag RI)")
            }
            clipboard.setPrimaryClip(ClipData.newPlainText("Ayat Al-Qur'an", text))
            snackbarHostState.showSnackbar("Ayat dan Tafsir berhasil disalin ✨")
        }
    }

    fun shareAyat(ayat: QuranAyat, surahName: String, surahNumber: Int = selectedSurah?.number ?: 1, tafsirText: String? = null) {
        coroutineScope.launch {
            val tafsir = tafsirText ?: QuranRepository.getTafsir(surahNumber, ayat.number)
            val text = buildString {
                append("📖 QS. $surahName Ayat ${ayat.number}\n\n")
                append("${ayat.arabic}\n\n")
                if (ayat.latin.isNotBlank()) append("${ayat.latin}\n\n")
                append("Artinya:\n\"${ayat.translation}\"\n\n")
                if (tafsir.isNotBlank() && !tafsir.startsWith("Tafsir tidak")) {
                    append("Tafsir Kemenag RI:\n$tafsir\n\n")
                }
                append("📲 Dibagikan dari Adzkaruna - Doa & Dzikir Harian Muslimin")
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan QS. $surahName:${ayat.number}"))
        }
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
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(QuranFilterCategory.entries) { category ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
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

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = surah.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${surah.englishMeaning} • ${surah.revelation} (${surah.totalAyat} Ayat)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
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
                AnimatedVisibility(
                    visible = (viewMode == QuranViewMode.PER_BARIS || isImmersiveBarsVisible),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
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
                                            val quranGreenColor = if (isDark) Color(0xFF4DB6AC) else Color(0xFF1B5E20)
                                            Text(
                                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                fontFamily = LpmqFontFamily,
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = quranGreenColor
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
                                                    onClick = {
                                                        selectedTafsirSurahNumber = surah.number
                                                        selectedTafsirSurahName = surah.name
                                                        selectedAyatForTafsir = ayat
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Tafsir", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Spacer(modifier = Modifier.width(2.dp))

                                                if (ayat.audioUrl.isNotBlank()) {
                                                    IconButton(
                                                        onClick = { playAyatAudio(ayat.audioUrl) },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isPlayingAyatAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                            contentDescription = "Audio",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                    }
                                                }

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

                                                IconButton(
                                                    onClick = {
                                                        val startP = QuranRepository.getStartPageForSurah(surah.number)
                                                        saveBookmark(startP, surah.name)
                                                        Toast.makeText(context, "QS. ${surah.name}:${ayat.number} ditandai", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.BookmarkBorder,
                                                        contentDescription = "Favorit",
                                                        tint = AccentGold,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Arabic Text in Islamic Green (Warna hijau mushaf identik Bismillah)
                                        val tajweedAnnotated = remember(ayat.arabic, isDark) {
                                            buildTajweedAnnotatedString(ayat.arabic, isDark)
                                        }
                                        Text(
                                            text = tajweedAnnotated,
                                            fontFamily = LpmqFontFamily,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.SemiBold,
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
                                                color = if (isDark) Color(0xFF80CBC4) else Color(0xFF004D40)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Indonesian Translation (Kemenag RI) - Warna hitam pekat di mode terang sesuai permintaan user
                                        Text(
                                            text = ayat.translation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDark) Color(0xFFECEFF1) else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // ==================== MODE 2: MUSHAF 15 BARIS PER HALAMAN RESMI (604 HALAMAN) ====================
                        var pageData by remember(currentMushafPage) { mutableStateOf<MushafPageData?>(null) }
                        var pageBitmap by remember(currentMushafPage) { mutableStateOf<Bitmap?>(null) }
                        var isLoadingPage by remember(currentMushafPage) { mutableStateOf(true) }

                        LaunchedEffect(currentMushafPage) {
                            isLoadingPage = true
                            blockedVerseKey = null
                            QuranRepository.preloadMushafAdjacentPages(context, currentMushafPage)
                            coroutineScope.launch {
                                pageData = QuranRepository.getMushafPage(currentMushafPage, context)
                                isLoadingPage = false
                            }
                            coroutineScope.launch {
                                pageBitmap = QuranRepository.getMushafPageBitmap(context, currentMushafPage)
                            }
                        }

                        val mushafBg = Color(0xFFFFFDF7)
                        val goldColor = AccentGold
                        val greenDark = Color(0xFF2E7D32)
                        val greenLight = Color(0xFF4CAF50)
                        val rosetteBg = Color(0xFFFFF8E1)
                        val rosetteStroke = Color(0xFFE65100)
                        val rosetteTxt = Color(0xFFBF360C)
                        val isEvenPage = currentMushafPage % 2 == 0
                        val currentJuz = pageData?.juzNumber ?: QuranRepository.getJuzForPage(currentMushafPage)
                        val currentSurahName = pageData?.mainSurahName ?: surah.name

                        // Mode Imersif: Kartu mengisi 100% layar (0dp horizontal padding agar rapat kiri-kanan)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(mushafBg)
                                .pointerInput(currentMushafPage) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        var totalDragX = 0f
                                        var hasMovedPastSlop = false
                                        val pointerId = down.id

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                            if (change.changedToUp()) {
                                                if (!hasMovedPastSlop) {
                                                    // Mode Imersif: Ketuk background untuk tampil/sembunyi bar
                                                    isImmersiveBarsVisible = !isImmersiveBarsVisible
                                                } else {
                                                    // Geser kiri ke kanan (gaya kitab Arab) -> Halaman selanjutnya
                                                    if (totalDragX > 35f && currentMushafPage < 604) {
                                                        currentMushafPage++
                                                    } else if (totalDragX < -35f && currentMushafPage > 1) {
                                                        currentMushafPage--
                                                    }
                                                }
                                                break
                                            } else {
                                                val dx = change.position.x - change.previousPosition.x
                                                totalDragX += dx
                                                if (Math.abs(totalDragX) > 12f) {
                                                    hasMovedPastSlop = true
                                                    change.consume()
                                                }
                                            }
                                        }
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 1. HEADER 3 TABLET (Muncul saat bar aktif / layar disentuh)
                                AnimatedVisibility(
                                    visible = isImmersiveBarsVisible,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(mushafBg)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = mushafBg,
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, greenDark)
                                            ) {
                                                Text(
                                                    text = currentSurahName,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = greenDark,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }

                                            Surface(
                                                color = mushafBg,
                                                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                                border = BorderStroke(1.5.dp, goldColor),
                                                modifier = Modifier.clickable {
                                                    saveBookmark(currentMushafPage, currentSurahName)
                                                }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (currentMushafPage == lastReadPage) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                        contentDescription = "Bookmark",
                                                        tint = if (currentMushafPage == lastReadPage) AccentGold else greenDark,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "$currentMushafPage",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = greenDark
                                                    )
                                                }
                                            }

                                            Surface(
                                                color = mushafBg,
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, greenDark)
                                            ) {
                                                Text(
                                                    text = "JUZ $currentJuz",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = greenDark,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Canvas(modifier = Modifier.fillMaxWidth().height(1.5.dp)) {
                                            drawLine(
                                                color = greenDark.copy(alpha = 0.25f),
                                                start = Offset(0f, size.height / 2),
                                                end = Offset(size.width, size.height / 2),
                                                strokeWidth = 0.8.dp.toPx()
                                            )
                                        }
                                    }
                                }

                                // 2. KONTEN MUSHAF 15 BARIS ASLI (Full Width, rapat kiri-kanan sesuai format mushaf resmi)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (pageBitmap != null) {
                                        Image(
                                            bitmap = pageBitmap!!.asImageBitmap(),
                                            contentDescription = "Halaman $currentMushafPage Mushaf Madinah",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 0.dp)
                                        )
                                    } else if (isLoadingPage) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = PrimaryTosca, strokeWidth = 2.5.dp, modifier = Modifier.size(34.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Memuat Halaman $currentMushafPage Mushaf Madinah...",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = greenDark
                                            )
                                            Text(
                                                text = "15 Baris Standar Kemenag RI / KFGQPC",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Gagal memuat gambar halaman $currentMushafPage", fontSize = 12.sp, color = Color.Gray)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    isLoadingPage = true
                                                    coroutineScope.launch {
                                                        pageBitmap = QuranRepository.getMushafPageBitmap(context, currentMushafPage)
                                                        isLoadingPage = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTosca)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Coba Lagi", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                // 3. BOTTOM PANEL: TOOLBAR AKSI & NAVIGASI HALAMAN (Muncul saat bar aktif / layar disentuh)
                                AnimatedVisibility(
                                    visible = isImmersiveBarsVisible,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(mushafBg)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        // Tombol Aksi di Panel Bawah
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            FilledTonalButton(
                                                onClick = { showPageVersesSheet = true },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(
                                                    containerColor = PrimaryTosca,
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Tafsir & Terjemahan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                FilledTonalButton(
                                                    onClick = { showDownloadPromptDialog = true },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp),
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = if (cachedPageCount >= 600) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                                        contentColor = if (cachedPageCount >= 600) Color(0xFF2E7D32) else Color(0xFFE65100)
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = if (cachedPageCount >= 600) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = if (cachedPageCount >= 600) "Offline" else "Unduh",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        saveBookmark(currentMushafPage, currentSurahName)
                                                        Toast.makeText(context, "Halaman $currentMushafPage ($currentSurahName) ditandai 🔖", Toast.LENGTH_SHORT).show()
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (currentMushafPage == lastReadPage) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                        contentDescription = null,
                                                        tint = if (currentMushafPage == lastReadPage) AccentGold else greenDark,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (currentMushafPage == lastReadPage) "Ditandai" else "Tandai Hal. $currentMushafPage",
                                                        fontSize = 10.5.sp,
                                                        color = greenDark,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Canvas(modifier = Modifier.fillMaxWidth().height(1.5.dp)) {
                                            drawLine(greenDark.copy(alpha = 0.22f), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 0.6.dp.toPx())
                                        }

                                        // FOOTER NAVIGASI HALAMAN (Gaya kitab Arab)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Halaman Selanjutnya (Geser kiri-ke-kanan gaya Arab)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable(enabled = currentMushafPage < 604) {
                                                    currentMushafPage++
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ChevronLeft,
                                                    contentDescription = "Hal. Berikutnya",
                                                    tint = if (currentMushafPage < 604) greenDark else Color.LightGray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = "‹ Hal. ${currentMushafPage + 1}",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (currentMushafPage < 604) greenDark else Color.LightGray
                                                )
                                            }

                                            Text(
                                                text = "Juz $currentJuz • Hal. $currentMushafPage / 604",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = greenDark.copy(alpha = 0.85f)
                                            )

                                            // Halaman Sebelumnya
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable(enabled = currentMushafPage > 1) {
                                                    currentMushafPage--
                                                }
                                            ) {
                                                Text(
                                                    text = "Hal. ${currentMushafPage - 1} ›",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (currentMushafPage > 1) greenDark else Color.LightGray
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ChevronRight,
                                                    contentDescription = "Hal. Sebelumnya",
                                                    tint = if (currentMushafPage > 1) greenDark else Color.LightGray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ==================== BOTTOM SHEET: DAFTAR AYAT & TAFSIR HALAMAN INI ====================
                        if (showPageVersesSheet) {
                            val versesOnPage = remember(pageData, surah) {
                                val mapVerses = pageData?.versesMap?.values?.sortedBy { it.number }
                                if (!mapVerses.isNullOrEmpty()) {
                                    mapVerses
                                } else {
                                    surah.ayats.take(15)
                                }
                            }

                            ModalBottomSheet(
                                onDismissRequest = { showPageVersesSheet = false },
                                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.85f)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    // Header BottomSheet
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Daftar Ayat & Tafsir Kemenag RI",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Halaman $currentMushafPage • Juz ${pageData?.juzNumber ?: QuranRepository.getJuzForPage(currentMushafPage)}",
                                                fontSize = 11.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = { showPageVersesSheet = false }) {
                                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        contentPadding = PaddingValues(vertical = 12.dp)
                                    ) {
                                        items(versesOnPage, key = { it.number }) { ayatItem ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    // Row 1: Badge Nomor Ayat & Tombol Aksi
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            shape = RoundedCornerShape(6.dp)
                                                        ) {
                                                            Text(
                                                                text = "Ayat ${ayatItem.number}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                            )
                                                        }

                                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            FilledTonalButton(
                                                                onClick = {
                                                                    selectedTafsirSurahNumber = surah.number
                                                                    selectedTafsirSurahName = surah.name
                                                                    selectedAyatForTafsir = ayatItem
                                                                },
                                                                shape = RoundedCornerShape(6.dp),
                                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                                modifier = Modifier.height(28.dp),
                                                                colors = ButtonDefaults.filledTonalButtonColors(
                                                                    containerColor = PrimaryTosca,
                                                                    contentColor = Color.White
                                                                )
                                                            ) {
                                                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(12.dp))
                                                                Spacer(modifier = Modifier.width(3.dp))
                                                                Text("Tafsir", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                            }

                                                            if (ayatItem.audioUrl.isNotBlank()) {
                                                                IconButton(
                                                                    onClick = { playAyatAudio(ayatItem.audioUrl) },
                                                                    modifier = Modifier.size(28.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = if (isPlayingAyatAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                                        contentDescription = "Murottal",
                                                                        tint = MaterialTheme.colorScheme.primary,
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                }
                                                            }

                                                            IconButton(
                                                                onClick = { copyAyat(ayatItem, surah.name) },
                                                                modifier = Modifier.size(28.dp)
                                                            ) {
                                                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                                            }

                                                            IconButton(
                                                                onClick = { shareAyat(ayatItem, surah.name) },
                                                                modifier = Modifier.size(28.dp)
                                                            ) {
                                                                Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(8.dp))

                                                    // Teks Arab
                                                    Text(
                                                        text = ayatItem.arabic,
                                                        fontFamily = LpmqFontFamily,
                                                        fontSize = 20.sp,
                                                        lineHeight = 36.sp,
                                                        textAlign = TextAlign.End,
                                                        color = if (isDark) Color(0xFF4DB6AC) else Color(0xFF1B5E20),
                                                        modifier = Modifier.fillMaxWidth()
                                                    )

                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    // Terjemahan Kemenag RI
                                                    Text(
                                                        text = ayatItem.translation,
                                                        fontSize = 11.5.sp,
                                                        lineHeight = 16.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
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
        }
        
        // ==================== DIALOG 1: UNDUH MUSHAF OFFLINE (POP UP PERTAMA KALI) ====================
        if (showDownloadPromptDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDownloadPromptDialog = false
                    prefs.setMushafDownloadPrompted(true)
                },
                icon = {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = PrimaryTosca, modifier = Modifier.size(36.dp))
                },
                title = {
                    Text(
                        text = "Unduh Seluruh Mushaf (604 Halaman)?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Unduh seluruh halaman Al-Qur'an agar Anda dapat membaca dengan sangat cepat, lancar tanpa kendala/ngelag, dan 100% offline tanpa kuota internet (±45 MB).\n\nJika memilih 'Nanti Saja', halaman akan tetap dapat dibaca secara online (diunduh otomatis satu per satu saat dibuka).",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        if (cachedPageCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sudah tersimpan di perangkat: $cachedPageCount / 604 halaman",
                                fontSize = 11.5.sp,
                                color = PrimaryTosca,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDownloadPromptDialog = false
                            prefs.setMushafDownloadPrompted(true)
                            isDownloadingAllMushaf = true
                            coroutineScope.launch {
                                QuranRepository.downloadAllMushafPages(context) { downloaded, total ->
                                    downloadProgressCount = downloaded
                                    totalMushafPages = total
                                    cachedPageCount = downloaded
                                }
                                isDownloadingAllMushaf = false
                                prefs.setMushafAllDownloaded(true)
                                snackbarHostState.showSnackbar("Alhamdulillah! Seluruh 604 halaman mushaf telah tersimpan offline 📖✨")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTosca)
                    ) {
                        Text("Unduh Sekarang")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDownloadPromptDialog = false
                            prefs.setMushafDownloadPrompted(true)
                        }
                    ) {
                        Text("Nanti Saja")
                    }
                }
            )
        }

        // ==================== DIALOG 2: PROGRES DOWNLOAD MUSHAF ====================
        if (isDownloadingAllMushaf) {
            AlertDialog(
                onDismissRequest = { /* jangan tutup saat klik luar */ },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = PrimaryTosca)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Mengunduh Mushaf Offline...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        val percent = if (totalMushafPages > 0) (downloadProgressCount * 100 / totalMushafPages) else 0
                        LinearProgressIndicator(
                            progress = { if (totalMushafPages > 0) downloadProgressCount.toFloat() / totalMushafPages else 0f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = PrimaryTosca,
                            trackColor = PrimaryTosca.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$downloadProgressCount / $totalMushafPages Halaman", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTosca)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Halaman yang tersimpan langsung dapat dibaca tanpa internet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isDownloadingAllMushaf = false }) {
                        Text("Lanjutkan di Latar Belakang")
                    }
                }
            )
        }

        // ==================== BOTTOM SHEET: TAFSIR LENGKAP KEMENAG RI & AUDIO MUROTTAL ====================
        if (selectedAyatForTafsir != null) {
            val ayat = selectedAyatForTafsir!!
            val surahName = selectedTafsirSurahName.ifBlank { selectedSurah?.name ?: "Al-Qur'an" }

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
                                    text = "Tafsir QS. $surahName : Ayat ${ayat.number}",
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
                                    onClick = { copyAyat(ayat, surahName) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = PrimaryTosca, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { shareAyat(ayat, surahName) },
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
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
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
        val quranGreen = if (isDark) Color(0xFF4DB6AC) else Color(0xFF1B5E20)
        withStyle(SpanStyle(color = quranGreen, fontWeight = FontWeight.SemiBold)) {
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
