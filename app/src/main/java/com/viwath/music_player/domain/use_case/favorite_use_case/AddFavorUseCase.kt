package com.viwath.music_player.domain.use_case.favorite_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddFavorUseCase @Inject constructor(
    private val repository: MusicRepository
){
    operator fun invoke(music: FavoriteMusic): Flow<Resource<Long>> = flow {
        emit(Resource.Loading())
        try {
            val rowId = repository.addFavorite(music)
            emit(Resource.Success(rowId))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }

    }
}