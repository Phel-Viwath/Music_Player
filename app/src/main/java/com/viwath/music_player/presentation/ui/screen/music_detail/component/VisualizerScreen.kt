package com.viwath.music_player.presentation.ui.screen.music_detail.component

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.viwath.music_player.core.util.AudioLevels
import com.viwath.music_player.presentation.viewmodel.VisualizerViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class VisualizerParticle(
    var x: Float = 0f,
    var y: Float = 0f,
    var size: Float = 0f,
    var angle: Float = 0f,
    var speed: Float = 0f,
    var radius: Float = 0f,
    var alpha: Float = 0f,
    var active: Boolean = false
)

@Preview(showBackground = false)
@Composable
fun PreviewNCSVisualizer() {
//    // Generate some sample waveform data for preview
//    val sampleWaveform = ByteArray(128) { i ->
//        (sin(i * 0.1) * 127 * sin(i * 0.05)).toInt().toByte()
//    }

    val frequencies = FloatArray(128) { i ->
        (sin(i * 0.1) * 126 * sin(i * 0.05)).toFloat()
    }

    val sampleAudioLevels = AudioLevels(
        bass = 0.6f,
        mid = 0.8f,
        treble = 0.4f
    )

    Box(
        modifier = Modifier.size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        NCSStyleVisualizer3(
            modifier = Modifier.fillMaxSize(),
            frequencies = frequencies,
            audioLevels = sampleAudioLevels
        )
    }
}

