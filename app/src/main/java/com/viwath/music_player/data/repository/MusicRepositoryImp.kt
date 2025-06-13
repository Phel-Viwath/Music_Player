package com.viwath.music_player.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.viwath.music_player.core.util.GetImage.getImagePath
import com.viwath.music_player.data.data_source.FavoriteMusicDao
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class MusicRepositoryImp(
    private val context: Context,
    private val dao: FavoriteMusicDao
) : MusicRepository{

    override suspend fun getMusicFiles(): List<Music> {
        val musicFiles = mutableListOf<Music>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
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
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()){
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val album = cursor.getString(albumCol)
                val data = cursor.getString(dataCol)
                val duration = cursor.getLong(durationCol)
                val trackNumber = cursor.getInt(trackCol)
                val dateCol = cursor.getString(dateCol)

                val imagePath = data.getImagePath(context)

                val music = Music(id, title, artist, album, duration, imagePath, data,trackNumber, dateCol)
                musicFiles.add(music)
            }
        }
        return musicFiles
    }


    override suspend fun addFavorite(music: FavoriteMusic) {
        dao.addFavorite(music)
    }

    override suspend fun removeFavorite(music: FavoriteMusic) {
        dao.removeFavorite(music)
    }

    override fun getFavoriteMusic(): Flow<FavoriteMusic> {
        return dao.getFavoriteMusic()
    }

    override fun getFavoriteMusicByDate(): Flow<FavoriteMusic> {
        return dao.getFavoriteMusicByDate()
    }

    override fun getFavoriteMusicByTitle(): Flow<FavoriteMusic> {
        return dao.getFavoriteMusicByTitle()
    }

    override fun getFavoriteMusicByDuration(): Flow<FavoriteMusic> {
        return dao.getFavoriteMusicByDuration()
    }
}