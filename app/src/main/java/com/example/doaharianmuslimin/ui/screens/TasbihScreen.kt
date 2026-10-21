package com.example.doaharianmuslimin.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import com.example.doaharianmuslimin.ui.theme.PrimaryToscaLight
import com.example.doaharianmuslimin.ui.theme.UthmanicFontFamily

@Composable
fun TasbihScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var count by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(33) }
    var totalRounds by remember { mutableIntStateOf(0) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    val dzikirOptions = listOf(
        "سُبْحَانَ اللَّهِ" to "Subhanallah",
        "الْحَمْدُ لِلَّهِ" to "Alhamdulillah",
        "اللَّهُ أَكْبَرُ" to "Allahu Akbar",
        "لَا إِلَهَ إِلَّا اللَّهُ" to "Laa ilaaha illallaah",
        "أَسْتَغْفِرُ اللَّهَ" to "Astaghfirullah"
    )
    var selectedDzikirIndex by remember { mutableIntStateOf(0) }

    fun vibrate() {
        if (!vibrationEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    fun increment() {
        vibrate()
        count++
        if (target > 0 && count >= target) {
            totalRounds++
            count = 0
            if (vibrationEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 70, 70, 70), -1))
            }
        }
    }

    val progress = if (target > 0) (count.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dzikir Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dzikirOptions[selectedDzikirIndex].first,
                    fontSize = 32.sp,
                    fontFamily = UthmanicFontFamily,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dzikirOptions[selectedDzikirIndex].second,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dzikirOptions.forEachIndexed { index, pair ->
                        FilterChip(
                            selected = selectedDzikirIndex == index,
                            onClick = { selectedDzikirIndex = index },
                            label = { Text(pair.second.split(" ")[0], fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Target Selector & Vibration Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(33, 99, 100, 0).forEach { t ->
                    val isSelected = target == t
                    OutlinedButton(
                        onClick = {
                            target = t
                            count = 0
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (t == 0) "Bebas" else "$t", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(
                onClick = { vibrationEnabled = !vibrationEnabled }
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = "Getar",
                    tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }

        // Circular Counter Button in Tosca Green
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryToscaLight, PrimaryTosca)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) {
                    increment()
                },
            contentAlignment = Alignment.Center
        ) {
            // Circular progress indicator
            if (target > 0) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    color = AccentGold,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$count",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                if (target > 0) {
                    Text(
                        text = "Target: $target",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ketuk untuk Menghitung",
                    fontSize = 12.sp,
                    color = AccentGold
                )
            }
        }

        // Round summary and Reset button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Putaran Selesai: $totalRounds",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = {
                    count = 0
                    totalRounds = 0
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Hitungan")
            }
        }
    }
}