@Composable
fun VisualizerScreen(
    modifier: Modifier = Modifier,
    viewModel: VisualizerViewModel
) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    // val waveformData by viewModel.waveformData.collectAsState()
    val audioLevels by viewModel.audioLevel.collectAsState()
    val frequencies by viewModel.frequencyBand.collectAsState()

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            viewModel.onPlaybackStarted()
        }
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.stopVisualizer()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            NCSStyleVisualizer3(
                modifier = Modifier.fillMaxSize(),
                frequencies = frequencies,
                audioLevels = audioLevels
            )
        } else {
            Text(
                text = "Play music to see visualizer",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun NCSStyleVisualizer3(
    modifier: Modifier = Modifier,
    frequencies: FloatArray,
    audioLevels: AudioLevels,
) {
    // Particle system
    val particles = remember { List(40) { VisualizerParticle() } }

    // Smooth bass animation
    val animatedBass by animateFloatAsState(
        targetValue = audioLevels.bass,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "BassPulse"
    )

    // Smooth mid animation for subtle effects
    val animatedMid by animateFloatAsState(
        targetValue = audioLevels.mid,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "MidPulse"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        // Background glow effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Subtle background glow that pulses with bass
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.03f + animatedBass * 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = 300.dp.toPx()
                ),
                radius = 300.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }

        // Main visualizer canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Base radius that pulses with bass
            val baseRadius = (90.dp.toPx()) + (animatedBass * 15.dp.toPx())
            val maxBarHeight = size.minDimension * 0.30f

            val barCount = frequencies.size
            val angleStep = PI.toFloat() / barCount

            // Normalize frequencies - find the max value
            val maxMagnitude = frequencies.maxOrNull() ?: 0.01f
            val normalizedFrequencies = if (maxMagnitude > 0.01f) {
                frequencies.map { (it / maxMagnitude).coerceIn(0f, 1f) }.toFloatArray()
            } else {
                frequencies
            }

            // Draw frequency bars (mirrored on both sides)
            normalizedFrequencies.forEachIndexed { index, magnitude ->
                val barHeight = magnitude * maxBarHeight

                // Start from bottom (270 degrees)
                val theta = (index * angleStep) + (PI.toFloat() / 2)

                // Calculate positions
                val rX = centerX + baseRadius * cos(theta)
                val rY = centerY + baseRadius * sin(theta)
                val rEndX = centerX + (baseRadius + barHeight) * cos(theta)
                val rEndY = centerY + (baseRadius + barHeight) * sin(theta)

                // Color gradient based on position (NCS style: white with slight color tint)
                val barColor = when {
                    index < barCount / 3 -> {
                        // Bass frequencies - slight cyan tint
                        Color.White.copy(alpha = 0.9f)
                    }
                    index < barCount * 2 / 3 -> {
                        // Mid frequencies - pure white
                        Color.White.copy(alpha = 0.95f)
                    }
                    else -> {
                        // High frequencies - slight blue tint
                        Color(0xFFEEF5FF)
                    }
                }

                // Right side bar
                drawLine(
                    color = barColor,
                    start = Offset(rX, rY),
                    end = Offset(rEndX, rEndY),
                    strokeWidth = 8f + (magnitude * 4f),
                    cap = StrokeCap.Round
                )

                // Add glow for prominent bars
                if (magnitude > 0.3f) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f * magnitude),
                        start = Offset(rX, rY),
                        end = Offset(rEndX, rEndY),
                        strokeWidth = 16f + (magnitude * 8f),
                        cap = StrokeCap.Round
                    )
                }

                // Left side bar (mirrored)
                val lX = centerX - baseRadius * cos(theta)
                val lEndX = centerX - (baseRadius + barHeight) * cos(theta)

                drawLine(
                    color = barColor,
                    start = Offset(lX, rY),
                    end = Offset(lEndX, rEndY),
                    strokeWidth = 8f + (magnitude * 4f),
                    cap = StrokeCap.Round
                )

                // Glow for left side
                if (magnitude > 0.3f) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f * magnitude),
                        start = Offset(lX, rY),
                        end = Offset(lEndX, rEndY),
                        strokeWidth = 16f + (magnitude * 8f),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Spawn particles on strong bass hits
            if (audioLevels.bass > 0.5f && Random.nextFloat() > 0.7f) {
                val activeParticle = particles.firstOrNull { !it.active }
                activeParticle?.let {
                    it.active = true
                    it.angle = Random.nextFloat() * 2 * PI.toFloat()
                    it.radius = baseRadius + (maxBarHeight * Random.nextFloat() * 0.6f)
                    it.speed = 3f + Random.nextFloat() * 6f
                    it.alpha = 0.8f
                    it.x = centerX + it.radius * cos(it.angle)
                    it.y = centerY + it.radius * sin(it.angle)
                }
            }

            // Update and draw particles
            particles.filter { it.active }.forEach { p ->
                p.radius += p.speed
                p.alpha -= 0.015f
                p.x = centerX + p.radius * cos(p.angle)
                p.y = centerY + p.radius * sin(p.angle)

                if (p.alpha <= 0f || p.radius > size.minDimension) {
                    p.active = false
                }

                drawCircle(
                    color = Color.White.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = 3f,
                    center = Offset(p.x, p.y)
                )

                // Particle glow
                drawCircle(
                    color = Color.White.copy(alpha = (p.alpha * 0.3f).coerceIn(0f, 1f)),
                    radius = 8f,
                    center = Offset(p.x, p.y)
                )
            }

            // Inner circle border (thin white ring around album art)
            drawCircle(
                color = Color.White.copy(alpha = 0.4f + animatedMid * 0.2f),
                radius = baseRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // Outer decorative ring
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = baseRadius + maxBarHeight * 0.5f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1f)
            )
        }
    }
}

