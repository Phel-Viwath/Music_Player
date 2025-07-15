package com.viwath.music_player.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long? = 0,
    val name: String,
    val createAt: Long = System.currentTimeMillis(),
    val thumbnail: String? = null,
)
