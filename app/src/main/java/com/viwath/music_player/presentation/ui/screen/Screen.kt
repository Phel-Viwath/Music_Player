package com.viwath.music_player.presentation.ui.screen

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title: String,
    val icon: ImageVector
)

object Routes {
    const val HOME = "home"
    const val PLAYLIST_MUSIC = "playlist_music"
    const val MUSIC_PICKER = "music_picker"
}