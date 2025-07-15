package com.viwath.music_player.domain.model.dto

import com.viwath.music_player.domain.model.Playlist

data class PlaylistDto(
    val playlistId: Long? = 0,
    val name: String,
    val createAt: Long = System.currentTimeMillis(),
    val thumbnail: String? = null,
    val totalSong: Int = 0
)

fun Playlist.toPlaylistDto(): PlaylistDto{
    return PlaylistDto(
        playlistId = playlistId,
        name = name,
        createAt = createAt,
        thumbnail = thumbnail
    )
}

fun PlaylistDto.toPlaylist(): Playlist{
    return Playlist(
        playlistId = playlistId,
        name = name,
        createAt = createAt,
        thumbnail = thumbnail
    )
}

