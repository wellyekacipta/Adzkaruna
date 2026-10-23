package com.example.doaharianmuslimin.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.R
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.LpmqFontFamily
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Entrance animations
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logoAlpha"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 350),
        label = "contentAlpha"
    )

    // Continuous subtle rotating ornament ring
    val infiniteTransition = rememberInfiniteTransition(label = "ringSpin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinAngle"
    )

    // Progress bar animation
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val quotes = remember {
        listOf(
            "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ" to "Ingatlah, hanya dengan mengingat Allah hati menjadi tenteram. (QS. Ar-Ra'd: 28)",
            "فَاذْكُرُونِي أَذْكُرْكُمْ" to "Maka ingatlah kepada-Ku, niscaya Aku ingat kepadamu. (QS. Al-Baqarah: 152)",
            "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ" to "Berdoalah kepada-Ku, niscaya akan Aku perkenankan bagimu. (QS. Ghafir: 60)"
        )
    }
    val currentQuote = remember { quotes.random() }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF042421),
                        Color(0xFF083E3A),
                        Color(0xFF021715)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Rotating Islamic Ring & Logo Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                // Outer glowing halo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .rotate(rotationAngle)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    AccentGold.copy(alpha = 0.5f),
                                    PrimaryTosca.copy(alpha = 0.15f),
                                    AccentGold.copy(alpha = 0.7f),
                                    Color.Transparent,
                                    AccentGold.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Logo Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF08332F),
                    shadowElevation = 16.dp,
                    modifier = Modifier.size(108.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Logo Adzkaruna",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name & Calligraphy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha)
            ) {
                Text(
                    text = "أَذْكَارُنَا",
                    fontFamily = LpmqFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ADZKARUNA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = Color.White
                )

                Text(
                    text = "Doa & Dzikir Harian Muslimin",
                    fontSize = 13.sp,
                    color = Color(0xFF80CBC4),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Islamic Quote
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = currentQuote.first,
                    fontFamily = LpmqFontFamily,
                    fontSize = 17.sp,
                    color = AccentGold.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${currentQuote.second}\"",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(200.dp)
                    .alpha(contentAlpha)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AccentGold,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Memuat Berkah Hari Ini...",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }
    }
}
