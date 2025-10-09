package com.viwath.music_player.data.repository

import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.viwath.music_player.core.util.GetImage.getImagePath
import com.viwath.music_player.data.data_source.FavoriteMusicDao
import com.viwath.music_player.data.data_source.PlaylistDao
import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MusicRepositoryImp(
    private val context: Context,
    private val favoriteDao: FavoriteMusicDao,
    private val playlistDao: PlaylistDao
) : MusicRepository{

    private var cacheSong: List<Music> = emptyList()
    private var cacheAlbum: List<Album> = emptyList()
    private var cacheAlbumSong: Set<Pair<Long, List<Music>>> = emptySet()

    // warning part
    // delete music from storage
    override suspend fun deleteMusic(music: Music): Int {
        return withContext(Dispatchers.IO){
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                music.id
            )
            context.contentResolver.delete(uri,null,null)
        }
    }

    override suspend fun getDeletePermissionIntent(music: Music): IntentSender? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    music.id
                )
                MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(uri)
                ).intentSender
            } else {
                null
            }
        }
    }

    override suspend fun getMusicFiles(): List<Music> {
        if (cacheSong.isEmpty()){
            cacheSong = queryMediaStore()
        }
        return cacheSong
    }

    override suspend fun getAlbums(): List<Album> {
        if (cacheAlbum.isEmpty()){
            cacheAlbum = getAlbumsList()
        }
        return cacheAlbum
    }

    override suspend fun getSongByAlbumId(
        albumId: Long
    ): List<Music>{
        if (!cacheAlbumSong.any { it.first == albumId }){
            val songs = getAlbumSong(albumId)
            cacheAlbumSong = cacheAlbumSong + (albumId to songs)
        }
        return cacheAlbumSong.find { it.first == albumId }?.second ?: emptyList()
    }

    override suspend fun clearCache() {
        cacheSong = emptyList()
        cacheAlbum = emptyList()
        cacheAlbumSong = emptySet()
    }
    // Favorite

    override suspend fun addFavorite(music: FavoriteMusic): Long {
        return favoriteDao.addFavorite(music)
    }

    override suspend fun removeFavorite(music: FavoriteMusic): Int {
        return favoriteDao.removeFavorite(music)
    }

    override suspend fun getFavoriteMusicById(id: Long): FavoriteMusic? {
        return favoriteDao.getFavoriteMusicById(id)
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

    private suspend fun queryMediaStore(): List<Music> = withContext(Dispatchers.IO){
        val musicList = mutableListOf<Music>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            musicProjection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.idCol())
                val title = cursor.getString(cursor.titleCol())
                val artist = cursor.getString(cursor.artistCol())
                val album = cursor.getString(cursor.albumCol())
                val albumId = cursor.getLong(cursor.albumIdCol())
                val data = cursor.getString(cursor.dataCol())
                val duration = cursor.getLong(cursor.durationCol())
                val dateCol = cursor.getLong(cursor.dateCol())

                val imagePath = data.getImagePath(context)
                val music = Music(
                    id,
                    title,
                    artist,
                    album,
                    albumId,
                    duration,
                    imagePath,
                    data,
                    dateCol
                )
                musicList.add(music)
            }
        }
        return@withContext musicList
    }

    private suspend fun getAlbumsList(): List<Album> = withContext(Dispatchers.IO){

        val albums = mutableListOf<Album>()
        val albumSongCountMap = mutableMapOf<Long, Int>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursorCount = context.contentResolver.query(
            collection,
            arrayOf(MediaStore.Audio.Media.ALBUM_ID),
            selection,
            null,
            null
        )
        cursorCount?.use { cursor ->
            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(cursor.albumIdCol())
                albumSongCountMap[albumId] = (albumSongCountMap[albumId] ?: 0) + 1
            }
        }


        val cursor = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )
        cursor?.use { cursor ->

            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            while (cursor.moveToNext()){
                val albumId = cursor.getLong(albumIdCol)
                val albumName = cursor.getString(albumCol)
                val artist = cursor.getString(cursor.artistCol())
                val albumArtUri = ContentUris.withAppendedId(
                    "content://media/external/audio/albumart".toUri(),
                    albumId
                ).toString()
                val songCount = albumSongCountMap[albumId]
                val album = Album(
                    albumId,
                    albumName,
                    albumArtUri,
                    artist,
                    songCount
                )
                albums.add(album)
            }
        }
        return@withContext albums.distinctBy { it.albumId }
    }

    private suspend fun getAlbumSong(albumId: Long): List<Music> = withContext(Dispatchers.IO){
        val musics = mutableListOf<Music>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = "${MediaStore.Audio.Media.ALBUM_ID}=?"
        val selectionArgs = arrayOf(albumId.toString())

        val cursor = context.contentResolver.query(
            uri, musicProjection, selection, selectionArgs,
            "${MediaStore.Audio.Media.TRACK} ASC"
        )

        cursor?.use {
            while (cursor.moveToNext()){

                val id = cursor.getLong(cursor.idCol())
                val title = cursor.getString(cursor.titleCol())
                val artist = cursor.getString(cursor.artistCol())
                val album = cursor.getString(cursor.albumCol())
                val albumId = cursor.getLong(cursor.albumIdCol())
                val data = cursor.getString(cursor.dataCol())
                val duration = cursor.getLong(cursor.durationCol())
                val dateCol = cursor.getLong(cursor.dateCol())
                val imagePath = data.getImagePath(context)
                val music = Music(
                    id,
                    title,
                    artist,
                    album,
                    albumId,
                    duration,
                    imagePath,
                    data,
                    dateCol
                )
                musics.add(music)
            }
        }

        return@withContext musics
    }

    private fun Cursor.idCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    private fun Cursor.titleCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
    private fun Cursor.artistCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
    private fun Cursor.albumCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
    private fun Cursor.albumIdCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
    private fun Cursor.dataCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
    private fun Cursor.durationCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
    private fun Cursor.dateCol(): Int = this.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

    companion object{
        private val musicProjection = arrayOf(
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
    }




}