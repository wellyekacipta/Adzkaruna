package com.example.doaharianmuslimin.service

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.doaharianmuslimin.data.QuranRepository
import com.example.doaharianmuslimin.data.model.AmbientSoundType
import com.example.doaharianmuslimin.data.model.MurotalQari
import com.example.doaharianmuslimin.data.model.MurotalQariRepository
import com.example.doaharianmuslimin.data.model.QuranSurah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

object MurotalPlayerManager {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressTrackerJob: Job? = null

    // Murotal MediaPlayer
    private var murotalPlayer: MediaPlayer? = null
    // Ambient Nature Sounds MediaPlayer
    private var ambientPlayer: MediaPlayer? = null

    // Murotal State
    var currentSurah by mutableStateOf(
        QuranRepository.allSurahMeta.find { it.number == 2 } ?: QuranRepository.allSurahMeta.first()
    )
        private set

    var currentQari by mutableStateOf<MurotalQari>(MurotalQariRepository.defaultQari)
        private set

    var isPlayingMurotal by mutableStateOf(false)
        private set

    var isBufferingMurotal by mutableStateOf(false)
        private set

    var currentPositionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(0L)
        private set

    var playbackErrorMessage by mutableStateOf<String?>(null)
        private set

    // Ambient State
    var activeAmbientSound by mutableStateOf<AmbientSoundType?>(null)
        private set

    var isAmbientLoading by mutableStateOf(false)
        private set

    var ambientVolume by mutableFloatStateOf(0.6f)
        private set

    val nextSurah: QuranSurah
        get() {
            val nextNum = if (currentSurah.number >= 114) 1 else currentSurah.number + 1
            return QuranRepository.allSurahMeta.find { it.number == nextNum } ?: QuranRepository.allSurahMeta.first()
        }

    val prevSurah: QuranSurah
        get() {
            val prevNum = if (currentSurah.number <= 1) 114 else currentSurah.number - 1
            return QuranRepository.allSurahMeta.find { it.number == prevNum } ?: QuranRepository.allSurahMeta.first()
        }

    fun getAudioUrl(qari: MurotalQari, surah: QuranSurah): String {
        return String.format(Locale.US, "https://cdn.equran.id/audio-full/%s/%03d.mp3", qari.audioPath, surah.number)
    }

    fun playOrPause() {
        if (isPlayingMurotal) {
            pauseMurotal()
        } else {
            playCurrentSurah()
        }
    }

    fun playCurrentSurah() {
        playbackErrorMessage = null
        val player = murotalPlayer

        // If player already exists and prepared, resume playback
        if (player != null) {
            try {
                player.start()
                isPlayingMurotal = true
                isBufferingMurotal = false
                startProgressTracker()
                return
            } catch (e: Exception) {
                // If resume fails, rebuild player below
            }
        }

        // Initialize and start streaming new audio
        initAndPlayMurotal(currentSurah, currentQari)
    }

    fun pauseMurotal() {
        try {
            murotalPlayer?.pause()
        } catch (_: Exception) {}
        isPlayingMurotal = false
        stopProgressTracker()
    }

    fun seekTo(positionMs: Long) {
        currentPositionMs = positionMs
        try {
            murotalPlayer?.seekTo(positionMs.toInt())
        } catch (_: Exception) {}
    }

    fun selectSurah(surah: QuranSurah, autoPlay: Boolean = true) {
        if (currentSurah.number == surah.number && murotalPlayer != null) {
            if (autoPlay && !isPlayingMurotal) {
                playCurrentSurah()
            }
            return
        }
        currentSurah = surah
        currentPositionMs = 0L
        durationMs = 0L
        if (autoPlay || isPlayingMurotal) {
            initAndPlayMurotal(surah, currentQari)
        } else {
            releaseMurotalPlayer()
        }
    }

    fun selectQari(qari: MurotalQari) {
        if (currentQari.id == qari.id) return
        currentQari = qari
        val wasPlaying = isPlayingMurotal
        val savedPos = currentPositionMs
        initAndPlayMurotal(currentSurah, qari, startPosition = savedPos, autoPlay = wasPlaying)
    }

