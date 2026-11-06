package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.ui.theme.AccentGold

@Immutable
data class IslamicInsight(
    val id: String,
    val title: String,
    val category: String,
    val content: String,
    val source: String,
    val keywords: List<String>
)

private val StaticInsights = listOf(
    IslamicInsight(
        id = "hadits_senjata",
        title = "Doa Adalah Senjata Mukmin",
        category = "Mutiara Hadits",
        content = "\"Doa adalah senjata orang mukmin, tiang penyangga agama, dan cahaya penerang langit dan bumi.\"",
        source = "HR. Abu Ya'la, Al-Hakim, & Al-Qudha'i",
        keywords = listOf("senjata", "mukmin", "tiang", "agama", "cahaya", "langit", "bumi", "hadits")
    ),
    IslamicInsight(
        id = "mustajab_waktu",
        title = "5 Waktu Paling Mustajab Berdoa",
        category = "Waktu Utama",
        content = "1. Sepertiga malam terakhir (Waktu Shalat Tahajjud)\n2. Di antara Adzan dan Iqamah\n3. Saat sujud dalam shalat fardhu maupun sunnah\n4. Saat turun hujan lebat membawa rahmat\n5. Menjelang berbuka puasa dan di hari Jum'at bakda Ashar",
        source = "Berdasarkan Hadits Shahih Bukhari & Muslim",
        keywords = listOf("mustajab", "waktu", "tahajjud", "malam", "adzan", "iqamah", "sujud", "hujan", "puasa", "jumat")
    ),
    IslamicInsight(
        id = "adab_berdoa",
        title = "4 Adab Pokok Saat Berdoa",
        category = "Adab & Sunnah",
        content = "1. Ikhlas & menghadap arah kiblat\n2. Memulai dengan memuji Allah (Hamdalah) & bershalawat kepada Nabi SAW\n3. Mengangkat kedua telapak tangan setinggi dada\n4. Merendahkan suara dengan penuh harap (khauf wa raja') dan yakin dikabulkan",
        source = "Tuntunan Rasulullah SAW",
        keywords = listOf("adab", "sunnah", "kiblat", "shalawat", "hamdalah", "tangan", "yakin", "ikhlas")
    ),
    IslamicInsight(
        id = "dzikir_perisai",
        title = "Perisai & Ketenangan Jiwa Melalui Dzikir",
        category = "Keutamaan Dzikir",
        content = "\"Ingatlah, hanya dengan mengingat Allah hati akan menjadi tentram dan tenang.\" Dzikir pagi dan petang melindungi seorang muslim dari godaan syaitan, marabahaya, serta kegelisahan batin.",
        source = "QS. Ar-Ra'd: 28",
        keywords = listOf("perisai", "dzikir", "tentram", "tenang", "hati", "pagi", "petang", "syaitan", "ar-ra'd")
    ),
    IslamicInsight(
        id = "janji_ijabah",
        title = "Janji Allah: Setiap Doa Pasti Dijawab",
        category = "Janji Allah",
        content = "\"Dan Tuhanmu berfirman: Berdoalah kepada-Ku, niscaya akan Kuperkenankan bagimu.\" Jawaban doa Allah berikan dalam bentuk langsung dikabulkan, disimpan sebagai pahala akhirat, atau dihindarkan dari keburukan yang setimpal.",
        source = "QS. Ghafir: 60 & HR. Ahmad",
        keywords = listOf("janji", "ijabah", "kabul", "ghafir", "allah", "doa", "pahala", "akhirat")
    ),
    IslamicInsight(
        id = "janji_kemudahan",
        title = "Janji Allah: Kemudahan di Balik Setiap Kesulitan",
        category = "Janji Allah",
        content = "\"Maka sesungguhnya bersama kesulitan ada kemudahan, sesungguhnya bersama kesulitan ada kemudahan.\" Satu kesulitan tidak akan mampu mengalahkan dua kemudahan yang Allah bentangkan.",
        source = "QS. Al-Insyirah: 5-6",
        keywords = listOf("kesulitan", "kemudahan", "insyirah", "janji", "lapang", "beban")
    )
)

