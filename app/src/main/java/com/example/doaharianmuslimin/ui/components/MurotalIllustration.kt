package com.example.doaharianmuslimin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp

/**
 * Serene Dark-Themed Illustration for Murotal Screen
 * Based on the cozy arched window aesthetic with rain, glowing crescent moon,
 * twinkling stars, and an open Holy Qur'an on a traditional wooden Rehal (X-stand)
 * without any cats, adhering strictly to the user's instruction.
 */
@Composable
fun MurotalIllustration(
    isPlaying: Boolean,
    isRainActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "murotalAnimation")

    // Subtle rain streak motion - only animated if isRainActive is true
    val rainOffset = if (isRainActive) {
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 60f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rainOffset"
        )
        offset
    } else {
        0f
    }

    // Gentle pulsing ambient glow on the Holy Qur'an
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 24.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dimensions for the arched window
            val windowWidth = width * 0.58f
            val windowHeight = height * 0.88f
            val windowLeft = (width - windowWidth) / 2f
            val windowTop = height * 0.04f
            val archRadius = windowWidth / 2f

            // Create Path for Arched Window
            val windowPath = Path().apply {
                moveTo(windowLeft, windowTop + archRadius)
                // Top semicircular arch
                arcTo(
                    rect = Rect(
                        left = windowLeft,
                        top = windowTop,
                        right = windowLeft + windowWidth,
                        bottom = windowTop + archRadius * 2f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                // Down to bottom
                lineTo(windowLeft + windowWidth, windowTop + windowHeight)
                lineTo(windowLeft, windowTop + windowHeight)
                close()
            }

            // Window Outer Border / Frame (warm dark wood/stone)
            drawPath(
                path = windowPath,
                color = Color(0xFF332921),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10f)
            )

            // Inner content clipped inside the arched window
            clipPath(windowPath) {
                // Night Sky Gradient inside window (deep atmospheric rain night tone)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F261E), // Deep night pine
                            Color(0xFF143025), // Mid night teal-green
                            Color(0xFF183B2F)  // Lower ambient green
                        ),
                        startY = windowTop,
                        endY = windowTop + windowHeight
                    )
                )

                // Twinkling Stars
                val stars = listOf(
                    Offset(windowLeft + windowWidth * 0.22f, windowTop + archRadius * 0.45f),
                    Offset(windowLeft + windowWidth * 0.35f, windowTop + archRadius * 0.25f),
                    Offset(windowLeft + windowWidth * 0.42f, windowTop + archRadius * 0.85f),
                    Offset(windowLeft + windowWidth * 0.78f, windowTop + archRadius * 0.95f),
                    Offset(windowLeft + windowWidth * 0.28f, windowTop + archRadius * 1.25f),
                    Offset(windowLeft + windowWidth * 0.82f, windowTop + archRadius * 1.45f)
                )
                for (star in stars) {
                    drawCircle(
                        color = Color(0xFFFAF6E9).copy(alpha = 0.85f),
                        radius = 2.2f,
                        center = star
                    )
                }

                // Glowing Crescent Moon
                val moonCenter = Offset(windowLeft + windowWidth * 0.65f, windowTop + archRadius * 0.6f)
                val moonRadius = windowWidth * 0.16f

                // Moon Glow Halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFDE68A).copy(alpha = 0.35f), Color.Transparent),
                        center = moonCenter,
                        radius = moonRadius * 2.2f
                    ),
                    radius = moonRadius * 2.2f,
                    center = moonCenter
                )

                // Crescent Moon Shape
                val moonPath = Path().apply {
                    addOval(Rect(center = moonCenter, radius = moonRadius))
                    // Subtract inner circle to form crescent
                    op(
                        this,
                        Path().apply {
                            addOval(
                                Rect(
                                    center = Offset(moonCenter.x - moonRadius * 0.48f, moonCenter.y - moonRadius * 0.22f),
                                    radius = moonRadius * 0.88f
                                )
                            )
                        },
                        PathOperation.Difference
                    )
                }
                drawPath(moonPath, color = Color(0xFFFFF7D6))

                // Rain Streaks (slanted, animated) - only drawn when Hujan is active
                if (isRainActive) {
                    val rainRows = 6
                    val rainCols = 5
                    val colSpacing = windowWidth / rainCols
                    val rowSpacing = windowHeight / rainRows

                    for (r in 0..rainRows) {
                        for (c in 0..rainCols) {
                            val baseStartX = windowLeft + c * colSpacing + (r % 2) * (colSpacing / 2f)
                            val baseStartY = windowTop + ((r * rowSpacing + rainOffset) % windowHeight)
                            drawLine(
                                color = Color(0xFFA7C7B7).copy(alpha = 0.35f),
                                start = Offset(baseStartX, baseStartY),
                                end = Offset(baseStartX - 6f, baseStartY + 14f),
                                strokeWidth = 2f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Window Pane Cross Dividers
                // Vertical divider line
                drawLine(
                    color = Color(0xFF27211A),
                    start = Offset(windowLeft + windowWidth / 2f, windowTop),
                    end = Offset(windowLeft + windowWidth / 2f, windowTop + windowHeight),
                    strokeWidth = 6f
                )
                // Horizontal divider line
                drawLine(
                    color = Color(0xFF27211A),
                    start = Offset(windowLeft, windowTop + archRadius + 10f),
                    end = Offset(windowLeft + windowWidth, windowTop + archRadius + 10f),
                    strokeWidth = 6f
                )
            }

            // Window Sill Base (dark carved wooden sill)
            val sillWidth = windowWidth * 1.15f
            val sillHeight = 16f
            val sillLeft = (width - sillWidth) / 2f
            val sillTop = windowTop + windowHeight - 8f

            drawRoundRect(
                color = Color(0xFF2C241E),
                topLeft = Offset(sillLeft, sillTop),
                size = Size(sillWidth, sillHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // ==================== FOREGROUND: HOLY QUR'AN ON REHAL ====================
            val rehalCenter = Offset(width / 2f, sillTop + 6f)

            // Warm Radiant Ambient Light Glow under the Quran
            val ambientGlowRadius = windowWidth * 0.45f * glowScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEAA649).copy(alpha = if (isPlaying) 0.40f else 0.22f),
                        Color(0xFFEAA649).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(rehalCenter.x, rehalCenter.y - 20f),
                    radius = ambientGlowRadius
                ),
                radius = ambientGlowRadius,
                center = Offset(rehalCenter.x, rehalCenter.y - 20f)
            )

            // Rehal Stand (X-Stand made of carved rich mahogany wood)
            val rehalWidth = windowWidth * 0.62f
            val rehalHeight = 44f
            val rLeft = rehalCenter.x - rehalWidth / 2f
            val rRight = rehalCenter.x + rehalWidth / 2f
            val rBottom = sillTop + 2f

            // Left-to-right leg
            val leg1 = Path().apply {
                moveTo(rLeft + 8f, rBottom - rehalHeight)
                lineTo(rRight - 8f, rBottom)
                lineTo(rRight - 22f, rBottom)
                lineTo(rLeft, rBottom - rehalHeight + 10f)
                close()
            }
            drawPath(leg1, color = Color(0xFF5A3C24))
            drawPath(leg1, color = Color(0xFF332012), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))

            // Right-to-left leg
            val leg2 = Path().apply {
                moveTo(rRight - 8f, rBottom - rehalHeight)
                lineTo(rLeft + 8f, rBottom)
                lineTo(rLeft + 22f, rBottom)
                lineTo(rRight, rBottom - rehalHeight + 10f)
                close()
            }
            drawPath(leg2, color = Color(0xFF6F4B2F))
            drawPath(leg2, color = Color(0xFF3B2516), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))

            // Gold floral inlay accents on the rehal
            drawLine(
                color = Color(0xFFEAA649),
                start = Offset(rehalCenter.x - 20f, rBottom - rehalHeight / 2f),
                end = Offset(rehalCenter.x + 20f, rBottom - rehalHeight / 2f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Open Holy Al-Qur'an (Mushaf)
            val bookWidth = windowWidth * 0.72f
            val bookHeight = 46f
            val bookCenterY = rBottom - rehalHeight - 2f

            // Dark green Quran cover border
            val coverLeft = Path().apply {
                moveTo(rehalCenter.x, bookCenterY + 10f)
                lineTo(rehalCenter.x - bookWidth / 2f - 4f, bookCenterY - 12f)
                lineTo(rehalCenter.x - bookWidth / 2f - 4f, bookCenterY + bookHeight - 16f)
                lineTo(rehalCenter.x, bookCenterY + bookHeight - 4f)
                close()
            }
            drawPath(coverLeft, color = Color(0xFF133F2E)) // Islamic dark emerald green

            val coverRight = Path().apply {
                moveTo(rehalCenter.x, bookCenterY + 10f)
                lineTo(rehalCenter.x + bookWidth / 2f + 4f, bookCenterY - 12f)
                lineTo(rehalCenter.x + bookWidth / 2f + 4f, bookCenterY + bookHeight - 16f)
                lineTo(rehalCenter.x, bookCenterY + bookHeight - 4f)
                close()
            }
            drawPath(coverRight, color = Color(0xFF184D38))

            // Left Open Page (illuminated parchment)
            val pageLeft = Path().apply {
                moveTo(rehalCenter.x, bookCenterY + 8f)
                lineTo(rehalCenter.x - bookWidth / 2f, bookCenterY - 14f)
                lineTo(rehalCenter.x - bookWidth / 2f, bookCenterY + bookHeight - 20f)
                lineTo(rehalCenter.x, bookCenterY + bookHeight - 8f)
                close()
            }
            drawPath(pageLeft, color = Color(0xFFFFF7E8))
            drawPath(pageLeft, color = Color(0xFFD8C2A0), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f))

            // Right Open Page (illuminated parchment)
            val pageRight = Path().apply {
                moveTo(rehalCenter.x, bookCenterY + 8f)
                lineTo(rehalCenter.x + bookWidth / 2f, bookCenterY - 14f)
                lineTo(rehalCenter.x + bookWidth / 2f, bookCenterY + bookHeight - 20f)
                lineTo(rehalCenter.x, bookCenterY + bookHeight - 8f)
                close()
            }
            drawPath(pageRight, color = Color(0xFFFFFBEF))
            drawPath(pageRight, color = Color(0xFFE2CCA8), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f))

            // Gold Ribbon Bookmark (Hilya / Pembatas Mushaf) hanging down
            val ribbon = Path().apply {
                moveTo(rehalCenter.x, bookCenterY + 8f)
                lineTo(rehalCenter.x - 3f, rBottom + 12f)
                lineTo(rehalCenter.x + 3f, rBottom + 12f)
                lineTo(rehalCenter.x, bookCenterY + 8f)
                close()
            }
            drawPath(ribbon, color = Color(0xFFEAA649))

            // Delicate Lines representing Holy Verses & Gold Rosette
            val lineIndent = 12f
            for (i in 0..3) {
                val yOff = bookCenterY + 2f + i * 7.5f
                // Left page verse lines
                drawLine(
                    color = Color(0xFF9E8468).copy(alpha = 0.7f),
                    start = Offset(rehalCenter.x - bookWidth / 2f + lineIndent, yOff - 6f),
                    end = Offset(rehalCenter.x - 8f, yOff - 2f),
                    strokeWidth = 1.8f,
                    cap = StrokeCap.Round
                )
                // Right page verse lines
                drawLine(
                    color = Color(0xFF9E8468).copy(alpha = 0.7f),
                    start = Offset(rehalCenter.x + 8f, yOff - 2f),
                    end = Offset(rehalCenter.x + bookWidth / 2f - lineIndent, yOff - 6f),
                    strokeWidth = 1.8f,
                    cap = StrokeCap.Round
                )
            }

            // Golden ornamental header bar at top of open pages
            drawRoundRect(
                color = Color(0xFFEAA649).copy(alpha = 0.85f),
                topLeft = Offset(rehalCenter.x - bookWidth / 2f + 10f, bookCenterY - 11f),
                size = Size(bookWidth / 2f - 18f, 3.5f),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
            drawRoundRect(
                color = Color(0xFFEAA649).copy(alpha = 0.85f),
                topLeft = Offset(rehalCenter.x + 8f, bookCenterY - 9f),
                size = Size(bookWidth / 2f - 18f, 3.5f),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
        }
    }
}
