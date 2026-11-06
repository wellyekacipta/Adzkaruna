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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.doaharianmuslimin.data.model.sanitizeAyahBrackets
import com.example.doaharianmuslimin.ui.components.*
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import kotlinx.coroutines.launch
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape

enum class BottomNavTab(val title: String) {
    QURAN("Quran"),
    EXPLORE("Explore"),
    HOME("Home"),
    DOA("Doa"),
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
    BELAJAR_ISLAM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentNavTab by remember { mutableStateOf(BottomNavTab.HOME) }
    var activeSubScreen by remember { mutableStateOf(ActiveSubScreen.NONE) }
    var selectedCategory by remember { mutableStateOf(DoaCategory.SEMUA) }
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
    val homeQuranHistory = remember(historyRefreshKey) { prefs.getQuranReadingHistory().firstOrNull() }
    val homeDoaHistory = remember(historyRefreshKey) { prefs.getDoaReadingHistory().firstOrNull() }
    val doaHistoryList = remember(historyRefreshKey) { prefs.getDoaReadingHistory() }
    val quranWeeklyStats = remember(historyRefreshKey) { prefs.getQuranWeeklyStats() }

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

    // Doa Pilihan Hari ini: Berdasarkan apa yang sudah difavoritkan user (Requirement 4)
    val dailyFeaturedDoa = remember(favoriteIds) {
        val favoritedDoas = DoaRepository.doaList.filter { favoriteIds.contains(it.id) }
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (favoritedDoas.isNotEmpty()) {
            favoritedDoas[dayOfYear % favoritedDoas.size]
        } else {
            DoaRepository.doaList[dayOfYear % DoaRepository.doaList.size]
        }
    }
    val isFeaturedFromFavorites = favoriteIds.isNotEmpty()

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
                appendLine(doa.arabic.sanitizeAyahBrackets())
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
                appendLine(doa.arabic.sanitizeAyahBrackets())
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

