package com.viwath.music_player.domain.model.dto

import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music

data class MusicDto(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val imagePath: String?, // use to get image
    val uri: String,
    val addDate: String,
    val isFavorite: Boolean = false
)

fun MusicDto.toMusic(): Music {
    return Music(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        duration = duration,
        imagePath = imagePath,
        uri = uri,
        addDate = addDate
    )
}

fun MusicDto.toFavoriteMusic(): FavoriteMusic {
    return FavoriteMusic(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        duration = duration,
        imagePath = imagePath,
        uri = uri,
        addDate = addDate
    )
}

fun Music.toMusicDto(): MusicDto{
    return MusicDto(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        duration = duration,
        imagePath = imagePath,
        uri = uri,
        addDate = addDate
    )
}