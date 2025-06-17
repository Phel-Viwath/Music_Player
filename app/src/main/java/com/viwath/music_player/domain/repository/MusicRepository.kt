package com.viwath.music_player.domain.repository

import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getMusicFiles(): List<Music>

    suspend fun addFavorite(music: FavoriteMusic)
    suspend fun removeFavorite(music: FavoriteMusic)
    fun getFavoriteMusic(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByDate(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByTitle(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByDuration():Flow<List<FavoriteMusic>>

}