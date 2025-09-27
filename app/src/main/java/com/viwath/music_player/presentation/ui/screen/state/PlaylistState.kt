package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.dto.PlaylistDto

data class PlaylistState(
    val isLoading: Boolean = false,
    val playlists: List<PlaylistDto> = emptyList(),
    val playlist: Playlist? = null,
    val playlistSongs: List<MusicDto> = emptyList(),
    val sortOrder: SortOrder = SortOrder.TITLE
)