private val InsightCardShape = RoundedCornerShape(14.dp)
private val InsightTagShape = RoundedCornerShape(6.dp)
private val InsightBoxShape = RoundedCornerShape(10.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToPromises: () -> Unit,
    onNavigateToPrayerTimes: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val insights = StaticInsights

    val query = searchQuery.trim().lowercase()
    val filteredInsights = remember(query) {
        if (query.isBlank()) {
            insights
        } else {
            insights.filter { insight ->
                insight.title.lowercase().contains(query) ||
                insight.category.lowercase().contains(query) ||
                insight.content.lowercase().contains(query) ||
                insight.source.lowercase().contains(query) ||
                insight.keywords.any { it.contains(query) }
            }
        }
    }

    val showPromisesCard = query.isBlank() || "janji promises janji-janji allah pengingat hati al-qur'an".contains(query)
    val showPrayerTimesCard = query.isBlank() || "sholat jadwal waktu adzan kemenag hitung mundur".contains(query)
    val showQiblaCard = query.isBlank() || "kiblat qiblat kompas ka'bah ka'bah arah mekkah".contains(query)

    if (onBack != null) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
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
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.app_logo_kufi),
                                        contentDescription = "Logo Adzkaruna",
                                        modifier = Modifier.size(34.dp)
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
                                            text = "Doa & Dzikir Harian Muslimin v1.64.0",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
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
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            ExploreBody(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filteredInsights = filteredInsights,
                showPromisesCard = showPromisesCard,
                showPrayerTimesCard = showPrayerTimesCard,
                showQiblaCard = showQiblaCard,
                onNavigateToPromises = onNavigateToPromises,
                onNavigateToPrayerTimes = onNavigateToPrayerTimes,
                onNavigateToQibla = onNavigateToQibla,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            ExploreBody(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filteredInsights = filteredInsights,
                showPromisesCard = showPromisesCard,
                showPrayerTimesCard = showPrayerTimesCard,
                showQiblaCard = showQiblaCard,
                onNavigateToPromises = onNavigateToPromises,
                onNavigateToPrayerTimes = onNavigateToPrayerTimes,
                onNavigateToQibla = onNavigateToQibla
            )
        }
    }
}

@Composable
private fun ExploreBody(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredInsights: List<IslamicInsight>,
    showPromisesCard: Boolean,
    showPrayerTimesCard: Boolean,
    showQiblaCard: Boolean,
    onNavigateToPromises: () -> Unit,
    onNavigateToPrayerTimes: () -> Unit,
    onNavigateToQibla: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Kolom Pencarian Pinned (Tetap di atas saat di-scroll)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cari materi (mustajab, adab, janji, dzikir...)",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }

        if (filteredInsights.isEmpty() && !showPromisesCard && !showPrayerTimesCard && !showQiblaCard) {
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
                        text = "Materi Tidak Ditemukan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tidak ada materi Islam yang cocok dengan kata kunci \"$searchQuery\"",
                        fontSize = 12.5.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { onSearchQueryChange("") },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Tampilkan Semua Wawasan")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Shortcut: Janji-Janji Allah
                if (showPromisesCard) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onNavigateToPromises() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Janji-Janji Allah (Promises)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Pengingat dan penenang hati di saat gundah",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Interactive Shortcut: Jadwal Waktu Sholat
                if (showPrayerTimesCard) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onNavigateToPrayerTimes() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Jadwal Sholat Kemenag RI",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Hitung mundur & jadwal akurat 500+ kota",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Interactive Shortcut: Petunjuk Arah Qiblat
                if (showQiblaCard) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onNavigateToQibla() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Explore,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Petunjuk Arah Qiblat",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Sensor kompas magnetik akurat Ka'bah 295°",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Daftar Pembahasan & Wawasan Islam
                items(
                    items = filteredInsights,
                    key = { it.id },
                    contentType = { "IslamicInsight" }
                ) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = InsightCardShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = InsightTagShape
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Icon(
                                    imageVector = when (item.category) {
                                        "Mutiara Hadits" -> Icons.Default.Lightbulb
                                        "Waktu Utama" -> Icons.Default.AccessTime
                                        "Adab & Sunnah" -> Icons.Default.CheckCircleOutline
                                        "Keutamaan Dzikir" -> Icons.Default.Shield
                                        else -> Icons.Default.StarOutline
                                    },
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.5.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Sumber: ${item.source}",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
