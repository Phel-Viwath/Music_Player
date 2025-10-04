package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp

//@Composable
//fun AmbientGradientBackground(modifier: Modifier = Modifier){
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color(0xFF191834)) // Base dark background
//    ){
//        // Blue Spot
//        ColoredBlurSpot(
//            color = Color(0xFF338ACA),
//            offsetX = 300f,
//            offsetY = 300f,
//            radius = 300f
//        )
//
//        // Cyan Spot
//        ColoredBlurSpot(
//            color = Color(0xFF26B7CD),
//            offsetX = 400f,
//            offsetY = 500f,
//            radius = 250f
//        )
//
//        // Purple Spot
//        ColoredBlurSpot(
//            color = Color(0xFF2B2C68),
//            offsetX = 300f,
//            offsetY = 100f,
//            radius = 300f
//        )
//
//        // Green Spot
//        ColoredBlurSpot(
//            color = Color(0xFF61BDAF),
//            offsetX = 20f,
//            offsetY = 900f,
//            radius = 300f
//        )
//    }
//}
//
//
//@RequiresApi(Build.VERSION_CODES.S)
//@Composable
//fun ColoredBlurSpot(
//    color: Color,
//    offsetX: Float,
//    offsetY: Float,
//    radius: Float
//){
//    Canvas(
//        modifier = Modifier
//            .fillMaxSize()
//            .graphicsLayer {
//                translationX = offsetX
//                translationY = offsetY
//                shadowElevation = 0f
//                renderEffect = RenderEffect
//                    .createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
//                    .asComposeRenderEffect()
//            }
//    ) {
//        drawCircle(
//            color = color.copy(alpha = 0.8f),
//            radius = radius
//        )
//    }
//}


////


@Composable
fun AmbientGradientBackground(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF1A0B3D)
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFF1A0B3D), // Dark purple top
//                        Color(0xFF2D1B69), // Medium purple
//                        Color(0xFF4A148C), // Bright purple
//                        Color(0xFF6A1B9A), // Light purple
//                        Color(0xFF1A0B3D)  // Dark purple bottom
//                    )
//                )
            )
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Create text measurer for musical notes
        val textMeasurer = TextMeasurer(
            defaultFontFamilyResolver = fontFamilyResolver,
            defaultDensity = density,
            defaultLayoutDirection = LayoutDirection.Ltr
        )

        // Draw mixed musical notes at different positions, sizes, and rotations
        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.2f,
            centerY = canvasHeight * 0.15f,
            noteSymbol = "♪",
            size = 200.sp,
            rotation = -15f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.8f,
            centerY = canvasHeight * 0.1f,
            noteSymbol = "♫",
            size = 150.sp,
            rotation = 25f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.1f,
            centerY = canvasHeight * 0.4f,
            noteSymbol = "♬",
            size = 120.sp,
            rotation = 45f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.7f,
            centerY = canvasHeight * 0.35f,
            noteSymbol = "♭",
            size = 180.sp,
            rotation = -30f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.9f,
            centerY = canvasHeight * 0.6f,
            noteSymbol = "♯",
            size = 100.sp,
            rotation = 60f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.3f,
            centerY = canvasHeight * 0.7f,
            noteSymbol = "♩",
            size = 160.sp,
            rotation = -45f,
            alpha = 0.09f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.6f,
            centerY = canvasHeight * 0.8f,
            noteSymbol = "♪",
            size = 140.sp,
            rotation = 15f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.05f,
            centerY = canvasHeight * 0.8f,
            noteSymbol = "𝄞",
            size = 110.sp,
            rotation = -60f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.85f,
            centerY = canvasHeight * 0.9f,
            noteSymbol = "♫",
            size = 90.sp,
            rotation = 30f,
            alpha = 0.1f
        )

        // Add some smaller notes for depth
        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.4f,
            centerY = canvasHeight * 0.2f,
            noteSymbol = "♬",
            size = 80.sp,
            rotation = 75f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.4f,
            centerY = canvasHeight * 0.5f,
            noteSymbol = "♮",
            size = 200.sp,
            rotation = -75f,
            alpha = 0.1f
        )

        // Additional mixed notes for more variety
        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.15f,
            centerY = canvasHeight * 0.65f,
            noteSymbol = "♪",
            size = 95.sp,
            rotation = 20f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.75f,
            centerY = canvasHeight * 0.25f,
            noteSymbol = "♩",
            size = 130.sp,
            rotation = -50f,
            alpha = 0.1f
        )

        drawMusicalNote(
            textMeasurer = textMeasurer,
            centerX = canvasWidth * 0.95f,
            centerY = canvasHeight * 0.4f,
            noteSymbol = "♭",
            size = 75.sp,
            rotation = 85f,
            alpha = 0.1f
        )
    }
}

private fun DrawScope.drawMusicalNote(
    textMeasurer: TextMeasurer,
    centerX: Float,
    centerY: Float,
    noteSymbol: String,
    size: androidx.compose.ui.unit.TextUnit,
    rotation: Float,
    alpha: Float
) {
    rotate(rotation, pivot = Offset(centerX, centerY)) {
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(noteSymbol),
            style = TextStyle(
                fontSize = size,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = alpha)
            )
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                centerX - textLayoutResult.size.width / 2f,
                centerY - textLayoutResult.size.height / 2f
            )
        )
    }
}
