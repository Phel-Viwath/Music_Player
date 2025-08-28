package com.viwath.music_player.domain.use_case.album_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.toMusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(albumId: Long): Flow<Resource<List<MusicDto>>> = flow {
        emit(Resource.Loading())
        if (albumId == -1L){
            emit(Resource.Error("Album not found"))
            return@flow
        }
        try {
            val musicFiles = repository.getMusicFiles()
            val album = musicFiles.filter { it.albumId == albumId }
                .map { music -> music.toMusicDto() }
            emit(Resource.Success(album))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}