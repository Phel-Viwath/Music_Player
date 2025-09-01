package com.viwath.music_player.presentation.ui.screen.event

import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.dto.MusicDto

sealed class PlaylistEvent {
    data object OnCreatePlaylist: PlaylistEvent()
    data object OnDeletePlaylist: PlaylistEvent()
    data object LoadPlaylist: PlaylistEvent()

    data object LoadPlaylistSong: PlaylistEvent()

    data class NewPlaylist(val playlist: Playlist): PlaylistEvent()
    data class DeletePlaylist(val playlist: Playlist): PlaylistEvent()
    data class AddPlaylistSong(val musicList: List<MusicDto>): PlaylistEvent()
    data class SortPlaylistSong(val sortOrder: SortOrder): PlaylistEvent()
}