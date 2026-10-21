package com.example.doaharianmuslimin.ui.screens

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

    val topics = listOf(
        TopicItem("Rezeki & Pekerjaan", "Memohon kemudahan rizki yang halal & berkah", Icons.Default.MonetizationOn, keyword = "rezeki"),
        TopicItem("Kesehatan & Kesembuhan", "Doa menjenguk orang sakit dan kesehatan", Icons.Default.Healing, keyword = "sakit"),
        TopicItem("Ketenangan & Hilang Cemas", "Terhindar dari kesedihan, kegelisahan & malas", Icons.Default.Spa, keyword = "hazan"),
        TopicItem("Ujian & Kesabaran", "Doa saat tertimpa musibah & mohon keteguhan hati", Icons.Default.Shield, keyword = "musibah"),
        TopicItem("Keluarga & Anak Shalih", "Doa orang tua, pasangan, dan keturunan shalih", Icons.Default.FamilyRestroom, matchingCategory = DoaCategory.KELUARGA),
        TopicItem("Sholat & Wudhu", "Doa adzan, qunut, setelah sholat fardhu & dhuha", Icons.Default.Mosque, matchingCategory = DoaCategory.SHOLAT),
        TopicItem("Aktivitas Harian", "Tidur, makan, rumah, pakaian, dan bepergian", Icons.Default.Today, matchingCategory = DoaCategory.AKTIVITAS)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedTopic?.title ?: "Jelajah Doa Berdasarkan Topik",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedTopic == null) "Pilih topik kebutuhan doa Anda" else "Daftar Doa Pilihan",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedTopic != null) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        if (selectedTopic == null) {
            // Grid / list of topics
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTopic = topic },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = topic.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = topic.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        } else {
            // Filtered prayers for the selected topic
            val topic = selectedTopic!!
            val filteredDoas = remember(topic) {
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Menampilkan ${filteredDoas.size} Doa untuk ${topic.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(filteredDoas, key = { it.id }) { doa ->
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
