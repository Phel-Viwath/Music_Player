package com.viwath.music_player.presentation.ui.screen.component

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AmbientGradientBackground(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF191834)) // Base dark background
    ){
        // Blue Spot
        ColoredBlurSpot(
            color = Color(0xFF338ACA),
            offsetX = 300f,
            offsetY = 300f,
            radius = 300f
        )

        // Cyan Spot
        ColoredBlurSpot(
            color = Color(0xFF26B7CD),
            offsetX = 400f,
            offsetY = 500f,
            radius = 250f
        )

        // Purple Spot
        ColoredBlurSpot(
            color = Color(0xFF2B2C68),
            offsetX = 300f,
            offsetY = 100f,
            radius = 300f
        )

        // Green Spot
        ColoredBlurSpot(
            color = Color(0xFF61BDAF),
            offsetX = 20f,
            offsetY = 900f,
            radius = 300f
        )
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ColoredBlurSpot(
    color: Color,
    offsetX: Float,
    offsetY: Float,
    radius: Float
){
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY
                shadowElevation = 0f
                renderEffect = RenderEffect
                    .createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
                    .asComposeRenderEffect()
            }
    ) {
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = radius
        )
    }
}

