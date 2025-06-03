package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.SortOrder

data class MusicState(
    val isLoading: Boolean = false,
    val musicFiles: List<Music> = emptyList(),
    val error: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE
)
