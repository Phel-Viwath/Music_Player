package com.viwath.music_player.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_song",
    primaryKeys = ["playlistId", "musicId"],
    foreignKeys = [ForeignKey(
        entity = Playlist::class,
        parentColumns = ["playlistId"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PlaylistSong(
    val playlistId: Long,
    val musicId: String,
    val musicUri: String,
)
