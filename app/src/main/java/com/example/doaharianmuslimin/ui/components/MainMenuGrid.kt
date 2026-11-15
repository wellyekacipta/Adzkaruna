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
    DOA_HARIAN("Doa", R.drawable.ic_menu_doa),                      // Icon 15: Dua Tangan Berdoa
    QURAN("Al-Qur'an", R.drawable.ic_menu_quran),                   // Icon 4: Mushaf Al-Qur'an
    DZIKIR("Dzikir", R.drawable.ic_menu_dzikir),                   // Icon 18: Matahari & Awan
    WAKTU_SHOLAT("Sholat", R.drawable.ic_menu_jadwal_salat),       // Icon 29: Pin Lokasi Bulan Bintang
    QIBLAT("Kiblat", R.drawable.ic_menu_kiblat),                   // Icon 30: Ka'bah
    TOPIK_DOA("Topik", R.drawable.ic_menu_topik_doa),              // Icon 3: Rub el Hizb
    FAVORIT("Favorit", R.drawable.ic_menu_favorit_love),            // Icon Love
    BELAJAR_ISLAM("Sirah", R.drawable.ic_menu_belajar_islam)
}

// 4 Kolom x 2 Baris = 8 Menu Utama
private val ChunkedMenuItems4Col = MainMenuItem.entries.chunked(4)
private val MenuBoxShape = RoundedCornerShape(18.dp)

@Composable
fun MainMenuGrid(
    onMenuItemClick: (MainMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ChunkedMenuItems4Col.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onMenuItemClick(item) }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = MenuBoxShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.title,
                            fontSize = 11.5.sp,
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
