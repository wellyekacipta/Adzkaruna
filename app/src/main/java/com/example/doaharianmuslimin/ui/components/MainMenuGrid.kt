package com.example.doaharianmuslimin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.ui.theme.AccentGold

enum class MainMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    PROMISES("Promises of Allah", "Janji-janji Allah Al-Qur'an", Icons.Default.AutoAwesome),
    WAKTU_SHOLAT("Waktu Sholat", "Jadwal & Adzan Harian", Icons.Default.AccessTime),
    DOA_DOA("Doa-doa", "Kumpulan Doa Lengkap", Icons.AutoMirrored.Filled.MenuBook),
    TOPIK_DOA("Jelajah Doa Topik", "Rezeki, Sehat, Ujian dll.", Icons.Default.Category),
    QIBLAT("Qiblat", "Arah Kiblat 295° Ka'bah", Icons.Default.Explore),
    FAVORIT("Favorit", "Doa Pilihan Tersimpan", Icons.Default.Bookmark)
}

@Composable
fun MainMenuGrid(
    onMenuItemClick: (MainMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val items = MainMenuItem.entries
        val chunked = items.chunked(2)

        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onMenuItemClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (item == MainMenuItem.PROMISES || item == MainMenuItem.FAVORIT) AccentGold else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                maxLines = 1
                            )

                            Text(
                                text = item.subtitle,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
