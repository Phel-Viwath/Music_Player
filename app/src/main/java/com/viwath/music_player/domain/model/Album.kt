package com.viwath.music_player.domain.model

data class Album(
    val albumId: Long,
    val albumName: String,
    val albumArtUri: String? = null,
    val totalSong: Int? = 0
)