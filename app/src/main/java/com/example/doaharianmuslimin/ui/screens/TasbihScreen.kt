package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.DoaRepository
import com.example.doaharianmuslimin.data.model.Doa
import com.example.doaharianmuslimin.data.model.DoaCategory
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.LpmqFontFamily
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily

private val DzikirCardShape = RoundedCornerShape(14.dp)
private val DzikirBannerShape = RoundedCornerShape(12.dp)
private val SearchFieldShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Dzikir Pagi & Petang, 1 = Tasbih Digital
    var searchQuery by remember { mutableStateOf("") }

    fun vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    fun vibrateSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 70, 70, 70), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(80)
        }
    }

    val allDzikirList = remember { DoaRepository.dzikirList }

    val filteredDzikirList = remember(allDzikirList, searchQuery) {
        if (searchQuery.isBlank()) {
            allDzikirList
        } else {
            val q = searchQuery.trim().lowercase()
            allDzikirList.filter {
                it.title.lowercase().contains(q) ||
                it.arabic.contains(q) ||
                it.latin.lowercase().contains(q) ||
                it.translation.lowercase().contains(q) ||
                (!it.note.isNullOrBlank() && it.note.lowercase().contains(q))
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    TopAppBar(
                        windowInsets = WindowInsets(0.dp),
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo_kufi),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(34.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Adzkaruna",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = AccentGold
                                        ) {
                                            Text(
                                                text = "PRO",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Doa & Dzikir Harian Muslimin v1.55.0",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )

                    // Tab Navigation Row
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "Dzikir Pagi & Petang",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                            },
                            text = {
                                Text(
                                    "Tasbih Digital",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            icon = { Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }

                    // Sticky / Pinned Search Field di atas konten Tab Dzikir
                    if (selectedTab == 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        "Cari dzikir (Sayyidul Istighfar, latin, arti...)",
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = SearchFieldShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                // ==================== TAB 0: DZIKIR PAGI & PETANG ====================
                val doaCounters = remember { mutableStateMapOf<Int, Int>() }

                fun copyDzikir(doa: Doa) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val text = buildString {
                        append("${doa.title}\n\n")
                        append("${doa.arabic}\n\n")
                        if (doa.latin.isNotBlank()) append("${doa.latin}\n\n")
                        append("Artinya:\n\"${doa.translation}\"\n\n")
                        if (!doa.note.isNullOrBlank()) append("Fadhilah: ${doa.note}\n")
                        append("(Adzkaruna - Dzikir Pagi & Petang)")
                    }
                    clipboard.setPrimaryClip(ClipData.newPlainText("Dzikir", text))
                }

                fun shareDzikir(doa: Doa) {
                    val text = buildString {
                        append("🤲 ${doa.title}\n\n")
                        append("${doa.arabic}\n\n")
                        if (doa.latin.isNotBlank()) append("${doa.latin}\n\n")
                        append("Artinya:\n\"${doa.translation}\"\n\n")
                        if (!doa.note.isNullOrBlank()) append("Fadhilah: ${doa.note}\n")
                        append("\n📲 Dibagikan dari Adzkaruna - Doa & Dzikir Harian Muslimin")
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan Dzikir"))
                }

                if (filteredDzikirList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
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
                                text = "Dzikir Tidak Ditemukan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tidak ditemukan bacaan yang cocok dengan \"$searchQuery\"",
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { searchQuery = "" },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Tampilkan Semua Dzikir")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Info header banner
                        if (searchQuery.isBlank()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    shape = DzikirBannerShape,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Dzikir pagi dibaca setelah Subuh hingga Dhuha, dzikir petang dibaca setelah Ashar hingga terbenam matahari.",
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        items(
                            items = filteredDzikirList,
                            key = { it.id },
                            contentType = { "DzikirCard" }
                        ) { doa ->
                            val currentCount = doaCounters[doa.id] ?: 0
                            val targetCount = doa.repeatCount.coerceAtLeast(1)
                            val isCompleted = currentCount >= targetCount

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = DzikirCardShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCompleted) AccentGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCompleted) AccentGold.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Row atas: Badge & Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = if (isCompleted) AccentGold else MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Dibaca ${targetCount}x",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Row {
                                            IconButton(
                                                onClick = { copyDzikir(doa) },
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
                                                onClick = { shareDzikir(doa) },
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = doa.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Tulisan Arab
                                    Text(
                                        text = doa.arabic,
                                        fontFamily = LpmqFontFamily,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 36.sp,
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (doa.latin.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = doa.latin,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = Color(0xFF00695C)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = doa.translation,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!doa.note.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            color = AccentGold.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "💡 ${doa.note}",
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Interaksi Hitung Dzikir
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                vibrateShort()
                                                val next = currentCount + 1
                                                doaCounters[doa.id] = next
                                                if (next >= targetCount) {
                                                    vibrateSuccess()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isCompleted) AccentGold else MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.TouchApp,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isCompleted) "Selesai ($currentCount/$targetCount)" else "Hitung ($currentCount/$targetCount)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        if (currentCount > 0) {
                                            TextButton(
                                                onClick = { doaCounters[doa.id] = 0 },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Ulangi", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== TAB 1: TASBIH DIGITAL (RESPONSIVE) ====================
                var count by remember { mutableIntStateOf(0) }
                var target by remember { mutableIntStateOf(33) }
                var totalRounds by remember { mutableIntStateOf(0) }
                var vibrationEnabled by remember { mutableStateOf(true) }

                val dzikirOptions = listOf(
                    "سُبْحَانَ اللَّهِ" to "Subhanallah",
                    "الْحَمْدُ لِلَّهِ" to "Alhamdulillah",
                    "اللَّهُ أَكْبَرُ" to "Allahu Akbar",
                    "لَا إِلَهَ إِلَّا اللَّهُ" to "Laa ilaaha illallaah",
                    "أَسْتَغْفِرُ اللَّهَ" to "Astaghfirullah"
                )
                var selectedDzikirIndex by remember { mutableIntStateOf(0) }

                fun vibrate() {
                    if (!vibrationEnabled) return
                    vibrateShort()
                }

                fun increment() {
                    vibrate()
                    count++
                    if (target > 0 && count >= target) {
                        totalRounds++
                        count = 0
                        if (vibrationEnabled) {
                            vibrateSuccess()
                        }
                    }
                }

                val progress = if (target > 0) (count.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Dzikir Selection Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dzikirOptions[selectedDzikirIndex].first,
                                fontSize = 28.sp,
                                fontFamily = UthmanicFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dzikirOptions[selectedDzikirIndex].second,
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                dzikirOptions.forEachIndexed { index, pair ->
                                    FilterChip(
                                        selected = selectedDzikirIndex == index,
                                        onClick = { selectedDzikirIndex = index },
                                        label = { Text(pair.second.split(" ")[0], fontSize = 10.5.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Target Selector & Vibration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(33, 99, 100, 0).forEach { t ->
                                val isSelected = target == t
                                OutlinedButton(
                                    onClick = {
                                        target = t
                                        count = 0
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(if (t == 0) "Bebas" else "$t", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        IconButton(
                            onClick = { vibrationEnabled = !vibrationEnabled }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Getar",
                                tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // Circular Counter Button (Responsive size with sizeIn)
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 170.dp, minHeight = 170.dp, maxWidth = 210.dp, maxHeight = 210.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.primary)
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                increment()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (target > 0) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 8.dp,
                                color = AccentGold,
                                trackColor = Color.White.copy(alpha = 0.25f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$count",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            if (target > 0) {
                                Text(
                                    text = "Target: $target",
                                    fontSize = 12.5.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ketuk untuk Menghitung",
                                fontSize = 11.sp,
                                color = AccentGold
                            )
                        }
                    }

                    // Round summary and Reset button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Putaran Selesai: $totalRounds",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                count = 0
                                totalRounds = 0
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Hitungan", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
