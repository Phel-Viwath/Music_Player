package com.viwath.music_player.domain.use_case.album_use_case

import android.util.Log
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.domain.repository.MusicRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumsUseCase @Inject constructor(
    private val repository: MusicRepository
){
    operator fun invoke(): Flow<Resource<List<Album>>> = flow{
        emit(Resource.Loading())
        try {
            val albums = repository.getAlbums()
            emit(Resource.Success(albums))
        }catch (e: Exception){
            Log.e("GetAlbumsUseCase", "invoke: ${e.message}")
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}