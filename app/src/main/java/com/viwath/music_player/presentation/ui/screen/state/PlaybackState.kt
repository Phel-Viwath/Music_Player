package com.viwath.music_player.presentation.ui.screen.state

import androidx.media3.common.Player
import com.viwath.music_player.domain.model.Music

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val currentMusic: Music? = null,
    val playbackState: Int = Player.STATE_IDLE
)
