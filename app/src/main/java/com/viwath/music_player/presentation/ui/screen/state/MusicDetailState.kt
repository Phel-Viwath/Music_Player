package com.viwath.music_player.presentation.ui.screen.state

import com.viwath.music_player.domain.model.Music

data class MusicDetailState(
    val isLoading: Boolean = false,
    val music: Music? = null,
    val error: String = ""
)
