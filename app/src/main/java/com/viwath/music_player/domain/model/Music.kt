package com.viwath.music_player.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val albumArtUri: String?,
    val duration: Long,
    val imagePath: String?, // use to get image
    val uri: String,
    val trackNumber: Int,
    val addDate: String
)
