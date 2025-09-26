package com.viwath.music_player.presentation.ui.screen.event

sealed class NavigationEvent {
    data object HomeNav: NavigationEvent()
    data object PlaylistNav: NavigationEvent()
    data object SearchNav: NavigationEvent()
}