    fun playNext() {
        selectSurah(nextSurah, autoPlay = true)
    }

    fun playPrevious() {
        if (currentPositionMs > 4000L) {
            seekTo(0L)
        } else {
            selectSurah(prevSurah, autoPlay = true)
        }
    }

    private fun initAndPlayMurotal(
        surah: QuranSurah,
        qari: MurotalQari,
        startPosition: Long = 0L,
        autoPlay: Boolean = true
    ) {
        releaseMurotalPlayer()
        isBufferingMurotal = true
        playbackErrorMessage = null

        val audioUrl = getAudioUrl(qari, surah)

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    isBufferingMurotal = false
                    durationMs = mp.duration.toLong().coerceAtLeast(0L)
                    if (startPosition > 0L && startPosition < durationMs) {
                        mp.seekTo(startPosition.toInt())
                    }
                    if (autoPlay) {
                        mp.start()
                        isPlayingMurotal = true
                        startProgressTracker()
                    }
                }
                setOnCompletionListener {
                    isPlayingMurotal = false
                    currentPositionMs = durationMs
                    stopProgressTracker()
                    // Auto-advance to next surah
                    playNext()
                }
                setOnErrorListener { _, what, extra ->
                    isBufferingMurotal = false
                    isPlayingMurotal = false
                    playbackErrorMessage = "Gagal memuat audio murotal ($what, $extra). Periksa koneksi internet."
                    stopProgressTracker()
                    true
                }
                prepareAsync()
            }
            murotalPlayer = player
        } catch (e: Exception) {
            isBufferingMurotal = false
            isPlayingMurotal = false
            playbackErrorMessage = "Error: ${e.localizedMessage ?: "Tidak dapat memutar audio"}"
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressTrackerJob = scope.launch {
            while (isActive) {
                try {
                    val p = murotalPlayer
                    if (p != null && p.isPlaying) {
                        currentPositionMs = p.currentPosition.toLong()
                        val dur = p.duration.toLong()
                        if (dur > 0L) durationMs = dur
                    }
                } catch (_: Exception) {}
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    private fun releaseMurotalPlayer() {
        stopProgressTracker()
        try {
            murotalPlayer?.stop()
            murotalPlayer?.release()
        } catch (_: Exception) {}
        murotalPlayer = null
        isPlayingMurotal = false
        isBufferingMurotal = false
    }

    // ==================== AMBIENT NATURE SOUND CONTROLS ====================

    fun toggleAmbientSound(soundType: AmbientSoundType) {
        if (activeAmbientSound == soundType) {
            // User tapped active sound -> turn off
            stopAmbientSound()
        } else {
            // User tapped different ambient sound
            startAmbientSound(soundType)
        }
    }

    fun startAmbientSound(soundType: AmbientSoundType) {
        releaseAmbientPlayer()
        activeAmbientSound = soundType
        isAmbientLoading = true

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(soundType.audioUrl)
                isLooping = true
                setVolume(ambientVolume, ambientVolume)
                setOnPreparedListener { mp ->
                    isAmbientLoading = false
                    mp.start()
                }
                setOnErrorListener { _, _, _ ->
                    isAmbientLoading = false
                    activeAmbientSound = null
                    true
                }
                prepareAsync()
            }
            ambientPlayer = player
        } catch (e: Exception) {
            isAmbientLoading = false
            activeAmbientSound = null
        }
    }

    fun stopAmbientSound() {
        activeAmbientSound = null
        releaseAmbientPlayer()
    }

    fun setAmbientVolumeLevel(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        ambientVolume = clamped
        try {
            ambientPlayer?.setVolume(clamped, clamped)
        } catch (_: Exception) {}
    }

    private fun releaseAmbientPlayer() {
        isAmbientLoading = false
        try {
            ambientPlayer?.stop()
            ambientPlayer?.release()
        } catch (_: Exception) {}
        ambientPlayer = null
    }

    fun releaseAll() {
        releaseMurotalPlayer()
        releaseAmbientPlayer()
    }

    fun formatDuration(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }
}
