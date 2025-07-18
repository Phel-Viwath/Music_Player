package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.dto.PlaylistDto

data class PlaylistState(
    val error: String = "",
    val success: String = "",
    val isLoading: Boolean = false,

    val playlists: List<PlaylistDto> = emptyList(),
    val playlist: Playlist? = null,
    val playlistSongs: List<MusicDto> = emptyList(),
)