package com.viwath.music_player.domain.use_case.playlist_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(id: Long): Flow<Resource<Playlist>> = flow {
        emit(Resource.Loading())
        try {
            val playlist = musicRepository.getPlaylist(id)
            emit(Resource.Success(playlist))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}