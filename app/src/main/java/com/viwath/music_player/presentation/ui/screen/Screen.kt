package com.viwath.music_player.presentation.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object MusicListScreenRoute : Screen("music_list_screen")
//    data object MusicDetailScreenRoute : Screen("music_detail_screen")
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String){
    data object Home : BottomNavItem(Screen.MusicListScreenRoute.route, Icons.Default.Home, "Home")
    data object Downloads : BottomNavItem("downloads", Icons.Default.Download, "Downloads")
}

data class TabItem(
    val title: String,
    val icon: ImageVector
)