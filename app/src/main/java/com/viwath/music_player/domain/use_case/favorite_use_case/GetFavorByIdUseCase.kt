package com.viwath.music_player.domain.use_case.favorite_use_case

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.repository.MusicRepository

class GetFavorByIdUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(id: Long): FavoriteMusic? {
        return try{
            repository.getFavoriteMusicById(id)
        }catch (e: Exception){
            FirebaseCrashlytics.getInstance().log("GetFavorByIdUseCase")
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("GetFavorByIdUseCase", "invoke: ${e.message}")
            null
        }
    }
}