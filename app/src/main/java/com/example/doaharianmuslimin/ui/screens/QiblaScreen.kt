package com.example.doaharianmuslimin.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doaharianmuslimin.ui.theme.AccentGold
import com.example.doaharianmuslimin.ui.theme.PrimaryTosca
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Ka'bah direction from Indonesia (approx 295° NW)
    val kaabaAzimuth = 295f
    var deviceHeading by remember { mutableFloatStateOf(0f) }
    var sensorAvailable by remember { mutableStateOf(true) }
    var hasVibratedOnAligned by remember { mutableStateOf(false) }

    // Register sensor listener for ultra-smooth orientation
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        if (rotationSensor == null) {
            sensorAvailable = false
        }

        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3)
            private var lastHeading = 0f
            private var isInitialized = false

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                var azimuth = 0f

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    if (azimuth < 0) azimuth += 360f
                } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    azimuth = event.values[0]
                }

                if (!isInitialized) {
                    lastHeading = azimuth
                    isInitialized = true
                    deviceHeading = azimuth
                    return
                }

                // Shortest angular difference (-180° to +180°) to prevent 360° flip across North
                var diff = azimuth - lastHeading
                while (diff < -180f) diff += 360f
                while (diff > 180f) diff -= 360f

                // Low-pass filter for butter-smooth movement
                val alpha = 0.18f
                val smoothed = (lastHeading + alpha * diff + 360f) % 360f
                lastHeading = smoothed

                // Only trigger state update when change is visible (reduces recomposition workload)
                if (abs(diff) > 0.35f) {
                    deviceHeading = smoothed
                }
            }
        }

        if (rotationSensor != null) {
            sensorManager?.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Relative angle to Ka'bah from current phone heading
    val relativeAngle = (kaabaAzimuth - deviceHeading + 360f) % 360f
    val isAligned = relativeAngle <= 4f || relativeAngle >= 356f

    // Haptic feedback when accurately facing Kaaba
    LaunchedEffect(isAligned) {
        if (isAligned && !hasVibratedOnAligned) {
            hasVibratedOnAligned = true
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            } catch (_: Exception) {}
        } else if (!isAligned) {
            hasVibratedOnAligned = false
        }
    }

    // Smooth animation with tween interpolation
    val animatedRotation by animateFloatAsState(
        targetValue = -deviceHeading,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "compassRotation"
    )

    val dialRingColor by animateColorAsState(
        targetValue = if (isAligned) PrimaryTosca else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "dialRingColor"
    )

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
                            text = "Akurasi Tinggi • Sensor Magnetik Halus",
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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Derajat Kiblat",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${kaabaAzimuth.toInt()}° NW",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Arah Ponsel",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${deviceHeading.toInt()}°",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Jarak Ka'bah",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "±7.912 km",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Status Indicator Banner
            Surface(
                color = if (isAligned) PrimaryTosca.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    if (isAligned) PrimaryTosca else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = if (isAligned) PrimaryTosca else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAligned) "Tepat Menghadap Ka'bah (Kiblat)" else "Putar ponsel hingga jarum mengarah ke Ka'bah",
                        fontSize = 12.sp,
                        fontWeight = if (isAligned) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAligned) PrimaryTosca else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // GPU Hardware-Accelerated Smooth Compass Graphic (using graphicsLayer to prevent relayout jank)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // Outer Ring Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2

                    // Background outer circle
                    drawCircle(
                        color = dialRingColor,
                        radius = radius - 8.dp.toPx(),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Rotating Dial with GPU GraphicsLayer (zero composition & zero relayout cost)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = animatedRotation
                        }
                ) {
                    // Cardinal Points
                    Text(
                        text = "U",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 14.dp)
                    )
                    Text(
                        text = "S",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp)
                    )
                    Text(
                        text = "T",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    )
                    Text(
                        text = "B",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )

                    // Kaaba Direction Marker on dial (295°)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = kaabaAzimuth
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = "Ka'bah",
                                tint = AccentGold,
                                modifier = Modifier.size(32.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = PrimaryTosca,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Center Pin/Hub
                Surface(
                    color = if (isAligned) PrimaryTosca else MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(10.dp)
                        ) {}
                    }
                }
            }

            // Bottom Calibration & Accuracy Guide
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (!sensorAvailable) {
                            "Sensor orientasi/magnetometer tidak terdeteksi pada perangkat ini. Pastikan izin sensor aktif atau gunakan kompas fisik."
                        } else {
                            "Pegang ponsel secara datar sejajar permukaan tanah. Jika arah kurang tepat, gerakkan ponsel membentuk angka 8 (∞) untuk kalibrasi kompas."
                        },
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
