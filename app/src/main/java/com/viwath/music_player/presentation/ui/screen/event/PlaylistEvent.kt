package com.viwath.music_player.presentation.ui.screen.event

import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.dto.MusicDto

sealed class PlaylistEvent {
    data object OnCreatePlaylist: PlaylistEvent()
    data object OnDeletePlaylist: PlaylistEvent()

    data class LoadPlaylistSong(val playlistId: Long): PlaylistEvent()

    data class NewPlaylist(val playlist: Playlist): PlaylistEvent()
    data class DeletePlaylist(val playlist: Playlist): PlaylistEvent()
    data class AddPlaylistSong(val musicList: List<MusicDto>, val playlist: Playlist): PlaylistEvent()
}