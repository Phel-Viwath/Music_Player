package com.viwath.music_player.data.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.viwath.music_player.core.util.GetImage.getImagePath
import com.viwath.music_player.data.data_source.FavoriteMusicDao
import com.viwath.music_player.data.data_source.PlaylistDao
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class MusicRepositoryImp(
    private val context: Context,
    private val favoriteDao: FavoriteMusicDao,
    private val playlistDao: PlaylistDao
) : MusicRepository{

    override suspend fun getMusicFiles(): List<Music> {
        val result = mutableListOf<Music>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()){
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val album = cursor.getString(albumCol)
                val albumId = cursor.getLong(albumIdCol)
                val data = cursor.getString(dataCol)
                val duration = cursor.getLong(durationCol)
                val trackNumber = cursor.getInt(trackCol)
                val dateCol = cursor.getString(dateCol)

                val imagePath = data.getImagePath(context)

                val albumArtUri = ContentUris.withAppendedId(
                    "content://media/external/audio/albumart".toUri(),
                    albumId
                ).toString()

                val music = Music(id, title, artist, album, albumId, albumArtUri, duration, imagePath, data,trackNumber, dateCol)
                result.add(music)
            }
        }
        return result
    }


    // Favorite

    override suspend fun addFavorite(music: FavoriteMusic) {
        favoriteDao.addFavorite(music)
    }

    override suspend fun removeFavorite(music: FavoriteMusic) {
        favoriteDao.removeFavorite(music)
    }

    override fun getFavoriteMusic(): Flow<List<FavoriteMusic>> {
        return favoriteDao.getFavoriteMusic()
    }

    override fun getFavoriteMusicByDate(): Flow<List<FavoriteMusic>> {
        return favoriteDao.getFavoriteMusicByDate()
    }

    override fun getFavoriteMusicByTitle(): Flow<List<FavoriteMusic>> {
        return favoriteDao.getFavoriteMusicByTitle()
    }

    override fun getFavoriteMusicByDuration():Flow<List<FavoriteMusic>> {
        return favoriteDao.getFavoriteMusicByDuration()
    }

    /// Playlist
    override suspend fun newPlayList(playlist: Playlist) {
        playlistDao.newPlaylist(playlist)
    }

    override suspend fun addMusicToPlaylist(playlistSongs: List<PlaylistSong>) {
        playlistSongs.forEach {
            playlistDao.addMusicToPlaylist(it)
        }
    }
    override suspend fun getPlaylist(playlistId: Long): Playlist {
        return playlistDao.getPlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlayList()
    }

    override fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSong>> {
        return playlistDao.getPlaylistSongs(playlistId)
    }

    override fun getAllPlaylistSongs(): Flow<List<PlaylistSong>> {
        return playlistDao.getAllPlaylistSongs()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }

    override suspend fun removePlaylistSong(playlistId: Long, musicId: String) {
        playlistDao.removeFromPlaylist(playlistId, musicId)
    }

    override suspend fun updatePlaylistThumbnail(playlistId: Long, thumbnailUri: String): Int {
        return playlistDao.updateThumbnailUri(thumbnailUri, playlistId)
    }
}