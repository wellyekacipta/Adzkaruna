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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.ui.theme.AccentGold

enum class MainMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    DOA_HARIAN("Doa Harian", "552 Kumpulan Doa", Icons.AutoMirrored.Filled.MenuBook),
    DZIKIR("Dzikir", "Dzikir Pagi & Petang", Icons.Default.SelfImprovement),
    TOPIK_DOA("Topik Doa", "Kategori Doa Harian", Icons.Default.Category),
    FAVORIT("Favorit Doa & Al-Qur'an", "Doa & Al-Qur'an Tersimpan", Icons.Default.Bookmark),
    QIBLAT("Kiblat Sholat", "Arah Kiblat 295° Ka'bah", Icons.Default.Explore),
    BELAJAR_ISLAM("Belajar Islam", "Perluas Wawasan Keislaman", Icons.Default.AutoAwesome)
}

// Static values to avoid re-allocations on every frame
private val MenuCardShape = RoundedCornerShape(16.dp)
private val MenuIconShape = RoundedCornerShape(12.dp)
private val ChunkedMenuItems2Col = MainMenuItem.entries.chunked(2)
private val ChunkedMenuItems3Col = MainMenuItem.entries.chunked(3)

@Composable
fun MainMenuGrid(
    onMenuItemClick: (MainMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Responsif: 3 kolom untuk tablet / layar lebar (>= 600dp), 2 kolom untuk ponsel (< 600dp)
    val columns = if (screenWidth >= 600.dp) 3 else 2
    val chunkedMenuItems = if (columns == 3) ChunkedMenuItems3Col else ChunkedMenuItems2Col

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chunkedMenuItems.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 104.dp)
                            .clip(MenuCardShape)
                            .clickable { onMenuItemClick(item) },
                        shape = MenuCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MenuIconShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (item == MainMenuItem.FAVORIT || item == MainMenuItem.BELAJAR_ISLAM) AccentGold else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                fontSize = if (item.title.length > 15) 11.5.sp else 12.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = item.subtitle,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                val remaining = columns - rowItems.size
                if (remaining > 0) {
                    repeat(remaining) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
