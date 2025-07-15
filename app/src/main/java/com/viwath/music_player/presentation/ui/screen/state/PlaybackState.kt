package com.viwath.music_player.presentation.ui.screen.state

import androidx.compose.runtime.Stable
import androidx.media3.common.Player
import com.viwath.music_player.domain.model.dto.MusicDto

@Stable
data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val currentMusic: MusicDto? = null,
    val playbackState: Int = Player.STATE_IDLE
)
