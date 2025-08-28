package com.viwath.music_player.domain.use_case.album_use_case

import android.util.Log
import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.domain.repository.MusicRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumsUseCase @Inject constructor(
    private val repository: MusicRepository
){
    operator fun invoke(): Flow<List<Album>> = flow{
        try {
            val musicFiles = repository.getMusicFiles()
            val albums = musicFiles.groupBy { it.album }
                .map { (albumName, musicList) ->
                    Album(musicList[0].albumId, albumName, musicList[0].albumArtUri, musicList.size)
                }
            emit(albums)
        }catch (e: Exception){
            Log.e("GetAlbumsUseCase", "invoke: ${e.message}")
            emit(emptyList())
        }
    }

}