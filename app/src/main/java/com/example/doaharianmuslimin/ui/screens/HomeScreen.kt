package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.AppThemeMode
import com.example.doaharianmuslimin.data.CommunityPrayerRepository
import com.example.doaharianmuslimin.data.DoaRepository
import com.example.doaharianmuslimin.data.PreferencesManager
import com.example.doaharianmuslimin.data.model.Doa
import com.example.doaharianmuslimin.data.model.DoaCategory
import com.example.doaharianmuslimin.ui.components.*
import com.example.doaharianmuslimin.ui.theme.AccentGold
import kotlinx.coroutines.launch
import java.util.Calendar

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
    TOPIK_DOA
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
    val communityRepo = remember { CommunityPrayerRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentNavTab by remember { mutableStateOf(BottomNavTab.HOME) }
    var activeSubScreen by remember { mutableStateOf(ActiveSubScreen.NONE) }
    var selectedCategory by remember { mutableStateOf(DoaCategory.SEMUA) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCreatePrayerSheet by remember { mutableStateOf(false) }
    var filterOnlyFavorites by remember { mutableStateOf(false) }

    var favoriteIds by remember { mutableStateOf(prefs.getFavoriteIds()) }
    var arabicFontSize by remember { mutableFloatStateOf(prefs.getArabicFontSize()) }
    var showLatin by remember { mutableStateOf(prefs.isShowLatin()) }
    val communityPrayers by communityRepo.prayersFlow.collectAsState()

    var selectedDoaForDetail by remember { mutableStateOf<Doa?>(null) }

    // Intercept Back Press if inside a subscreen
    BackHandler(enabled = activeSubScreen != ActiveSubScreen.NONE) {
        activeSubScreen = ActiveSubScreen.NONE
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
            appendLine()
            appendLine(doa.arabic)
            appendLine()
            appendLine(doa.latin)
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
            appendLine()
            appendLine(doa.arabic)
            appendLine()
            appendLine("_\"${doa.latin}\"_")
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

    // Filter list according to category, search, and favorites
    val displayedDoas = remember(currentNavTab, selectedCategory, searchQuery, favoriteIds, filterOnlyFavorites) {
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
                onBack = { activeSubScreen = ActiveSubScreen.NONE },
                onSelectDoa = { selectedDoaForDetail = it },
                favoriteIds = favoriteIds,
                onToggleFavorite = { toggleFavorite(it) },
                onCopyDoa = { copyDoaToClipboard(it) },
                onShareDoa = { shareDoa(it) }
            )
            return
        }
        ActiveSubScreen.NONE -> { /* Continue with Main Flow */ }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Adzkaruna",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Doa & Dzikir Harian Muslimin v.1",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = when (currentThemeMode) {
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
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
        },
        bottomBar = {
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentNavTab) {
                BottomNavTab.QURAN -> {
                    QuranScreen()
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
                        totalFavorites = favoriteIds.size
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
                            item {
                                Column {
                                    if (isFeaturedFromFavorites) {
                                        Surface(
                                            color = AccentGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = "⭐ Dipilih Otomatis dari Koleksi Favorit Anda",
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

                            // 2. Menu Utama (6 Kotak Menu Sesuai Gambar)
                            item {
                                Text(
                                    text = "Menu Utama",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            item {
                                MainMenuGrid(
                                    onMenuItemClick = { menuItem ->
                                        when (menuItem) {
                                            MainMenuItem.PROMISES -> activeSubScreen = ActiveSubScreen.PROMISES
                                            MainMenuItem.WAKTU_SHOLAT -> activeSubScreen = ActiveSubScreen.WAKTU_SHOLAT
                                            MainMenuItem.DOA_DOA -> {
                                                currentNavTab = BottomNavTab.DOA
                                                filterOnlyFavorites = false
                                            }
                                            MainMenuItem.TOPIK_DOA -> activeSubScreen = ActiveSubScreen.TOPIK_DOA
                                            MainMenuItem.QIBLAT -> activeSubScreen = ActiveSubScreen.QIBLAT
                                            MainMenuItem.FAVORIT -> {
                                                currentNavTab = BottomNavTab.DOA
                                                filterOnlyFavorites = true
                                            }
                                        }
                                    }
                                )
                            }

                            // 3. Aamiinkan Doa Saudaramu (Dipindah ke bawah Menu Utama)
                            item {
                                AmiinkanDoaSection(
                                    prayers = communityPrayers,
                                    onArrowClick = { showCreatePrayerSheet = true },
                                    onToggleAamiin = { prayerId ->
                                        communityRepo.toggleAamiin(prayerId)
                                    },
                                    onToggleLike = { prayerId ->
                                        communityRepo.toggleLike(prayerId)
                                    }
                                )
                            }
                            // DAFTAR 35 DOA SUDAH DIHILANGKAN DARI MENU UTAMA HOME SESUAI PERMINTAAN!
                        }

                        // ==================== KHUSUS TAB DOA ATAU KETIKA SEARCH AKTIF ====================
                        if (currentNavTab == BottomNavTab.DOA || searchQuery.isNotEmpty()) {
                            // Category Chips di tab Doa
                            if (searchQuery.isEmpty()) {
                                item {
                                    CategoryChips(
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = {
                                            selectedCategory = it
                                            filterOnlyFavorites = false
                                        }
                                    )
                                }
                            }

                            // Section header
                            item {
                                val headerTitle = when {
                                    searchQuery.isNotEmpty() -> "Hasil Pencarian (${displayedDoas.size})"
                                    filterOnlyFavorites -> "Doa Favorit Tersimpan (${displayedDoas.size})"
                                    selectedCategory != DoaCategory.SEMUA -> "${selectedCategory.title} (${displayedDoas.size})"
                                    else -> "Koleksi Lengkap Doa (${displayedDoas.size})"
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

                                    if (filterOnlyFavorites) {
                                        TextButton(onClick = { filterOnlyFavorites = false }) {
                                            Text("Lihat Semua", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Empty State
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
                                // Doa Cards
                                items(displayedDoas, key = { it.id }) { doa ->
                                    DoaCard(
                                        doa = doa,
                                        isFavorite = favoriteIds.contains(doa.id),
                                        onFavoriteToggle = { toggleFavorite(doa.id) },
                                        onCopy = { copyDoaToClipboard(doa) },
                                        onShare = { shareDoa(doa) },
                                        onClick = { selectedDoaForDetail = doa }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Prayer Sheet (from + button in Aamiinkan Doa)
        if (showCreatePrayerSheet) {
            CreatePrayerBottomSheet(
                onDismiss = { showCreatePrayerSheet = false },
                onSubmit = { name, location, text ->
                    communityRepo.addPrayer(name, location, text)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Doa berhasil dikirim dan kini dapat diaminkan sesama!")
                    }
                }
            )
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
                                    Icon(
                                        imageVector = when (mode) {
                                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                                            AppThemeMode.DARK -> Icons.Default.DarkMode
                                            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                }
            )
        }
    }
}
