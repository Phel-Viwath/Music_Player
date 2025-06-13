package com.viwath.music_player.domain.use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.SortOrder
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class GetFavorUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(sort: SortOrder):  Flow<Resource<List<FavoriteMusic>>> = flow{
        emit(Resource.Loading())
        try {
            val musics = when(sort){
                SortOrder.DATE -> repository.getFavoriteMusicByDate()
                SortOrder.DURATION -> repository.getFavoriteMusicByDuration()
                SortOrder.TITLE -> repository.getFavoriteMusicByTitle()
            }
            emit(Resource.Success(musics.toList()))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}