package com.viwath.music_player.domain.use_case.favorite_use_case

import android.util.Log
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.repository.MusicRepository

class GetFavorByIdUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(id: Long): FavoriteMusic? {
        return try{
            repository.getFavoriteMusicById(id)
        }catch (e: Exception){
            Log.e("GetFavorByIdUseCase", "invoke: ${e.message}")
            null
        }
    }
}