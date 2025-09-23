package com.viwath.music_player.presentation.ui.screen.bottom_sheet

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomSheetMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val iconTint: Color = Color.White,
    val enable: Boolean = true,
    val action: () -> Unit
)
