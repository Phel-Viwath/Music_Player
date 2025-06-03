package com.viwath.music_player.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

@Immutable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val image: ImageBitmap?, // use to get image
    val uri: String,
    val trackNumber: Int,
    val addDate: String
)
