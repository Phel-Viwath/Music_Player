package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.MusicDto

data class FavorMusicState(
    val idle: Boolean = false,
    val error: String? = null,
    val favorMusicList : List<FavoriteMusic>?= emptyList(),
    val deleteMusic: MusicDto? = null,
    val music: MusicDto? = null,
    val favoriteId: List<String> = emptyList()
)