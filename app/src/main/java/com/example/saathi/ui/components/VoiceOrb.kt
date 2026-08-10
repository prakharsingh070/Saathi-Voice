package com.example.saathi.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.saathi.ui.theme.CalmTeal
import com.example.saathi.ui.theme.DeepIndigo
import com.example.saathi.ui.theme.Saffron

enum class VoiceState {
    IDLE, LISTENING, THINKING, SPEAKING, ERROR
}

@Composable
fun VoiceOrb(
    state: VoiceState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbTransition")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == VoiceState.LISTENING) 1.2f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val orbColor = when (state) {
        VoiceState.IDLE -> DeepIndigo
        VoiceState.LISTENING -> CalmTeal
        VoiceState.THINKING -> Saffron
        VoiceState.SPEAKING -> CalmTeal
        VoiceState.ERROR -> Color.Red
    }

    val stateDescription = when (state) {
        VoiceState.IDLE -> "Assistant is ready"
        VoiceState.LISTENING -> "Assistant is listening"
        VoiceState.THINKING -> "Assistant is processing"
        VoiceState.SPEAKING -> "Assistant is speaking"
        VoiceState.ERROR -> "Assistant error"
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .semantics { contentDescription = stateDescription },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val radius = size.minDimension / 2
            
            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orbColor.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = radius * scale * 1.5f
                ),
                radius = radius * scale * 1.5f
            )

            // Main Orb
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(orbColor, orbColor.copy(alpha = 0.7f)),
                ),
                radius = radius * scale
            )

            // Animated ring
            if (state == VoiceState.LISTENING || state == VoiceState.THINKING) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = radius * scale * 1.1f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
