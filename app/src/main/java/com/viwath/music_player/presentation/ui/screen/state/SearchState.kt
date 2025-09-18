package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.dto.MusicDto

data class SearchState(
    val searchText: String = "",
    val musicList: List<MusicDto> = emptyList()
)