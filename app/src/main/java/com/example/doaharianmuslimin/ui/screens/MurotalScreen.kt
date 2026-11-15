package com.example.doaharianmuslimin.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.doaharianmuslimin.data.QuranRepository
import com.example.doaharianmuslimin.data.model.AmbientSoundType
import com.example.doaharianmuslimin.data.model.MurotalQari
import com.example.doaharianmuslimin.data.model.MurotalQariRepository
import com.example.doaharianmuslimin.data.model.QuranSurah
import com.example.doaharianmuslimin.service.MurotalPlayerManager
import com.example.doaharianmuslimin.ui.components.MurotalIllustration
import kotlin.math.roundToInt

// Custom Dark & Warm Amber Theme Colors matching the Reference UI
private val DarkBg = Color(0xFF191614)
private val DarkSurface = Color(0xFF231E1A)
private val DarkSurfaceCard = Color(0xFF2A241F)
private val DarkBorder = Color(0xFF3C342C)
private val AmberAccent = Color(0xFFEAA649)
private val TextWhite = Color(0xFFFBF8F3)
private val TextMuted = Color(0xFF9E968D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MurotalScreen(
    onBack: () -> Unit,
    onOpenQuran: (surahNumber: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSurah = MurotalPlayerManager.currentSurah
    val currentQari = MurotalPlayerManager.currentQari
    val isPlaying = MurotalPlayerManager.isPlayingMurotal
    val isBuffering = MurotalPlayerManager.isBufferingMurotal
    val currentPosMs = MurotalPlayerManager.currentPositionMs
    val durationMs = MurotalPlayerManager.durationMs
    val activeAmbient = MurotalPlayerManager.activeAmbientSound
    val isAmbientLoading = MurotalPlayerManager.isAmbientLoading
    val ambientVol = MurotalPlayerManager.ambientVolume
    val nextSurah = MurotalPlayerManager.nextSurah
    val errorMessage = MurotalPlayerManager.playbackErrorMessage

    var showSurahSheet by remember { mutableStateOf(false) }
    var showQariSheet by remember { mutableStateOf(false) }
    var showVolumeDialog by remember { mutableStateOf(false) }

    // User dragging slider state
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragProgressMs by remember { mutableFloatStateOf(0f) }

    val effectiveSliderValue = if (isDraggingSlider) {
        dragProgressMs
    } else {
        currentPosMs.toFloat()
    }

    val sliderMax = durationMs.toFloat().coerceAtLeast(1f)

    val isRainActive = activeAmbient == AmbientSoundType.HUJAN

    // Sesuaikan warna status bar / top area ke warna Dark Murotal (#191614) saat masuk layar ini
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as? Activity)?.window
            val insetsController = window?.let { WindowCompat.getInsetsController(it, view) }
            val originalStatusBarColor = window?.statusBarColor
            val originalLightStatusBars = insetsController?.isAppearanceLightStatusBars

            window?.statusBarColor = DarkBg.toArgb()
            insetsController?.isAppearanceLightStatusBars = false

            onDispose {
                if (originalStatusBarColor != null && window != null) {
                    window.statusBarColor = originalStatusBarColor
                }
                if (originalLightStatusBars != null && insetsController != null) {
                    insetsController.isAppearanceLightStatusBars = originalLightStatusBars
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==================== TOP BAR ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { onOpenQuran(currentSurah.number) },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Baca Al-Qur'an",
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ==================== COZY NIGHT ARTWORK (NO CAT) ====================
            MurotalIllustration(
                isPlaying = isPlaying,
                isRainActive = isRainActive,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // ==================== SURAH & QARI INFO ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Surah Title, Translation, Qari
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Surah Name with Dropdown Arrow
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSurahSheet = true }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentSurah.name,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pilih Surah",
                            tint = TextWhite.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Meaning / Translation underneath
                    Text(
                        text = currentSurah.englishMeaning,
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 1.dp, bottom = 4.dp)
                    )

                    // Qari selector with Dropdown Arrow
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showQariSheet = true }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentQari.name,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pilih Qari",
                            tint = TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Right Column: Arabic Script
                Text(
                    text = currentSurah.arabicName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== AUDIO SEEKBAR & TIMERS ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Slider(
                    value = effectiveSliderValue.coerceIn(0f, sliderMax),
                    onValueChange = { newVal ->
                        isDraggingSlider = true
                        dragProgressMs = newVal
                    },
                    onValueChangeFinished = {
                        isDraggingSlider = false
                        MurotalPlayerManager.seekTo(dragProgressMs.toLong())
                    },
                    valueRange = 0f..sliderMax,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberAccent,
                        activeTrackColor = AmberAccent,
                        inactiveTrackColor = Color(0xFF4A4036)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = MurotalPlayerManager.formatDuration(effectiveSliderValue.toLong()),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = if (durationMs > 0L) MurotalPlayerManager.formatDuration(durationMs) else "--:--",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Error notice if stream fails
            if (errorMessage != null) {
                Surface(
                    color = Color(0xFF3E1C1C),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = errorMessage,
                        fontSize = 11.5.sp,
                        color = Color(0xFFFF8A80),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== MEDIA PLAYER CONTROLS ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                IconButton(
                    onClick = { MurotalPlayerManager.playPrevious() },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Sebelumnya",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Big Circular Play/Pause Button
                Surface(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .clickable { MurotalPlayerManager.playOrPause() },
                    shape = CircleShape,
                    color = AmberAccent,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                color = DarkBg,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (isPlaying) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Jeda",
                                tint = DarkBg,
                                modifier = Modifier.size(36.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Putar",
                                tint = DarkBg,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // Next Button
                IconButton(
                    onClick = { MurotalPlayerManager.playNext() },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Berikutnya",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Next Track Subtitle Indicator
            Text(
                text = "Berikutnya: ${nextSurah.name}",
                fontSize = 12.5.sp,
                color = TextMuted,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ==================== SUARA ALAM (AMBIENT SOUNDS) ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Suara alam",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Hujan
                    AmbientSoundButton(
                        title = AmbientSoundType.HUJAN.title,
                        icon = Icons.Default.WaterDrop,
                        isActive = activeAmbient == AmbientSoundType.HUJAN,
                        isLoading = isAmbientLoading && activeAmbient == AmbientSoundType.HUJAN,
                        onClick = { MurotalPlayerManager.toggleAmbientSound(AmbientSoundType.HUJAN) }
                    )

                    // 2. Api
                    AmbientSoundButton(
                        title = AmbientSoundType.API.title,
                        icon = Icons.Default.LocalFireDepartment,
                        isActive = activeAmbient == AmbientSoundType.API,
                        isLoading = isAmbientLoading && activeAmbient == AmbientSoundType.API,
                        onClick = { MurotalPlayerManager.toggleAmbientSound(AmbientSoundType.API) }
                    )

                    // 3. Jangkrik
                    AmbientSoundButton(
                        title = AmbientSoundType.JANGKRIK.title,
                        icon = Icons.Default.Bedtime,
                        isActive = activeAmbient == AmbientSoundType.JANGKRIK,
                        isLoading = isAmbientLoading && activeAmbient == AmbientSoundType.JANGKRIK,
                        onClick = { MurotalPlayerManager.toggleAmbientSound(AmbientSoundType.JANGKRIK) }
                    )

                    // 4. Sungai
                    AmbientSoundButton(
                        title = AmbientSoundType.SUNGAI.title,
                        icon = Icons.Default.Waves,
                        isActive = activeAmbient == AmbientSoundType.SUNGAI,
                        isLoading = isAmbientLoading && activeAmbient == AmbientSoundType.SUNGAI,
                        onClick = { MurotalPlayerManager.toggleAmbientSound(AmbientSoundType.SUNGAI) }
                    )

                    // 5. Volume
                    AmbientSoundButton(
                        title = "Volume",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        isActive = showVolumeDialog,
                        isLoading = false,
                        onClick = { showVolumeDialog = true }
                    )
                }
            }
        }

        // ==================== SURAH SELECTION BOTTOM SHEET ====================
        if (showSurahSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSurahSheet = false },
                containerColor = DarkSurface,
                scrimColor = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                var searchSurahQuery by remember { mutableStateOf("") }
                val filteredSurahs = remember(searchSurahQuery) {
                    if (searchSurahQuery.isBlank()) {
                        QuranRepository.allSurahMeta
                    } else {
                        val q = searchSurahQuery.trim().lowercase()
                        QuranRepository.allSurahMeta.filter {
                            it.name.lowercase().contains(q) ||
                            it.number.toString().contains(q) ||
                            it.englishMeaning.lowercase().contains(q) ||
                            it.arabicName.contains(q)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Pilih Surah (1-114)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = searchSurahQuery,
                        onValueChange = { searchSurahQuery = it },
                        placeholder = { Text("Cari surah atau nomor...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberAccent) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceCard,
                            unfocusedContainerColor = DarkSurfaceCard,
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSurahs, key = { it.number }) { surah ->
                            val isSelected = surah.number == currentSurah.number
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        MurotalPlayerManager.selectSurah(surah, autoPlay = true)
                                        showSurahSheet = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AmberAccent.copy(alpha = 0.2f) else DarkSurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) AmberAccent else DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Surah Number badge
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(
                                                    if (isSelected) AmberAccent else Color(0xFF382F27),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = surah.number.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) DarkBg else TextWhite
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = surah.name,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) AmberAccent else TextWhite
                                            )
                                            Text(
                                                text = "${surah.englishMeaning} • ${surah.totalAyat} Ayat",
                                                fontSize = 11.5.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    Text(
                                        text = surah.arabicName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AmberAccent else TextWhite,
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== QARI SELECTION BOTTOM SHEET ====================
        if (showQariSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQariSheet = false },
                containerColor = DarkSurface,
                scrimColor = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp)
                ) {
                    Text(
                        text = "Pilih Qari / Pelafal Murotal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(MurotalQariRepository.qariList, key = { it.id }) { qari ->
                            val isSelected = qari.id == currentQari.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        MurotalPlayerManager.selectQari(qari)
                                        showQariSheet = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AmberAccent.copy(alpha = 0.2f) else DarkSurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) AmberAccent else DarkBorder)
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
                                                    if (isSelected) AmberAccent else Color(0xFF382F27),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RecordVoiceOver,
                                                contentDescription = null,
                                                tint = if (isSelected) DarkBg else TextWhite,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = qari.name,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) AmberAccent else TextWhite
                                            )
                                            Text(
                                                text = qari.subtitle,
                                                fontSize = 11.5.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Terpilih",
                                            tint = AmberAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== AMBIENT VOLUME DIALOG ====================
        if (showVolumeDialog) {
            AlertDialog(
                onDismissRequest = { showVolumeDialog = false },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Text(
                        text = "Volume Suara Alam",
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${(ambientVol * 100).roundToInt()}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Slider(
                            value = ambientVol,
                            onValueChange = { MurotalPlayerManager.setAmbientVolumeLevel(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberAccent,
                                activeTrackColor = AmberAccent,
                                inactiveTrackColor = DarkBorder
                            )
                        )

                        Text(
                            text = "Sesuaikan volume latar belakang alam untuk relaksasi saat mendengarkan ayat suci.",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showVolumeDialog = false }) {
                        Text("Tutup", color = AmberAccent, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

/**
 * Circular Nature Sound Button item matching the UI screenshot
 */
@Composable
private fun AmbientSoundButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = if (isActive) AmberAccent else DarkSurfaceCard,
            border = if (isActive) null else BorderStroke(1.dp, DarkBorder)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = if (isActive) DarkBg else AmberAccent,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isActive) DarkBg else TextWhite.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            fontSize = 11.5.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) AmberAccent else TextMuted
        )
    }
}
