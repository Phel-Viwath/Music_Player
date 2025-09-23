package com.viwath.music_player.domain.use_case.music_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteMusicUseCase @Inject constructor(
    private val repository: MusicRepository
) {

     operator fun invoke(music: Music): Flow<Resource<String>> = flow {
         emit(Resource.Loading())
        try {
            val deletedRows = repository.deleteMusic(music)
            if (deletedRows > 0) {
                emit(Resource.Success("File deleted successfully"))
            } else {
                emit(Resource.Error("Failed to delete music"))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}