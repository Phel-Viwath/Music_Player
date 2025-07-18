package com.viwath.music_player.domain.model

data class AudioLevels(
    val bass: Float = 0f,
    val mid: Float = 0f,
    val treble: Float = 0f
) {
    val overall: Float get() = (bass + mid + treble) / 3f
}