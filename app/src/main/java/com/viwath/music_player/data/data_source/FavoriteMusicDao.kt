package com.viwath.music_player.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viwath.music_player.domain.model.FavoriteMusic
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(music: FavoriteMusic): Long

    @Delete
    suspend fun removeFavorite(music: FavoriteMusic): Int

    @Query("SELECT * FROM favorite_music")
    fun getFavoriteMusic(): Flow<List<FavoriteMusic>>

    @Query("SELECT * FROM favorite_music WHERE id = :id")
    fun getFavoriteMusicById(id: Long): FavoriteMusic?

    @Query("SELECT * FROM favorite_music ORDER BY addDate DESC")
    fun getFavoriteMusicByDate(): Flow<List<FavoriteMusic>>

    @Query("SELECT * FROM favorite_music ORDER BY title DESC")
    fun getFavoriteMusicByTitle(): Flow<List<FavoriteMusic>>

    @Query("SELECT * FROM favorite_music ORDER BY duration DESC")
    fun getFavoriteMusicByDuration(): Flow<List<FavoriteMusic>>


}