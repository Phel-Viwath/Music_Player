package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.domain.model.dto.MusicDto

data class AlbumState(
    val albums: List<Album> = emptyList(),
    val musics: List<MusicDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)