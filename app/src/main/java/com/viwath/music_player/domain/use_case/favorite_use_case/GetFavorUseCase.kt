package com.viwath.music_player.domain.use_case.favorite_use_case

import android.util.Log
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.core.common.SortOrder
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.toMusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFavorUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(sort: SortOrder): Flow<Resource<List<MusicDto>>> = flow {
        emit(Resource.Loading())
        try {
            val musics = when (sort) {
                SortOrder.DATE -> repository.getFavoriteMusicByDate()
                SortOrder.DURATION -> repository.getFavoriteMusicByDuration()
                SortOrder.TITLE -> repository.getFavoriteMusicByTitle()
            }
            emit(Resource.Success(musics.first().map { it.toMusicDto() }))
        } catch (e: Exception) {
            Log.e("GetFavorUseCase", "invoke: ${e.message}")
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

}