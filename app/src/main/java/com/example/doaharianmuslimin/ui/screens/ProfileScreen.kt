package com.example.doaharianmuslimin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.data.AppThemeMode
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    currentThemeMode: AppThemeMode,
    onOpenThemeDialog: () -> Unit,
    totalFavorites: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showInfaqDialog by remember { mutableStateOf(false) }
    var showGoogleLoginDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("$label berhasil disalin!")
        }
    }

    fun shareApp() {
        val shareText = "Yuk install Adzkaruna - Aplikasi Doa & Dzikir Harian Muslimin lengkap dengan 114 Surat Al-Qur'an Mushaf Madinah, Jadwal Sholat Resmi Kemenag RI, Kompas Kiblat Halus, dan wadah saling mendoakan sesama muslim.\n\nUnduh sekarang dan raih pahala jariyah bersama!"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Aplikasi Adzkaruna"))
    }

    fun openPlayStore() {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            context.startActivity(webIntent)
        }
    }

    fun openWhatsAppFeedback() {
        try {
            val url = "https://wa.me/?text=Halo%20Developer%20Adzkaruna,%20saya%20ingin%20memberikan%20masukan%20untuk%20aplikasi%20Adzkaruna:"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Tidak dapat membuka WhatsApp")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sub-Header Bar (Directly beneath main header)
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Profil & Pengaturan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Akun, Preferensi & Dukungan Pengembang",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Content scrolling cleanly down to footer menu
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile & Guest Account Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Hamba Allah",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Akun Tamu (Data Tersimpan di HP)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Google Sign-In Button
                            OutlinedButton(
                                onClick = { showGoogleLoginDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = PrimaryTosca,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Masuk dengan Akun Google",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Statistics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$totalFavorites",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Doa Favorit",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .width(1.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(1.dp))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "114",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                    Text(
                                        text = "Surat Qur'an",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .width(1.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(1.dp))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Aktif",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryTosca
                                    )
                                    Text(
                                        text = "Doa & Dzikir",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // ==================== DUKUNGAN UNTUK DEVELOPER ====================
                item {
                    Text(
                        text = "Dukungan Pengembang (Infaq & Support)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                // Infaq & Sedekah Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showInfaqDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        border = BorderStroke(1.dp, PrimaryTosca.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryTosca,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.VolunteerActivism,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Infaq & Sedekah Pengembangan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Dukung operasional server API & amal jariyah",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Beri Rating di Play Store
                item {
                    SettingTile(
                        title = "Beri Rating Bintang 5 di Play Store",
                        subtitle = "Bantu Adzkaruna menjangkau lebih banyak muslim",
                        icon = Icons.Default.Star,
                        onClick = { openPlayStore() }
                    )
                }

                // Bagikan Aplikasi
                item {
                    SettingTile(
                        title = "Bagikan Aplikasi Adzkaruna",
                        subtitle = "Sebarkan kebaikan, raih pahala jariyah bersama",
                        icon = Icons.Default.Share,
                        onClick = { shareApp() }
                    )
                }

                // Kirim Saran & Masukan ke Developer
                item {
                    SettingTile(
                        title = "Kirim Masukan & Saran",
                        subtitle = "Hubungi pengembang melalui WhatsApp / Email",
                        icon = Icons.Default.Feedback,
                        onClick = { openWhatsAppFeedback() }
                    )
                }

                // ==================== PENGATURAN APLIKASI ====================
                item {
                    Text(
                        text = "Pengaturan Aplikasi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                // Mode Gelap & Terang Setting Tile
                item {
                    SettingTile(
                        title = "Mode Tampilan",
                        subtitle = currentThemeMode.title,
                        icon = when (currentThemeMode) {
                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                        },
                        onClick = onOpenThemeDialog
                    )
                }

                // Font Arabic Info
                item {
                    SettingTile(
                        title = "Font Teks Arab",
                        subtitle = "Uthmanic Naskh (Standar Asli Mushaf Madinah)",
                        icon = Icons.Default.FontDownload,
                        onClick = {}
                    )
                }

                // ==================== TENTANG APLIKASI ====================
                item {
                    Text(
                        text = "Tentang Aplikasi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Logo Adzkaruna",
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Adzkaruna",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Doa & Dzikir Harian Muslimin v1.28.0",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Aplikasi islami komprehensif tanpa iklan mengganggu: kumpulan doa harian shahih, 114 surat Al-Qur'an Mushaf Madinah, arah kiblat akurasi sensor, jadwal sholat resmi Kemenag RI, dan wadah saling mendoakan sesama muslim.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "© 2026 Adzkaruna • Dibuat dengan niat ibadah & amal jariyah.",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // ==================== DIALOG INFAQ & SEDEKAH ====================
    if (showInfaqDialog) {
        AlertDialog(
            onDismissRequest = { showInfaqDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = PrimaryTosca
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Infaq & Dukungan Developer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Jazakumullah Khairan atas niat baik Anda untuk mendukung keberlangsungan aplikasi Adzkaruna agar tetap gratis dan bebas iklan selamanya.",
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rekening Bank Syariah Indonesia (BSI)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { copyToClipboard("Rekening BSI", "7123456789") },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Bank Syariah Indonesia (BSI)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("7123456789 (a.n. Pengembang Adzkaruna)", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = PrimaryTosca, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // E-Wallet / QRIS
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { copyToClipboard("Nomor GoPay / Dana", "081234567890") },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GoPay / DANA / OVO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("0812-3456-7890", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = PrimaryTosca, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfaqDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ==================== DIALOG GOOGLE SIGN-IN INFO ====================
    if (showGoogleLoginDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleLoginDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = PrimaryTosca
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masuk dengan Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Fitur masuk dengan Google memungkinkan pencadangan otomatis doa favorit, riwayat baca Al-Qur'an, dan sinkronisasi antar perangkat.\n\nSaat ini Anda berada dalam Mode Tamu Offline (100% data tersimpan aman di HP ini).",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGoogleLoginDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Akun Tamu telah aktif secara otomatis!")
                        }
                    }
                ) {
                    Text("Lanjutkan Sebagai Tamu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleLoginDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SettingTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
