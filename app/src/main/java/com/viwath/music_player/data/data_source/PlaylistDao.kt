package com.viwath.music_player.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // Playlist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newPlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist")
    fun getAllPlayList(): Flow<List<Playlist>>

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    suspend fun getPlaylist(playlistId: Long): Playlist

    @Query("UPDATE playlist SET thumbnail = :thumbnail WHERE playlistId = :playlistId")
    suspend fun updateThumbnailUri(thumbnail: String, playlistId: Long): Int

    // playlist song
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMusicToPlaylist(playlistSong: PlaylistSong)

    @Query("SELECT * FROM playlist_song WHERE playlistId = :playlistId")
    fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSong>>

    @Query("DELETE FROM playlist_song WHERE playlistId = :playlistId AND musicId = :musicId")
    suspend fun removeFromPlaylist(playlistId: Long, musicId: String)

    @Query("SELECT * FROM playlist_song")
    fun getAllPlaylistSongs(): Flow<List<PlaylistSong>>



}