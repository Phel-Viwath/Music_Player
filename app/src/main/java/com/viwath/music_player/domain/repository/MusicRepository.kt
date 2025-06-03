package com.viwath.music_player.domain.repository

import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getMusicFiles(): List<Music>

    suspend fun addFavorite(music: FavoriteMusic)
    suspend fun removeFavorite(music: FavoriteMusic)
    fun getFavoriteMusic(): Flow<FavoriteMusic>
    fun getFavoriteMusicByDate(): Flow<FavoriteMusic>
    fun getFavoriteMusicByTitle(): Flow<FavoriteMusic>

}