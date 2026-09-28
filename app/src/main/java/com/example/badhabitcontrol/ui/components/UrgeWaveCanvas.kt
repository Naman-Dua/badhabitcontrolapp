package com.example.badhabitcontrol.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun UrgeWaveCanvas(
    modifier: Modifier = Modifier,
    waveColor: Color = Color(0xFF5A88C5)
) {
    val transition = rememberInfiniteTransition(label = "urge_wave")

    // Slow horizontal phase shift (5 seconds per cycle)
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    // Gentle breathing amplitude modulation (4 seconds per breath)
    val amplitudeFactor by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amplitude"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val width = size.width
        val height = size.height
        val midY = height / 2f
        val baseAmplitude = height * 0.22f * amplitudeFactor
        val wavelength = width * 0.75f

        // First gentle primary wave
        val primaryPath = Path()
        var x = 0f
        while (x <= width) {
            val angle = (2 * PI * (x / wavelength) + phase).toFloat()
            val y = midY + baseAmplitude * sin(angle)
            if (x == 0f) {
                primaryPath.moveTo(x, y)
            } else {
                primaryPath.lineTo(x, y)
            }
            x += 4f
        }

        drawPath(
            path = primaryPath,
            color = waveColor.copy(alpha = 0.85f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Second subtle harmonic wave (shifted phase and slightly smaller amplitude)
        val harmonicPath = Path()
        val harmonicPhase = phase * 0.75f + (PI / 3).toFloat()
        val harmonicAmp = baseAmplitude * 0.65f
        val harmonicWavelength = width * 0.9f

        x = 0f
        while (x <= width) {
            val angle = (2 * PI * (x / harmonicWavelength) + harmonicPhase).toFloat()
            val y = midY + harmonicAmp * sin(angle)
            if (x == 0f) {
                harmonicPath.moveTo(x, y)
            } else {
                harmonicPath.lineTo(x, y)
            }
            x += 4f
        }

        drawPath(
            path = harmonicPath,
            color = waveColor.copy(alpha = 0.35f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
