package com.viwath.music_player.domain.use_case.album_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.toMusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumMusicUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(albumId: Long): Flow<Resource<List<MusicDto>>> = flow {
        emit(Resource.Loading())
        if (albumId == -1L){
            emit(Resource.Error("Album not found"))
            return@flow
        }
        try {
            val musicFiles = repository.getSongByAlbumId(albumId).map {
                it.toMusicDto()
            }
            emit(Resource.Success(musicFiles))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}