package com.viwath.music_player.domain.use_case.favorite_use_case

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveFavorUseCase @Inject constructor(
    private val repository: MusicRepository
){
    operator fun invoke(music: FavoriteMusic): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())
        try {
            val effectedRow = repository.removeFavorite(music)
            if (effectedRow > 0)
                emit(Resource.Success(effectedRow))
            else
                emit(Resource.Error("No rows affected"))
        }catch (e: Exception){
            FirebaseCrashlytics.getInstance().log("RemoveFavorUseCase")
            FirebaseCrashlytics.getInstance().recordException(e)
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}