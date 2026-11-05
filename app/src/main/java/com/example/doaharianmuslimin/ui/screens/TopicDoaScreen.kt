package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.data.DoaRepository
import com.example.doaharianmuslimin.data.model.Doa
import com.example.doaharianmuslimin.data.model.DoaCategory
import com.example.doaharianmuslimin.ui.components.DoaCard

data class TopicItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val matchingCategory: DoaCategory? = null,
    val keyword: String? = null
)

// Static topics & shapes to prevent re-allocation on every recomposition
private val StaticTopics = listOf(
    TopicItem("Rezeki & Pekerjaan", "Kemudahan rizki halal & berkah", Icons.Default.MonetizationOn, keyword = "rezeki"),
    TopicItem("Kesehatan & Kesembuhan", "Menjenguk orang sakit dan kesehatan", Icons.Default.Healing, keyword = "sakit"),
    TopicItem("Ketenangan & Hilang Cemas", "Terhindar dari kesedihan & kegelisahan", Icons.Default.Spa, keyword = "hazan"),
    TopicItem("Ujian & Kesabaran", "Saat tertimpa musibah & keteguhan hati", Icons.Default.Shield, keyword = "musibah"),
    TopicItem("Keluarga & Anak Shalih", "Orang tua, pasangan & keturunan shalih", Icons.Default.FamilyRestroom, matchingCategory = DoaCategory.KELUARGA),
    TopicItem("Sholat & Wudhu", "Adzan, qunut, setelah sholat & dhuha", Icons.Default.Mosque, matchingCategory = DoaCategory.SHOLAT),
    TopicItem("Aktivitas Harian", "Tidur, makan, rumah & bepergian", Icons.Default.Today, matchingCategory = DoaCategory.AKTIVITAS)
)

private val TopicCardShape = RoundedCornerShape(14.dp)
private val TopicIconShape = RoundedCornerShape(12.dp)
private val SearchShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDoaScreen(
    onBack: () -> Unit,
    onSelectDoa: (Doa) -> Unit,
    favoriteIds: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onCopyDoa: (Doa) -> Unit,
    onShareDoa: (Doa) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTopic by remember { mutableStateOf<TopicItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val topics = StaticTopics

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
                        title = {
                            Column {
                                Text(
                                    text = selectedTopic?.title ?: "Topik Kategori Doa",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (selectedTopic == null) "Pilih topik kebutuhan doa Anda" else "Kumpulan Doa Pilihan",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (isSearchActive) {
                                    isSearchActive = false
                                    searchQuery = ""
                                } else if (selectedTopic != null) {
                                    selectedTopic = null
                                } else {
                                    onBack()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchQuery = ""
                            }) {
                                Icon(
                                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = "Cari Doa",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )

                    // Search Bar Input
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = if (selectedTopic == null) "Cari doa atau topik..." else "Cari doa dalam topik ini...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val query = searchQuery.trim().lowercase()

        if (selectedTopic == null) {
            // Screen 1: List of Topics (or global search results if query is active)
            val filteredTopics = remember(query) {
                if (query.isBlank()) topics else topics.filter {
                    it.title.lowercase().contains(query) || it.description.lowercase().contains(query)
                }
            }

            val matchingDoas = remember(query) {
                if (query.isBlank()) emptyList() else DoaRepository.doaList.filter {
                    it.title.lowercase().contains(query) ||
                    it.latin.lowercase().contains(query) ||
                    it.translation.lowercase().contains(query)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (query.isNotBlank() && matchingDoas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Ditemukan ${matchingDoas.size} Doa untuk \"$query\":",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(
                        items = matchingDoas,
                        key = { it.id },
                        contentType = { "DoaCard" }
                    ) { doa ->
                        DoaCard(
                            doa = doa,
                            isFavorite = favoriteIds.contains(doa.id),
                            onFavoriteToggle = { onToggleFavorite(doa.id) },
                            onCopy = { onCopyDoa(doa) },
                            onShare = { onShareDoa(doa) },
                            onClick = { onSelectDoa(doa) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Kategori Topik Terkait:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                items(
                    items = filteredTopics,
                    key = { it.title },
                    contentType = { "TopicItem" }
                ) { topic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(TopicCardShape)
                            .clickable {
                                selectedTopic = topic
                                searchQuery = ""
                                isSearchActive = false
                            },
                        shape = TopicCardShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = TopicIconShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = topic.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = topic.description,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (filteredTopics.isEmpty() && matchingDoas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(42.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Tidak ada topik atau doa yang cocok", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        } else {
            // Screen 2: Filtered prayers for the selected topic
            val topic = selectedTopic!!
            val topicDoas = remember(topic) {
                DoaRepository.doaList.filter { doa ->
                    when {
                        topic.matchingCategory != null -> doa.category == topic.matchingCategory
                        topic.keyword != null -> doa.title.contains(topic.keyword, ignoreCase = true) ||
                                doa.translation.contains(topic.keyword, ignoreCase = true) ||
                                doa.latin.contains(topic.keyword, ignoreCase = true)
                        else -> true
                    }
                }
            }

            val filteredDoas = remember(topicDoas, query) {
                if (query.isBlank()) topicDoas else topicDoas.filter {
                    it.title.lowercase().contains(query) ||
                    it.latin.lowercase().contains(query) ||
                    it.translation.lowercase().contains(query)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menampilkan ${filteredDoas.size} Doa untuk ${topic.title}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(
                            onClick = { selectedTopic = null },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Ganti Topik", fontSize = 11.5.sp)
                        }
                    }
                }

                if (filteredDoas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(42.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Tidak ada doa yang cocok dengan pencarian", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(
                        items = filteredDoas,
                        key = { it.id },
                        contentType = { "DoaCard" }
                    ) { doa ->
                        DoaCard(
                            doa = doa,
                            isFavorite = favoriteIds.contains(doa.id),
                            onFavoriteToggle = { onToggleFavorite(doa.id) },
                            onCopy = { onCopyDoa(doa) },
                            onShare = { onShareDoa(doa) },
                            onClick = { onSelectDoa(doa) }
                        )
                    }
                }
            }
        }
    }
}