/*
@Composable
fun NCSStyleVisualizer(
    modifier: Modifier = Modifier,
    waveform: ByteArray,
    audioLevels: AudioLevels
) {
    // Animated rotation for the outer ring - speed depends on bass level
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (20000 / (1 + audioLevels.bass * 2)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing animation for the center - intensity depends on mid frequencies
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f + (audioLevels.mid * 0.1f),
        targetValue = 1.1f + (audioLevels.mid * 0.3f),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (800 / (1 + audioLevels.mid * 2)).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier) {
        if (waveform.isEmpty()) return@Canvas

        drawNCSVisualization(
            waveform = waveform,
            rotation = rotation,
            pulseScale = pulseScale,
            audioLevels = audioLevels
        )
    }
}

private fun DrawScope.drawNCSVisualization(
    waveform: ByteArray,
    rotation: Float,
    pulseScale: Float,
    audioLevels: AudioLevels
) {
    val center = size.center
    val baseRadius = size.minDimension * 0.15f
    val maxRadius = size.minDimension * 0.4f

    // Calculate overall energy level (weighted average favoring bass)
    val energyLevel = (audioLevels.bass * 0.5f + audioLevels.mid * 0.3f + audioLevels.treble * 0.2f)

    // Draw outer rotating ring with particles - intensity depends on treble
    rotate(rotation, center) {
        drawOuterRing(center, maxRadius * 1.2f, waveform, audioLevels.treble)
    }

    // Draw main circular spectrum - responds to all frequencies
    drawCircularSpectrum(center, baseRadius, maxRadius, waveform, energyLevel)

    // Draw center pulsing orb - mainly responds to mid frequencies
    drawCenterOrb(center, baseRadius * 0.3f * pulseScale, waveform, audioLevels.mid)

    // Draw inner rotating elements - speed depends on bass
    rotate(-rotation * (0.5f + audioLevels.bass * 0.5f), center) {
        drawInnerElements(center, baseRadius * 0.8f, waveform, audioLevels.bass)
    }

    // Draw energy lines - appear only with strong bass
    if (audioLevels.bass > 0.4f) {
        drawEnergyLines(center, maxRadius, waveform, audioLevels.bass)
    }
}

private fun DrawScope.drawOuterRing(
    center: Offset,
    radius: Float,
    waveform: ByteArray,
    trebleLevel: Float
) {
    val particleCount = 60
    val step = waveform.size / particleCount

    for (i in 0 until particleCount) {
        val dataIndex = (i * step).coerceIn(0, waveform.size - 1)
        val amplitude = abs(waveform[dataIndex].toInt()).toFloat() / 128f * trebleLevel

        val angle = (i.toFloat() / particleCount) * 2f * PI
        val particleRadius = radius + (amplitude * 30f)

        val x = center.x + particleRadius * cos(angle).toFloat()
        val y = center.y + particleRadius * sin(angle).toFloat()

        // Neon particle effect - color varies with amplitude
        val color = Color.hsv(
            hue = 280f + (amplitude * 80f),
            saturation = 1f,
            value = 0.5f + (amplitude * 0.5f),
            alpha = 0.4f + (amplitude * 0.6f)
        )

        drawCircle(
            color = color,
            radius = 1f + (amplitude * 5f),
            center = Offset(x, y)
        )

        // Glow effect
        if (amplitude > 0.3f) {
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = 4f + (amplitude * 10f),
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawCircularSpectrum(
    center: Offset,
    baseRadius: Float,
    maxRadius: Float,
    waveform: ByteArray,
    energyLevel: Float
) {
    val barCount = 64
    val step = waveform.size / barCount
    val angleStep = 2f * PI / barCount

    for (i in 0 until barCount) {
        val dataIndex = (i * step).coerceIn(0, waveform.size - 1)
        val amplitude = (abs(waveform[dataIndex].toInt()).toFloat() / 128f) * energyLevel

        val angle = i * angleStep
        val barHeight = amplitude * (maxRadius - baseRadius)

        val startX = center.x + baseRadius * cos(angle).toFloat()
        val startY = center.y + baseRadius * sin(angle).toFloat()
        val endX = center.x + (baseRadius + barHeight) * cos(angle).toFloat()
        val endY = center.y + (baseRadius + barHeight) * sin(angle).toFloat()

        // Create gradient for each bar
        val barGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF00ffff).copy(alpha = 0.7f + energyLevel * 0.3f),
                Color(0xFFff00ff).copy(alpha = 0.7f + energyLevel * 0.3f),
                Color(0xFFffffff).copy(alpha = 0.7f + energyLevel * 0.3f)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY)
        )

        drawLine(
            brush = barGradient,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2f + (amplitude * 4f),
            cap = StrokeCap.Round
        )

        // Add glow effect only when amplitude is significant
        if (amplitude > 0.3f) {
            drawLine(
                color = Color.White.copy(alpha = 0.2f + amplitude * 0.1f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4f + (amplitude * 8f),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawCenterOrb(
    center: Offset,
    radius: Float,
    waveform: ByteArray,
    midLevel: Float
) {
    // Calculate average amplitude for center orb
    val avgAmplitude = waveform.map { abs(it.toInt()) }.average().toFloat() / 128f * midLevel

    // Outer glow - intensity depends on mid frequencies
    val glowGradient = Brush.radialGradient(
        colors = listOf(
            Color(0x88ff00ff).copy(alpha = 0.5f + midLevel * 0.5f),
            Color(0x4400ffff).copy(alpha = 0.3f + midLevel * 0.3f),
            Color.Transparent
        ),
        center = center,
        radius = radius * 3f
    )

    drawCircle(
        brush = glowGradient,
        radius = radius * 3f,
        center = center
    )

    // Main orb
    val orbGradient = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.8f + midLevel * 0.2f),
            Color(0xFFff00ff).copy(alpha = 0.7f + midLevel * 0.3f),
            Color(0xFF8000ff).copy(alpha = 0.6f + midLevel * 0.4f)
        ),
        center = center,
        radius = radius
    )

    drawCircle(
        brush = orbGradient,
        radius = radius * (0.6f + avgAmplitude * 0.4f),
        center = center
    )

    // Inner core
    drawCircle(
        color = Color.White.copy(alpha = 0.6f + avgAmplitude * 0.4f),
        radius = radius * 0.3f * (0.5f + avgAmplitude * 0.5f),
        center = center
    )
}

private fun DrawScope.drawInnerElements(
    center: Offset,
    radius: Float,
    waveform: ByteArray,
    bassLevel: Float
) {
    val elementCount = 12
    val step = waveform.size / elementCount

    for (i in 0 until elementCount) {
        val dataIndex = (i * step).coerceIn(0, waveform.size - 1)
        val amplitude = abs(waveform[dataIndex].toInt()).toFloat() / 128f * bassLevel

        val angle = (i.toFloat() / elementCount) * 2f * PI
        val elementRadius = radius * (0.7f + amplitude * 0.6f)

        val x = center.x + elementRadius * cos(angle).toFloat()
        val y = center.y + elementRadius * sin(angle).toFloat()

        val color = Color.hsv(
            hue = 200f + (amplitude * 160f),
            saturation = 1f,
            value = 0.6f + (amplitude * 0.4f),
            alpha = 0.5f + (amplitude * 0.5f)
        )

        drawCircle(
            color = color,
            radius = 1f + (amplitude * 4f),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawEnergyLines(
    center: Offset,
    maxRadius: Float,
    waveform: ByteArray,
    bassLevel: Float
) {
    val lineCount = 8
    val step = waveform.size / lineCount

    for (i in 0 until lineCount) {
        val dataIndex = (i * step).coerceIn(0, waveform.size - 1)
        val amplitude = abs(waveform[dataIndex].toInt()).toFloat() / 128f * bassLevel

        if (amplitude > 0.4f) {
            val angle = (i.toFloat() / lineCount) * 2f * PI
            val lineLength = maxRadius * (0.3f + amplitude * 0.7f)

            val startX = center.x + (maxRadius * 0.5f) * cos(angle).toFloat()
            val startY = center.y + (maxRadius * 0.5f) * sin(angle).toFloat()
            val endX = center.x + lineLength * cos(angle).toFloat()
            val endY = center.y + lineLength * sin(angle).toFloat()

            drawLine(
                color = Color(0xFFffffff).copy(alpha = 0.3f + amplitude * 0.7f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1f + (amplitude * 3f),
                cap = StrokeCap.Round
            )
        }
    }
} */
