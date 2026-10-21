package com.example.doaharianmuslimin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Petunjuk Arah Qiblat",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Ka'bah, Masjidil Haram • Makkah",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
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
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Derajat Kiblat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "295° NW", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Jarak ke Ka'bah", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "± 7.912 km", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Compass Graphic
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                // Circular marks
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(
                        color = Color(0xFF00897B).copy(alpha = 0.2f),
                        radius = radius - 8.dp.toPx()
                    )
                }

                // Cardinal Points
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "U", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Red, modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp))
                    Text(text = "S", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp))
                    Text(text = "T", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
                    Text(text = "B", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp))
                }

                // Qibla Pointer (Pointing 295° NW from North)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(295f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "Arah Ka'bah",
                            tint = AccentGold,
                            modifier = Modifier.size(34.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = PrimaryTosca,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Center Hub
                Surface(
                    color = PrimaryTosca,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                }
            }

            // Bottom Guide Note
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Arah kiblat untuk wilayah Indonesia rata-rata berkisar 290° - 295° (arah Barat Laut). Pastikan ponsel diletakkan di permukaan datar dan jauh dari benda magnetik.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
