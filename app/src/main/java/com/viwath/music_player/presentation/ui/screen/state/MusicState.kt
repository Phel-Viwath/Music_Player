package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.core.util.SortOrder

data class MusicState(
    val isLoading: Boolean = false,
    val musicFiles: List<MusicDto> = emptyList(),
    val error: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE
)