        val categoryFiltered = if (selectedCategory != DoaCategory.SEMUA && !filterOnlyFavorites) {
            baseList.filter { it.category == selectedCategory }
        } else {
            baseList
        }

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
            TasbihScreen(onBack = { activeSubScreen = ActiveSubScreen.NONE })
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
        ActiveSubScreen.NONE -> { /* Continue with Main Flow */ }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isQuranImmersive || currentNavTab != BottomNavTab.QURAN) {
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
                                text = "Doa & Dzikir Harian Muslimin v1.60.0",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Tombol pencarian hanya muncul di menu yang membutuhkan pencarian (Home & Doa), disembunyikan di Quran, Explore & Profil
                    if (currentNavTab == BottomNavTab.HOME || currentNavTab == BottomNavTab.DOA) {
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
                                AppThemeMode.RED -> Icons.Default.Palette
                                AppThemeMode.BLUE -> Icons.Default.Palette
                                AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            },
                            contentDescription = "Pengaturan Tema",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
            }
        },
        bottomBar = {
            if (!isQuranImmersive || currentNavTab != BottomNavTab.QURAN) {
            // Footer Navigation with 5 tabs as requested: Quran, Explore, Home, Doa, Profil
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Quran
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.QURAN,
                    onClick = {
                        currentNavTab = BottomNavTab.QURAN
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Quran"
                        )
                    },
                    label = { Text("Quran", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // 2. Explore
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.EXPLORE,
                    onClick = {
                        currentNavTab = BottomNavTab.EXPLORE
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Explore"
                        )
                    },
                    label = { Text("Explore", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // 3. Home (Active center tab)
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.HOME,
                    onClick = {
                        currentNavTab = BottomNavTab.HOME
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // 4. Doa
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.DOA,
                    onClick = {
                        currentNavTab = BottomNavTab.DOA
                        filterOnlyFavorites = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "Doa"
                        )
                    },
                    label = { Text("Doa", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // 5. Profil
                NavigationBarItem(
                    selected = currentNavTab == BottomNavTab.PROFIL,
                    onClick = {
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
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            }
        }
    ) { innerPadding ->
        val contentPadding = if (isQuranImmersive && currentNavTab == BottomNavTab.QURAN) PaddingValues(0.dp) else innerPadding
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
                        },
                        arabicFontStyle = currentArabicFontStyle
                    )
                }

                BottomNavTab.EXPLORE -> {
                    ExploreScreen(
                        onNavigateToPromises = { activeSubScreen = ActiveSubScreen.PROMISES },
                        onNavigateToPrayerTimes = { activeSubScreen = ActiveSubScreen.WAKTU_SHOLAT },
                        onNavigateToQibla = { activeSubScreen = ActiveSubScreen.QIBLAT }
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

                BottomNavTab.HOME, BottomNavTab.DOA -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Search bar when active or if query exists
                        if (isSearchActive || searchQuery.isNotEmpty()) {
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

                        // ==================== KHUSUS TAB HOME ====================
                        if (currentNavTab == BottomNavTab.HOME && searchQuery.isEmpty()) {
                            // 1. Doa Pilihan Hari Ini (Muncul berdasarkan apa yang difavoritkan user)
                            item(key = "daily_featured_doa", contentType = "daily_featured_doa") {
                                Column {
                                    if (isFeaturedFromFavorites) {
                                        Surface(
                                            color = AccentGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = "⭐ Doa Favorit Anda",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    DailyDoaBanner(
                                        doa = dailyFeaturedDoa,
                                        isFavorite = favoriteIds.contains(dailyFeaturedDoa.id),
                                        onFavoriteToggle = { toggleFavorite(dailyFeaturedDoa.id) },
                                        onClick = { selectedDoaForDetail = dailyFeaturedDoa }
                                    )
                                }
                            }

                            item(key = "main_menu_grid", contentType = "main_menu_grid") {
                                MainMenuGrid(
                                    onMenuItemClick = { menuItem ->
                                        when (menuItem) {
                                            MainMenuItem.DOA_HARIAN -> {
                                                currentNavTab = BottomNavTab.DOA
                                                filterOnlyFavorites = false
                                                filterOnlyHistory = false
                                                selectedCategory = DoaCategory.SEMUA
                                            }
                                            MainMenuItem.DZIKIR -> activeSubScreen = ActiveSubScreen.DZIKIR
                                            MainMenuItem.TOPIK_DOA -> activeSubScreen = ActiveSubScreen.TOPIK_DOA
                                            MainMenuItem.FAVORIT -> activeSubScreen = ActiveSubScreen.FAVORIT
                                            MainMenuItem.QIBLAT -> activeSubScreen = ActiveSubScreen.QIBLAT
                                            MainMenuItem.BELAJAR_ISLAM -> activeSubScreen = ActiveSubScreen.BELAJAR_ISLAM
                                        }
                                    }
                                )
                            }

                            // Quick Access: Terakhir Dibaca Al-Qur'an & Doa (Riwayat Nyata)
                            val quranHist = homeQuranHistory
                            val doaHist = homeDoaHistory
                            if (quranHist != null || doaHist != null) {
                                item(key = "quick_access_history", contentType = "quick_access_history") {
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
                                                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Aktivitas Terakhir Dibaca",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
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

                            // Statistik Membaca Al-Qur'an (Senin - Ahad) - Di bawah Aktivitas Terakhir Dibaca
                            item(key = "quran_weekly_stats", contentType = "quran_weekly_stats") {
                                QuranWeeklyStatsCard(
                                    weeklyStats = quranWeeklyStats,
                                    onNavigateToQuran = {
                                        currentNavTab = BottomNavTab.QURAN
                                    }
                                )
                            }
                            // DAFTAR 35 DOA SUDAH DIHILANGKAN DARI MENU UTAMA HOME SESUAI PERMINTAAN!
                        }

                        // ==================== KHUSUS TAB DOA ATAU KETIKA SEARCH AKTIF ====================
                        if (currentNavTab == BottomNavTab.DOA || searchQuery.isNotEmpty()) {
                            // Category Chips di tab Doa (Lengkap dengan tombol Favorit & Riwayat)
                            if (searchQuery.isEmpty()) {
                                item {
                                    CategoryChips(
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = {
                                            selectedCategory = it
                                            filterOnlyFavorites = false
                                            filterOnlyHistory = false
                                        },
                                        isFavoritesSelected = filterOnlyFavorites,
                                        onFavoritesClick = {
                                            filterOnlyFavorites = true
                                            filterOnlyHistory = false
                                        },
                                        isHistorySelected = filterOnlyHistory,
                                        onHistoryClick = {
                                            filterOnlyHistory = true
                                            filterOnlyFavorites = false
                                        }
                                    )
                                }
                            }

                            // Section header
                            item {
                                val headerTitle = when {
                                    searchQuery.isNotEmpty() -> "Hasil Pencarian (${displayedDoas.size})"
                                    filterOnlyHistory -> "Riwayat Doa yang Dibaca"
                                    filterOnlyFavorites -> "Doa Favorit Tersimpan (${displayedDoas.size})"
                                    else -> "Doa Sebelum Tidur dan Bangun Tidur (${displayedDoas.size})"
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = headerTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    if (filterOnlyFavorites || filterOnlyHistory) {
                                        TextButton(onClick = {
                                            filterOnlyFavorites = false
                                            filterOnlyHistory = false
                                            selectedCategory = DoaCategory.SEMUA
                                        }) {
                                            Text("Lihat Semua", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            if (filterOnlyHistory) {
                                if (doaHistoryList.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 40.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(64.dp)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = "Belum Ada Riwayat Baca Doa",
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Setiap doa yang Anda buka akan otomatis tercatat di sini lengkap dengan tanggal & jam.",
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${doaHistoryList.size} Riwayat Tersimpan",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            TextButton(
                                                onClick = { showConfirmClearDoaDialog = true },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteSweep,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Hapus Riwayat",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.error,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    items(doaHistoryList, key = { "${it.doaId}_${it.timestamp}" }) { item ->
                                        val foundDoa = DoaRepository.doaList.find { it.id == item.doaId }
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable {
                                                    if (foundDoa != null) {
                                                        selectedDoaForDetail = foundDoa
                                                    }
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.primaryContainer,
                                                        modifier = Modifier.size(38.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Default.History,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = item.title,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.5.sp,
                                                            maxLines = 1,
                                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                        )
                                                        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
                                                        Text(
                                                            text = "Dibaca: ${sdf.format(Date(item.timestamp))}",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            } else if (displayedDoas.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = if (filterOnlyFavorites) Icons.Default.BookmarkBorder else Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = if (filterOnlyFavorites)
                                                    "Belum ada doa yang difavoritkan"
                                                else
                                                    "Doa tidak ditemukan",
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (filterOnlyFavorites)
                                                    "Tandai doa dengan ikon penanda agar muncul di sini dan menjadi Doa Hari Ini"
                                                else
                                                    "Coba gunakan kata kunci pencarian yang lain",
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Tampilan Menu List Doa (Ringkas, rapi, bernomor & nyaman dibaca)
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
                    }
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
                                                    AppThemeMode.RED -> Icons.Default.Palette
                                                    AppThemeMode.BLUE -> Icons.Default.Palette
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
private fun HomeDoaListItem(
    index: Int,
    doa: Doa,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeItemShape)
            .clickable(onClick = onClick),
        shape = HomeItemShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
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

            Spacer(modifier = Modifier.width(6.dp))

            // Tombol Favorit
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isFavorite) "Hapus Favorit" else "Tambah Favorit",
                    tint = if (isFavorite) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Buka Doa",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
