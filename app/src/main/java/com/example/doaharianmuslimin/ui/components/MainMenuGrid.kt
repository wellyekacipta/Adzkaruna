package com.example.doaharianmuslimin.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R

enum class MainMenuItem(
    val title: String,
    @param:DrawableRes val iconRes: Int
) {
    DOA_HARIAN("Doa", R.drawable.ic_menu_doa),
    QURAN("Al-Qur'an", R.drawable.ic_menu_quran),
    HADITS("Hadits", R.drawable.ic_menu_hadits),
    DZIKIR("Dzikir", R.drawable.ic_menu_dzikir),
    WAKTU_SHOLAT("Sholat", R.drawable.ic_menu_jadwal_salat),
    KALENDER("Kalender", R.drawable.ic_menu_kalender),
    QIBLAT("Kiblat", R.drawable.ic_menu_kiblat),
    TOPIK_DOA("Topik", R.drawable.ic_menu_topik_doa),
    FAVORIT("Favorit", R.drawable.ic_menu_favorit_love),
    BELAJAR_ISLAM("Sirah", R.drawable.ic_menu_belajar_islam)
}

// 5 Kolom x 2 Baris = 10 Menu Utama
private val ChunkedMenuItems5Col = MainMenuItem.entries.chunked(5)
private val MenuBoxShape = RoundedCornerShape(13.dp)

@Composable
fun MainMenuGrid(
    onMenuItemClick: (MainMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ChunkedMenuItems5Col.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onMenuItemClick(item) }
                            .padding(vertical = 2.dp, horizontal = 1.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = MenuBoxShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.title,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
