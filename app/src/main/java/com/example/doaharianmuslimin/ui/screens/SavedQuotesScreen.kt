package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.doaharianmuslimin.data.QuoteRepository
import com.example.doaharianmuslimin.data.model.QuoteItem
import com.example.doaharianmuslimin.ui.theme.AccentGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedQuotesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val repo = remember { QuoteRepository(context) }
    val isDark = isSystemInDarkTheme()

    var refreshKey by remember { mutableIntStateOf(0) }
    val savedQuotes = remember(refreshKey) { repo.getSavedQuotes() }
    val dailyQuotes = QuoteRepository.defaultQuotes

    // Dialog state
    var showAddQuoteSheet by remember { mutableStateOf(false) }
    var quoteToExport by remember { mutableStateOf<QuoteItem?>(null) }

    // New quote input state
    var newQuoteText by remember { mutableStateOf("") }
    var newQuoteSource by remember { mutableStateOf("") }

    // Auto-sliding pager for the Top Banner
    val pagerState = rememberPagerState(pageCount = { dailyQuotes.size })
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (dailyQuotes.isNotEmpty()) {
                val next = (pagerState.currentPage + 1) % dailyQuotes.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Kutipan", text))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Kutipan berhasil disalin!")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kutipan tersimpan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                    IconButton(onClick = {
                        newQuoteText = ""
                        newQuoteSource = ""
                        showAddQuoteSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Kutipan Baru",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==================== 1. TOP BANNER: KUTIPAN HARI INI CAROUSEL ====================
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val quote = dailyQuotes[page]
                        val isSaved = remember(refreshKey, quote.id) { repo.isQuoteSaved(quote.id) }

                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row: Label + Category + Action Buttons (Bookmark & Share)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "KUTIPAN HARI INI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = quote.category,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Bookmark Button
                                    IconButton(
                                        onClick = {
                                            repo.toggleSaveQuote(quote)
                                            refreshKey++
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (!isSaved) "Kutipan disimpan ke daftar tersimpan 🔖" else "Kutipan dihapus dari tersimpan"
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Simpan",
                                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Share Button
                                    IconButton(
                                        onClick = { quoteToExport = quote },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Bagikan",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quote Text
                            Text(
                                text = "“${quote.text}”",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = quote.source,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Dot Indicators
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    dailyQuotes.indices.forEach { index ->
                                        val isCurrent = index == page
                                        Box(
                                            modifier = Modifier
                                                .size(if (isCurrent) 8.dp else 6.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isCurrent) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==================== 2. LIST KUTIPAN TERSIMPAN ====================
            if (savedQuotes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Belum ada kutipan tersimpan.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tekan ikon bookmark di Kutipan hari ini, atau tulis kutipanmu sendiri lewat tombol + di atas.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Koleksi Tersimpan (${savedQuotes.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(savedQuotes, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (item.isCustom) AccentGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = if (item.isCustom) "KUTIPAN SAYA" else item.category,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isCustom) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { copyToClipboard("“${item.text}” — ${item.source}") },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(17.dp))
                                    }
                                    IconButton(
                                        onClick = { quoteToExport = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            repo.deleteQuote(item.id)
                                            refreshKey++
                                            coroutineScope.launch { snackbarHostState.showSnackbar("Kutipan dihapus") }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "“${item.text}”",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.source,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== 3. MODAL BOTTOM SHEET: KUTIPAN BARU (SESUAI GAMBAR 2) ====================
    if (showAddQuoteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddQuoteSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Kutipan baru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Isi kutipan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newQuoteText,
                        onValueChange = { newQuoteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        placeholder = { Text("Tulis kutipan yang ingin kamu simpan...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sumber (opsional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newQuoteSource,
                        onValueChange = { newQuoteSource = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Mis. HR. Bukhari 6114", fontSize = 13.sp) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (newQuoteText.isNotBlank()) {
                            repo.addCustomQuote(newQuoteText, newQuoteSource)
                            refreshKey++
                            showAddQuoteSheet = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Kutipan berhasil disimpan!")
                            }
                        }
                    },
                    enabled = newQuoteText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // ==================== 4. EXPORT & SHARE DIALOG (SESUAI GAMBAR 1) ====================
    if (quoteToExport != null) {
        QuoteExportDialog(
            quote = quoteToExport!!,
            repo = repo,
            onDismiss = { quoteToExport = null }
        )
    }
}

@Composable
fun QuoteExportDialog(
    quote: QuoteItem,
    repo: QuoteRepository,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(QuoteRepository.ExportColorTheme.BIRU) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Bagikan Kutipan", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Preview Card Sesuai Gambar 1
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(selectedTheme.bgColor),
                    border = BorderStroke(1.dp, Color(selectedTheme.textColor).copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top: Header
                        Text(
                            text = "KUTIPAN HARI INI • ${quote.category.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(selectedTheme.textColor),
                            letterSpacing = 0.8.sp
                        )

                        // Center: Quote Text
                        Column {
                            Text(
                                text = "“${quote.text}”",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedTheme.textColor),
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = quote.source,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(selectedTheme.subTextColor)
                            )
                        }

                        // Bottom: Adzkaruna Brand
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Adzkaruna",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedTheme.textColor)
                            )
                            Text(
                                text = "Doa & Dzikir Harian",
                                fontSize = 10.sp,
                                color = Color(selectedTheme.subTextColor)
                            )
                        }
                    }
                }

                // Pilihan Warna Background Export (Biru, Tosca, Merah, Gelap)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Pilih Warna Kartu:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuoteRepository.ExportColorTheme.entries.forEach { theme ->
                            val isSelected = theme == selectedTheme
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(theme.bgColor),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedTheme = theme }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = theme.title,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = Color(theme.textColor),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    repo.shareQuoteImage(quote, selectedTheme)
                    onDismiss()
                }
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bagikan Gambar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    repo.shareQuoteText(quote)
                    onDismiss()
                }
            ) {
                Text("Bagikan Teks")
            }
        }
    )
}
