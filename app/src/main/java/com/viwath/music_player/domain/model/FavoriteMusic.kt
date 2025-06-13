package com.viwath.music_player.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_music")
data class FavoriteMusic(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val imagePath: String?,
    val uri: String,
    val trackNumber: Int,
    val addDate: String
)

fun Music.toFavoriteMusic(): FavoriteMusic {
    return FavoriteMusic(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        imagePath = imagePath,
        uri = uri,
        trackNumber = trackNumber,
        addDate = addDate
    )
}

fun FavoriteMusic.toMusic(): Music {
    return Music(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        imagePath = imagePath,
        uri = uri,
        trackNumber = trackNumber,
        addDate = addDate
    )
}