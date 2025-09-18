package com.viwath.music_player.presentation.ui.screen

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title: String,
    val icon: ImageVector
)

sealed class Routes(val route: String) {
    data object HomeScreen: Routes("home")
    data object PlaylistMusicScreen: Routes("playlist_music")
    data object MusicPickerScreen: Routes("music_picker")
    data object AlbumDetailScreen: Routes("album_detail")
    data object SearchScreen: Routes("search_screen")
}