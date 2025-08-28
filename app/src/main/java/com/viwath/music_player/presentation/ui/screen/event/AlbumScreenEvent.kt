package com.viwath.music_player.presentation.ui.screen.event

sealed class AlbumScreenEvent {
    data object GetAlbums: AlbumScreenEvent()
    data class GetAlbum(val albumId: Long): AlbumScreenEvent()
}