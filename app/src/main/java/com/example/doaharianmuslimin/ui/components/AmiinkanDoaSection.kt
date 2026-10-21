package com.example.doaharianmuslimin.ui.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.window.Dialog
import com.example.doaharianmuslimin.data.model.CommunityPrayer

@Composable
fun AmiinkanDoaSection(
    prayers: List<CommunityPrayer>,
    onArrowClick: () -> Unit,
    onToggleAamiin: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPrayerForDetail by remember { mutableStateOf<CommunityPrayer?>(null) }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(30)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with + Button (Aamiinkan Doa Saudaramu)
        // TITIK DI SAMPING TULISAN DAN TEKS "KETUK + UNTUK KIRIM DOA" SUDAH DIHAPUS
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aamiinkan Doa Saudaramu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Saling mendoakan sesama muslim dalam kebaikan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Tombol + untuk menulis doa
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onArrowClick() },
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tulis Doa Baru",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // High-performance LazyRow dengan kartu seragam dan bisa di-klik untuk melihat detail
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(prayers, key = { it.id }) { prayer ->
                CommunityPrayerCard(
                    prayer = prayer,
                    onClick = {
                        selectedPrayerForDetail = prayer
                    },
                    onAamiinClick = {
                        vibrate()
                        onToggleAamiin(prayer.id)
                    },
                    onLikeClick = {
                        vibrate()
                        onToggleLike(prayer.id)
                    },
                    modifier = Modifier
                        .width(285.dp)
                        .height(190.dp) // Ukuran kotak seragam
                )
            }
        }
    }

    // Modal Dialog Detail Doa & Berbagi ke WhatsApp
    selectedPrayerForDetail?.let { activePrayer ->
        // Ambil data terbaru dari list jika ada perubahan status Aamiin
        val currentPrayer = prayers.find { it.id == activePrayer.id } ?: activePrayer

        PrayerDetailDialog(
            prayer = currentPrayer,
            onDismiss = { selectedPrayerForDetail = null },
            onAamiinClick = {
                vibrate()
                onToggleAamiin(currentPrayer.id)
            },
            onShareWhatsApp = {
                sharePrayerToWhatsApp(context, currentPrayer)
            }
        )
    }
}

@Composable
fun CommunityPrayerCard(
    prayer: CommunityPrayer,
    onClick: () -> Unit,
    onAamiinClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aamiinButtonColor by animateColorAsState(
        targetValue = if (prayer.isAamiined) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        label = "aamiinColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Sender info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = prayer.senderName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${prayer.location} • ${prayer.timestamp}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Middle: Prayer text (Flexible space)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "\"${prayer.prayerText}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }

            // Bottom: Action row (Aamiin & Like)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Aamiin dengan ICON DUA TANGAN BERDOA (🤲)
                FilledTonalButton(
                    onClick = onAamiinClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = aamiinButtonColor,
                        contentColor = if (prayer.isAamiined) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "🤲",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (prayer.isAamiined) "Aamiin (${prayer.aamiinCount})" else "Aamiinkan (${prayer.aamiinCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Tombol Suka / Like
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (prayer.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (prayer.isLiked) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dialog Detail Doa & Bagikan ke WhatsApp
 */
@Composable
fun PrayerDetailDialog(
    prayer: CommunityPrayer,
    onDismiss: () -> Unit,
    onAamiinClick: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header Dialog: Nama Pengirim & Tombol Tutup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = prayer.senderName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${prayer.location} • ${prayer.timestamp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Isi Doa Lengkap
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "\"${prayer.prayerText}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Statistik Aamiin & Hadits
                Text(
                    text = "🤲 Telah diaminkan oleh ${prayer.aamiinCount} saudara muslim",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "“Doa seorang muslim untuk saudaranya secara sembunyi-sembunyi adalah mustajab.” (HR. Muslim)",
                    fontSize = 10.5.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tombol Aksi: Aamiinkan & Bagikan ke WhatsApp
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol Aamiinkan
                    Button(
                        onClick = onAamiinClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prayer.isAamiined) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (prayer.isAamiined) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(text = "🤲", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (prayer.isAamiined) "Sudah Diaminkan (${prayer.aamiinCount})" else "Aamiinkan Doa Ini (${prayer.aamiinCount})",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tombol Bagikan ke WhatsApp (Warna Resmi Hijau WhatsApp #25D366)
                    Button(
                        onClick = onShareWhatsApp,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan ke WhatsApp",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bagikan ke WhatsApp",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Utilitas untuk membagikan doa langsung ke WhatsApp
 */
fun sharePrayerToWhatsApp(context: Context, prayer: CommunityPrayer) {
    val message = """
        *Aamiinkan Doa Saudara Kita di Adzkaruna* 🤲
        
        "${prayer.prayerText}"
        
        — Dari: *${prayer.senderName}* (${prayer.location})
        
        "Doa seorang muslim untuk saudaranya secara sembunyi-sembunyi adalah mustajab. Di samping kepalanya ada malaikat yang mengaminkan doanya." (HR. Muslim)
        
        Yuk saling mendoakan kebaikan sesama muslim di aplikasi *Adzkaruna*.
    """.trimIndent()

    val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        `package` = "com.whatsapp"
        putExtra(Intent.EXTRA_TEXT, message)
    }

    try {
        context.startActivity(whatsappIntent)
    } catch (_: Exception) {
        // Fallback jika WhatsApp belum terinstall
        val chooserIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            },
            "Bagikan Doa Saudara"
        )
        context.startActivity(chooserIntent)
    }
}
