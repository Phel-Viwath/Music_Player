package com.viwath.music_player.presentation.ui.screen

sealed class Screen(val route: String) {
    data object MusicListScreenRoute : Screen("music_list_screen")
    data object MusicDetailScreenRoute : Screen("music_detail_screen")
}