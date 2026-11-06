package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val logoFadeAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseInOutCubic),
        label = "logoFadeAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.82f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutBack),
        label = "logoScale"
    )

    val textFadeAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300, easing = EaseInOutCubic),
        label = "textFadeAlpha"
    )

    val bottomFadeAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 500, easing = EaseInOutCubic),
        label = "bottomFadeAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00796B), // Hijau Tosca Atas
                        Color(0xFF00897B), // Hijau Tosca Utama Aplikasi
                        Color(0xFF004D40)  // Deep Tosca Bawah
                    )
                )
            )
    ) {
        // Konten Tengah: Logo Adzkaruna Putih & Nama Adzkaruna
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Container Logo Putih
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoFadeAlpha)
            ) {
                // Outer Subtle Halo
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.size(136.dp)
                ) {}

                // Inner Container Logo
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    shadowElevation = 0.dp,
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo_kufi),
                            contentDescription = "Logo Adzkaruna",
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Nama Aplikasi (Clean Adzkaruna)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textFadeAlpha)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Adzkaruna",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFB300), shape = RoundedCornerShape(3.dp))
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Doa & Dzikir Harian Muslimin",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Konten Bawah: Copyright & Versi Terbaru
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 26.dp, start = 16.dp, end = 16.dp)
                .alpha(bottomFadeAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Copyright © 2026 Adzkaruna. All Rights Reserved.",
                fontSize = 11.5.sp,
                color = Color.White.copy(alpha = 0.92f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Versi 1.65.0",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
