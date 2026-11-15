package com.example.doaharianmuslimin.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.AppThemeMode
import com.example.doaharianmuslimin.data.DoaRepository
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.model.ArabicFontStyle
import com.example.doaharianmuslimin.data.model.Doa
import com.example.doaharianmuslimin.data.model.DoaCategory
import com.example.doaharianmuslimin.ui.components.*
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryToscaDark
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.doaharianmuslimin.data.IslamicDateHelper
import com.example.doaharianmuslimin.data.PrayerTimesRepository
import com.example.doaharianmuslimin.data.model.PrayerSchedule
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape

enum class BottomNavTab(val title: String) {
    QURAN("Quran"),
    TRACKER("Tracker"),
    HOME("Home"),
    MUROTAL("Murotal"),
    PROFIL("Profil")
}

enum class ActiveSubScreen {
    NONE,
    PROMISES,
    WAKTU_SHOLAT,
    QIBLAT,
    TOPIK_DOA,
    DZIKIR,
    FAVORIT,
    BELAJAR_ISLAM,
    DOA_HARIAN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()
    val isDark = currentThemeMode == AppThemeMode.DARK || (currentThemeMode == AppThemeMode.SYSTEM && isSystemDark)
    val prefs = remember { PreferencesManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentNavTab by remember { mutableStateOf(BottomNavTab.HOME) }
    var activeSubScreen by remember { mutableStateOf(ActiveSubScreen.NONE) }
    var selectedCategory by remember { mutableStateOf(DoaCategory.TIDUR) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var filterOnlyFavorites by remember { mutableStateOf(false) }
    var filterOnlyHistory by remember { mutableStateOf(false) }

    var favoriteIds by remember { mutableStateOf(prefs.getFavoriteIds()) }
    var currentArabicFontStyle by remember { mutableStateOf(prefs.getArabicFontStyle()) }
    var arabicFontSize by remember { mutableFloatStateOf(prefs.getArabicFontSize()) }
    var showLatin by remember { mutableStateOf(prefs.isShowLatin()) }
    var historyRefreshKey by remember { mutableIntStateOf(0) }

    // Auto-refresh real-time riwayat Al-Qur'an dan statistik saat kembali ke Tab Home atau saat sub-layar ditutup
    LaunchedEffect(currentNavTab, activeSubScreen) {
        if (currentNavTab == BottomNavTab.HOME && activeSubScreen == ActiveSubScreen.NONE) {
            historyRefreshKey++
        }
    }

    val homeQuranHistory = remember(historyRefreshKey) { prefs.getQuranReadingHistory().firstOrNull() }
    val homeDoaHistory = remember(historyRefreshKey) { prefs.getDoaReadingHistory().firstOrNull() }
    val doaHistoryList = remember(historyRefreshKey) { prefs.getDoaReadingHistory() }
    val quranWeeklyStats = remember(historyRefreshKey) { prefs.getQuranWeeklyStats() }

    // Prayer schedule, live countdown & tracker for Home Header
    var homePrayerSchedule by remember { mutableStateOf<PrayerSchedule?>(null) }
    var homeCountdownText by remember { mutableStateOf("00:00:00") }
    var homeNextPrayerName by remember { mutableStateOf("Subuh") }
    var homeNextPrayerTime by remember { mutableStateOf("--:--") }
    var prayerCheckinRefreshKey by remember { mutableIntStateOf(0) }

    val savedCityName = remember(activeSubScreen) { prefs.getSavedCityName() }
    val cleanCityDisplay = remember(savedCityName) {
        savedCityName
            .replace("KAB. ", "Kabupaten ")
            .replace("KABUPATEN ", "Kabupaten ")
            .replace("KOTA ", "Kota ")
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
    }

    LaunchedEffect(activeSubScreen) {
        val cityId = prefs.getSavedCityId()
        val cityName = prefs.getSavedCityName()
        homePrayerSchedule = PrayerTimesRepository.fetchKemenagSchedule(cityId, cityName)
    }

    LaunchedEffect(homePrayerSchedule) {
        while (true) {
            val (cd, name) = PrayerTimesRepository.getCountdownToNextPrayer(homePrayerSchedule)
            homeCountdownText = cd
            homeNextPrayerName = name
            val item = homePrayerSchedule?.items?.find { it.name == name }
            if (item != null) {
                homeNextPrayerTime = item.time
            }
            delay(1000)
        }
    }

    var selectedDoaForDetail by remember { mutableStateOf<Doa?>(null) }
    var isQuranImmersive by remember { mutableStateOf(false) }
    var quranTargetPage by remember { mutableStateOf<Int?>(null) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    var showConfirmClearDoaDialog by remember { mutableStateOf(false) }

    // Menangani tombol kembali sistem Android di seluruh aplikasi (kembali ke Home, lalu pop-up keluar)
    BackHandler(enabled = true) {
        when {
            showThemeDialog -> showThemeDialog = false
            selectedDoaForDetail != null -> selectedDoaForDetail = null
            isSearchActive -> {
                isSearchActive = false
                searchQuery = ""
            }
            activeSubScreen != ActiveSubScreen.NONE -> {
                activeSubScreen = ActiveSubScreen.NONE
            }
            currentNavTab != BottomNavTab.HOME -> {
                currentNavTab = BottomNavTab.HOME
                filterOnlyFavorites = false
                filterOnlyHistory = false
            }
            else -> {
                showExitConfirmationDialog = true
            }
        }
    }

    fun toggleFavorite(id: Int) {
        val isFav = prefs.toggleFavorite(id)
        favoriteIds = prefs.getFavoriteIds()
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                if (isFav) "Doa ditambahkan ke Favorit" else "Doa dihapus dari Favorit"
            )
        }
    }

    fun copyDoaToClipboard(doa: Doa) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            appendLine(doa.title)
            if (doa.arabic.isNotBlank()) {
                appendLine()
                appendLine(doa.arabic)
            }
            if (doa.latin.isNotBlank()) {
                appendLine()
                appendLine(doa.latin)
            }
            appendLine()
            appendLine("Artinya:")
            appendLine(doa.translation)
            appendLine()
            appendLine("Sumber: ${doa.source}")
        }
        val clip = ClipData.newPlainText("Doa Harian", text)
        clipboard.setPrimaryClip(clip)
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Teks doa berhasil disalin!")
        }
    }

    fun shareDoa(doa: Doa) {
        val text = buildString {
            appendLine("📖 *${doa.title}*")
            if (doa.arabic.isNotBlank()) {
                appendLine()
                appendLine(doa.arabic)
            }
            if (doa.latin.isNotBlank()) {
                appendLine()
                appendLine("_\"${doa.latin}\"_")
            }
            appendLine()
            appendLine("Artinya:")
            appendLine(doa.translation)
            appendLine()
            appendLine("📌 Sumber: ${doa.source}")
            appendLine()
            append("Dibagikan melalui Adzkaruna")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Doa Melalui"))
    }

    // Filter list according to category, search, and favorites (di-cache tanpa re-filter saat ganti tab)
    val displayedDoas = remember(selectedCategory, searchQuery, favoriteIds, filterOnlyFavorites) {
        val baseList = if (filterOnlyFavorites) {
            DoaRepository.doaList.filter { favoriteIds.contains(it.id) }
        } else {
            DoaRepository.doaList
        }

        val categoryFiltered = baseList

        if (searchQuery.isBlank()) {
            categoryFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            categoryFiltered.filter {
                it.title.lowercase().contains(q) ||
                it.latin.lowercase().contains(q) ||
                it.translation.lowercase().contains(q)
            }
        }
    }

        val homeBottomBar = @Composable {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = if (isDark) Color(0xFF80CBC4) else MaterialTheme.colorScheme.primary,
                selectedTextColor = if (isDark) Color(0xFF80CBC4) else MaterialTheme.colorScheme.primary,
                indicatorColor = Color.Transparent,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Quran
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.QURAN && activeSubScreen == ActiveSubScreen.NONE,
                    onClick = {
                        activeSubScreen = ActiveSubScreen.NONE
                        currentNavTab = BottomNavTab.QURAN
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_quran),
                            contentDescription = "Quran",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Quran", fontSize = 11.sp) },
                    colors = navItemColors
                )

                // 2. Tracker (Habit & Ibadah Tracker)
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.TRACKER && activeSubScreen == ActiveSubScreen.NONE,
                    onClick = {
                        activeSubScreen = ActiveSubScreen.NONE
                        currentNavTab = BottomNavTab.TRACKER
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_footer_tracker),
                            contentDescription = "Tracker",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Tracker", fontSize = 11.sp) },
                    colors = navItemColors
                )

                // 3. Home (Icon 10: Kubah Masjid)
                NavigationBarItem(
                    selected = (currentNavTab == BottomNavTab.HOME && activeSubScreen == ActiveSubScreen.NONE) || activeSubScreen == ActiveSubScreen.DZIKIR,
                    onClick = {
                        activeSubScreen = ActiveSubScreen.NONE
                        currentNavTab = BottomNavTab.HOME
                        filterOnlyFavorites = false
                        historyRefreshKey++
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_footer_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors
                )

                // 4. Murotal (Audio Murotal Al-Qur'an & Suara Alam)
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.MUROTAL && activeSubScreen == ActiveSubScreen.NONE,
                    onClick = {
                        activeSubScreen = ActiveSubScreen.NONE
                        currentNavTab = BottomNavTab.MUROTAL
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_footer_murotal),
                            contentDescription = "Murotal",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Murotal", fontSize = 11.sp) },
                    colors = navItemColors
                )

                // 5. Profil
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.PROFIL && activeSubScreen == ActiveSubScreen.NONE,
                    onClick = {
                        activeSubScreen = ActiveSubScreen.NONE
                        currentNavTab = BottomNavTab.PROFIL
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    },
                    label = { Text("Profil", fontSize = 11.sp) },
                    colors = navItemColors
                )
            }
        }

        // Handle Active Sub-Screens
        when (activeSubScreen) {
            ActiveSubScreen.PROMISES -> {
                PromisesScreen(onBack = { activeSubScreen = ActiveSubScreen.NONE })
                return
            }
            ActiveSubScreen.WAKTU_SHOLAT -> {
                PrayerTimesScreen(onBack = { activeSubScreen = ActiveSubScreen.NONE })
                return
            }
            ActiveSubScreen.QIBLAT -> {
                QiblaScreen(onBack = { activeSubScreen = ActiveSubScreen.NONE })
                return
            }
            ActiveSubScreen.TOPIK_DOA -> {
                TopicDoaScreen(
                    onBack = {
                        selectedDoaForDetail = null
                        activeSubScreen = ActiveSubScreen.NONE
                    },
                    onSelectDoa = {},
                    favoriteIds = favoriteIds,
                    onToggleFavorite = { toggleFavorite(it) },
                    onCopyDoa = { copyDoaToClipboard(it) },
                    onShareDoa = { shareDoa(it) }
                )
                return
            }
            ActiveSubScreen.DZIKIR -> {
                TasbihScreen(
                    onBack = { activeSubScreen = ActiveSubScreen.NONE },
                    bottomBar = homeBottomBar
                )
                return
            }
        ActiveSubScreen.FAVORIT -> {
            FavoritesScreen(
                onBack = { activeSubScreen = ActiveSubScreen.NONE },
                onSelectDoa = { selectedDoaForDetail = it },
                onNavigateToQuranPage = { page ->
                    activeSubScreen = ActiveSubScreen.NONE
                    quranTargetPage = page
                    currentNavTab = BottomNavTab.QURAN
                },
                favoriteIds = favoriteIds,
                onToggleFavorite = { toggleFavorite(it) }
            )
            return
        }
        ActiveSubScreen.BELAJAR_ISLAM -> {
            ExploreScreen(
                onBack = { activeSubScreen = ActiveSubScreen.NONE },
                onNavigateToPromises = { activeSubScreen = ActiveSubScreen.PROMISES },
                onNavigateToPrayerTimes = { activeSubScreen = ActiveSubScreen.WAKTU_SHOLAT },
                onNavigateToQibla = { activeSubScreen = ActiveSubScreen.QIBLAT }
            )
            return
        }
        ActiveSubScreen.DOA_HARIAN -> {
            DoaHarianScreen(
                onBack = { activeSubScreen = ActiveSubScreen.NONE },
                favoriteIds = favoriteIds,
                onToggleFavorite = { toggleFavorite(it) },
                onSelectDoa = { doa ->
                    prefs.addDoaReadingHistory(doa.id, doa.title)
                    historyRefreshKey++
                    selectedDoaForDetail = doa
                }
            )
            return
        }
        ActiveSubScreen.NONE -> { /* Continue with Main Flow */ }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if ((!isQuranImmersive || currentNavTab != BottomNavTab.QURAN) && currentNavTab != BottomNavTab.MUROTAL && currentNavTab != BottomNavTab.TRACKER) {
                TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo_kufi),
                            contentDescription = "Logo Adzkaruna",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Adzkaruna",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AccentGold, shape = RoundedCornerShape(3.dp))
                                        .padding(horizontal = 3.dp, vertical = 0.5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF3E2723),
                                        letterSpacing = 0.2.sp,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                                            lineHeight = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Doa & Dzikir Harian Muslimin",
                                fontSize = 9.5.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Tombol pencarian hanya muncul di menu yang membutuhkan pencarian (Home), disembunyikan di Doa, Quran, Explore & Profil
                    if (currentNavTab == BottomNavTab.HOME) {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = when (currentThemeMode) {
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                                AppThemeMode.RED -> Icons.Default.LightMode
                                AppThemeMode.BLUE -> Icons.Default.LightMode
                                AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            },
                            contentDescription = "Pengaturan Tema",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) PrimaryToscaDark else MaterialTheme.colorScheme.primary
                )
            )
            }
        },
        bottomBar = {
            if ((!isQuranImmersive || currentNavTab != BottomNavTab.QURAN) && currentNavTab != BottomNavTab.MUROTAL) {
                homeBottomBar()
            }
        }
    ) { innerPadding ->
        val contentPadding = if ((isQuranImmersive && currentNavTab == BottomNavTab.QURAN) || currentNavTab == BottomNavTab.MUROTAL) {
            PaddingValues(0.dp)
        } else if (currentNavTab == BottomNavTab.TRACKER) {
            PaddingValues(bottom = innerPadding.calculateBottomPadding())
        } else {
            innerPadding
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (currentNavTab) {
                BottomNavTab.QURAN -> {
                    QuranScreen(
                        initialTargetPage = quranTargetPage,
                        onResetTargetPage = { quranTargetPage = null },
                        onNavigateToHome = {
                            currentNavTab = BottomNavTab.HOME
                        },
                        onImmersiveModeChanged = { immersive ->
                            isQuranImmersive = immersive
                        }
                    )
                }

                BottomNavTab.TRACKER -> {
                    TrackerScreen(
                        onBack = { currentNavTab = BottomNavTab.HOME },
                        onNavigateToQuran = { currentNavTab = BottomNavTab.QURAN },
                        onNavigateToTasbih = { activeSubScreen = ActiveSubScreen.DZIKIR }
                    )
                }

                BottomNavTab.PROFIL -> {
                    ProfileScreen(
                        currentThemeMode = currentThemeMode,
                        onOpenThemeDialog = { showThemeDialog = true },
                        totalFavorites = favoriteIds.size,
                        currentArabicFontStyle = currentArabicFontStyle,
                        onArabicFontStyleChange = { newStyle ->
                            currentArabicFontStyle = newStyle
                        },
                        onSelectDoa = { doaId ->
                            DoaRepository.doaList.find { it.id == doaId }?.let {
                                selectedDoaForDetail = it
                            }
                        },
                        onNavigateToQuran = {
                            currentNavTab = BottomNavTab.QURAN
                        }
                    )
                }

                BottomNavTab.HOME -> {
                    if (searchQuery.isNotEmpty()) {
                        // Tampilan hasil pencarian menggunakan LazyColumn (virtualized list)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (isSearchActive) {
                                item {
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Cari judul doa, arti, atau lafadz...") },
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
                                        shape = RoundedCornerShape(16.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = "Hasil Pencarian (${displayedDoas.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            if (displayedDoas.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Doa tidak ditemukan",
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Coba gunakan kata kunci pencarian yang lain",
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                itemsIndexed(
                                    items = displayedDoas,
                                    key = { _, doa -> doa.id },
                                    contentType = { _, _ -> "HomeDoaListItem" }
                                ) { index, doa ->
                                    HomeDoaListItem(
                                        index = index,
                                        doa = doa,
                                        isFavorite = favoriteIds.contains(doa.id),
                                        onFavoriteToggle = { toggleFavorite(doa.id) },
                                        onClick = {
                                            prefs.addDoaReadingHistory(doa.id, doa.title)
                                            historyRefreshKey++
                                            selectedDoaForDetail = doa
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Tampilan Dashboard Utama Menggunakan Column + verticalScroll Sesuai Layout Desain
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            // Search bar when active
                            if (isSearchActive) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Cari judul doa, arti, atau lafadz...") },
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
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            }

                            // 1. Sapaan Pengguna & Tanggal Islami (Masehi + Hijriyah)
                            val userName = remember {
                                val n = prefs.getUserName()
                                if (n.isBlank()) "Hamba Allah" else n
                            }
                            val todayMasehi = remember { IslamicDateHelper.getTodayMasehi() }
                            val todayHijri = remember { IslamicDateHelper.getTodayHijri() }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Assalamu'alaikum, $userName",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Terverifikasi",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "$todayMasehi · $todayHijri",
                                    fontSize = 9.5.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 2. Hero Card: Jadwal Salat Berikutnya & Absen Sholat
                            val todayDateKey = remember {
                                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
                            }
                            val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
                            val prayerToTrack = remember(currentHour) {
                                when {
                                    currentHour < 12 -> "Subuh"
                                    currentHour < 15 -> "Dzuhur"
                                    currentHour < 18 -> "Ashar"
                                    currentHour < 20 -> "Maghrib"
                                    else -> "Isya"
                                }
                            }
                            var isPrayerChecked by remember(prayerCheckinRefreshKey) {
                                mutableStateOf(prefs.isPrayerCheckedIn(todayDateKey, prayerToTrack))
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp)),
                                shape = RoundedCornerShape(22.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Brush.verticalGradient(
                                                colors = if (isDark) {
                                                    listOf(PrimaryToscaDark, PrimaryToscaDark)
                                                } else {
                                                    listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.90f)
                                                    )
                                                }
                                            )
                                        )
                                        .padding(20.dp)
                                ) {
                                    Column {
                                        // Clickable upper part to open PrayerTimesScreen
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { activeSubScreen = ActiveSubScreen.WAKTU_SHOLAT },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "SALAT BERIKUTNYA",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.85f),
                                                    letterSpacing = 1.sp
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val prayerIcon = when (homeNextPrayerName.lowercase(Locale.ROOT)) {
                                                        "subuh" -> Icons.Default.Brightness3
                                                        "dzuhur" -> Icons.Default.WbSunny
                                                        "ashar" -> Icons.Default.Brightness5
                                                        "maghrib" -> Icons.Default.Brightness4
                                                        "isya" -> Icons.Default.NightlightRound
                                                        else -> Icons.Default.WbSunny
                                                    }
                                                    Icon(
                                                        imageVector = prayerIcon,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(19.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = homeNextPrayerName,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = homeCountdownText,
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White,
                                                    letterSpacing = 1.sp
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "$homeNextPrayerTime · $cleanCityDisplay",
                                                    fontSize = 11.5.sp,
                                                    color = Color.White.copy(alpha = 0.88f)
                                                )
                                            }

                                            // Logo Adzkaruna Kufi di samping hitungan mundur sholat (persis seperti foto)
                                            Image(
                                                painter = painterResource(id = R.drawable.app_logo_kufi),
                                                contentDescription = "Logo Adzkaruna",
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .padding(end = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(18.dp))

                                        // Absen Sholat row inside the card
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color.White.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (isPrayerChecked) Icons.Default.CheckCircle else Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = if (isPrayerChecked) AccentGold else Color.White,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = "Absen $prayerToTrack",
                                                            fontSize = 13.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                        Text(
                                                            text = if (isPrayerChecked) "Sudah dicatat ✓" else "Belum dicatat",
                                                            fontSize = 11.sp,
                                                            color = if (isPrayerChecked) AccentGold else Color.White.copy(alpha = 0.8f)
                                                        )
                                                    }
                                                }

                                                // Capsule button
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = if (isPrayerChecked) AccentGold else Color.White,
                                                    modifier = Modifier.clickable {
                                                        val nextState = !isPrayerChecked
                                                        prefs.setPrayerCheckedIn(todayDateKey, prayerToTrack, nextState)
                                                        isPrayerChecked = nextState
                                                        prayerCheckinRefreshKey++
                                                    }
                                                ) {
                                                    Text(
                                                        text = if (isPrayerChecked) "Tercatat ✓" else "Catat",
                                                        color = if (isPrayerChecked) Color.White else MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. Grid 8 Menu Utama (4 Kolom x 2 Baris)
                            MainMenuGrid(
                                onMenuItemClick = { menuItem ->
                                    when (menuItem) {
                                        MainMenuItem.DOA_HARIAN -> {
                                            activeSubScreen = ActiveSubScreen.DOA_HARIAN
                                            filterOnlyFavorites = false
                                            filterOnlyHistory = false
                                            selectedCategory = DoaCategory.TIDUR
                                        }
                                        MainMenuItem.QURAN -> {
                                            currentNavTab = BottomNavTab.QURAN
                                        }
                                        MainMenuItem.DZIKIR -> activeSubScreen = ActiveSubScreen.DZIKIR
                                        MainMenuItem.WAKTU_SHOLAT -> activeSubScreen = ActiveSubScreen.WAKTU_SHOLAT
                                        MainMenuItem.QIBLAT -> activeSubScreen = ActiveSubScreen.QIBLAT
                                        MainMenuItem.TOPIK_DOA -> activeSubScreen = ActiveSubScreen.TOPIK_DOA
                                        MainMenuItem.FAVORIT -> activeSubScreen = ActiveSubScreen.FAVORIT
                                        MainMenuItem.BELAJAR_ISLAM -> activeSubScreen = ActiveSubScreen.BELAJAR_ISLAM
                                    }
                                }
                            )

                            // 4. Baris 3 Kartu Tracker & Statistik - Menyesuaikan 3 Warna Tema
                            val todayStat = remember(historyRefreshKey, quranWeeklyStats) {
                                quranWeeklyStats.days.find { it.isToday }
                            }
                            val todayPagesRead = todayStat?.pagesRead ?: 0
                            val todayStreak = quranWeeklyStats.currentStreak
                            val todayXp = remember(todayPagesRead, isPrayerChecked, historyRefreshKey) {
                                (todayPagesRead * 10) + (if (isPrayerChecked) 20 else 0) + (if (homeDoaHistory != null) 5 else 0)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Kartu 1: Hari Beruntun (Streak)
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = Color(0xFFFF6D00),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$todayStreak",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "hari beruntun",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Kartu 2: XP Hari Ini (Theme Adaptive)
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "+$todayXp",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "XP hari ini",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Kartu 3: Ngaji Hari Ini (Theme Adaptive)
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { currentNavTab = BottomNavTab.QURAN },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_menu_quran),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$todayPagesRead/5 hlm",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "ngaji hari ini",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // 5. Kartu Kutipan Hari Ini (Hadis) - Theme Adaptive
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "KUTIPAN HARI INI",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.8.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = "HADIS",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(7.dp))

                                    Text(
                                        text = "“Sesungguhnya setiap amalan tergantung pada niatnya.”",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = "HR. Al-Bukhari no. 1 & Muslim no. 1907",
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // 6. Quick Access: Tilawah Terakhir Al-Qur'an & Doa (Riwayat Nyata)
                            val quranHist = homeQuranHistory
                            val doaHist = homeDoaHistory
                            if (quranHist != null || doaHist != null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "Tilawah Terakhir",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.5.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    val latestTimestamp = maxOf(quranHist?.timestamp ?: 0L, doaHist?.timestamp ?: 0L)
                                                    val formattedDateTime = remember(latestTimestamp) {
                                                        val ts = if (latestTimestamp > 0L) latestTimestamp else System.currentTimeMillis()
                                                        val sdf = java.text.SimpleDateFormat("HH:mm, dd MMMM yyyy", java.util.Locale.forLanguageTag("id-ID"))
                                                        sdf.format(java.util.Date(ts))
                                                    }
                                                    Text(
                                                        text = formattedDateTime,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            if (quranHist != null) {
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            quranTargetPage = quranHist.pageNumber
                                                            currentNavTab = BottomNavTab.QURAN
                                                        },
                                                    color = MaterialTheme.colorScheme.surface,
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text("Al-Qur'an", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            quranHist.surahName,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            maxLines = 1,
                                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                        )
                                                        Text("Hal. ${quranHist.pageNumber} ›", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                            }

                                            if (doaHist != null) {
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            DoaRepository.doaList.find { it.id == doaHist.doaId }?.let {
                                                                selectedDoaForDetail = it
                                                            }
                                                        },
                                                    color = MaterialTheme.colorScheme.surface,
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text("Doa Harian", fontSize = 10.5.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            doaHist.title,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            maxLines = 1,
                                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                        )
                                                        Text("Buka Doa ›", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.primary)
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

                // ==================== KHUSUS TAB MUROTAL ====================
                BottomNavTab.MUROTAL -> {
                    MurotalScreen(
                        onBack = {
                            currentNavTab = BottomNavTab.HOME
                        },
                        onOpenQuran = { _ ->
                            currentNavTab = BottomNavTab.QURAN
                        }
                    )
                }
            }
        }

        // Theme Selection Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = {
                    Text(
                        text = "Pilih Mode Tampilan",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeMode.entries.forEach { mode ->
                            val isSelected = currentThemeMode == mode
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onThemeModeChange(mode)
                                        showThemeDialog = false
                                    },
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when (mode) {
                                            AppThemeMode.LIGHT -> Color(0xFF00897B)
                                            AppThemeMode.DARK -> Color(0xFF143330)
                                            AppThemeMode.RED -> Color(0xFFC62828)
                                            AppThemeMode.BLUE -> Color(0xFF1565C0)
                                            AppThemeMode.SYSTEM -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = when (mode) {
                                                    AppThemeMode.LIGHT -> Icons.Default.LightMode
                                                    AppThemeMode.DARK -> Icons.Default.DarkMode
                                                    AppThemeMode.RED -> Icons.Default.LightMode
                                                    AppThemeMode.BLUE -> Icons.Default.LightMode
                                                    AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                                },
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = mode.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onThemeModeChange(mode)
                                            showThemeDialog = false
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Tutup", color = MaterialTheme.colorScheme.primary)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // Doa Detail Modal Bottom Sheet
        selectedDoaForDetail?.let { doa ->
            DoaDetailBottomSheet(
                doa = doa,
                isFavorite = favoriteIds.contains(doa.id),
                onFavoriteToggle = { toggleFavorite(doa.id) },
                onDismiss = { selectedDoaForDetail = null },
                onCopy = { copyDoaToClipboard(doa) },
                onShare = { shareDoa(doa) },
                arabicFontSize = arabicFontSize,
                onFontSizeChange = { newSize ->
                    arabicFontSize = newSize
                    prefs.setArabicFontSize(newSize)
                },
                showLatin = showLatin,
                onToggleLatin = { newShow ->
                    showLatin = newShow
                    prefs.setShowLatin(newShow)
                },
                arabicFontFamily = currentArabicFontStyle.fontFamily
            )
        }

        // Dialog Konfirmasi Hapus Riwayat Doa
        if (showConfirmClearDoaDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmClearDoaDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus Riwayat Doa?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        "Semua daftar riwayat doa yang pernah Anda buka akan dibersihkan. Doa favorit Anda tetap aman tersimpan.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            prefs.clearDoaReadingHistory()
                            historyRefreshKey++
                            showConfirmClearDoaDialog = false
                            Toast.makeText(context, "Riwayat Doa telah dibersihkan", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmClearDoaDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Dialog Konfirmasi Keluar dari Aplikasi
        if (showExitConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showExitConfirmationDialog = false },
                icon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = "Keluar dari Aplikasi?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "Apakah Anda yakin ingin keluar dari aplikasi Adzkaruna?",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitConfirmationDialog = false
                            var act: Activity? = context as? Activity
                            var ctx = context
                            while (act == null && ctx is ContextWrapper) {
                                ctx = ctx.baseContext
                                act = ctx as? Activity
                            }
                            act?.finish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Keluar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showExitConfirmationDialog = false },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal", fontWeight = FontWeight.SemiBold)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

private val HomeItemShape = RoundedCornerShape(14.dp)
private val NumberBadgeShape = CircleShape

@Composable
fun HomeDoaListItem(
    index: Int,
    doa: Doa,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeItemShape)
            .clickable(onClick = onClick),
        shape = HomeItemShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Container badge nomor urut
            Surface(
                shape = NumberBadgeShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Container judul doa & info sumber
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doa.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                val subtitle = if (doa.arabic.isBlank()) {
                    "Keutamaan & Adab Sebelum Tidur"
                } else if (doa.source.isNotBlank()) {
                    doa.source
                } else {
                    doa.translation.take(50)
                }
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
