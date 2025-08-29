package com.viwath.music_player.domain.repository

import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getMusicFiles(): List<Music>
    suspend fun getAlbums(): List<Album>

    suspend fun getSongByAlbumId(albumId: Long): List<Music>

    suspend fun clearCache()
    // favorite
    suspend fun addFavorite(music: FavoriteMusic)
    suspend fun removeFavorite(music: FavoriteMusic)
    fun getFavoriteMusic(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByDate(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByTitle(): Flow<List<FavoriteMusic>>
    fun getFavoriteMusicByDuration():Flow<List<FavoriteMusic>>

    // playlist
    suspend fun newPlayList(playlist: Playlist)
    suspend fun addMusicToPlaylist(playlistSongs: List<PlaylistSong>)
    suspend fun getPlaylist(playlistId: Long): Playlist

    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSong>>
    fun getAllPlaylistSongs(): Flow<List<PlaylistSong>>

    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun removePlaylistSong(playlistId: Long, musicId: String)

    suspend fun updatePlaylistThumbnail(playlistId: Long, thumbnailUri: String): Int


}