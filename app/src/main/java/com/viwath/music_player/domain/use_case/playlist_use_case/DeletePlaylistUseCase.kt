package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(playlist: Playlist): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            repository.deletePlaylist(playlist)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            Log.e("DeletePlaylistUseCase", "invoke: ${e.message}")
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}