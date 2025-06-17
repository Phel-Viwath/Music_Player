package com.viwath.music_player.domain.use_case

import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.repository.MusicRepository
import javax.inject.Inject

class RemoveFavorUseCase @Inject constructor(
    private val repository: MusicRepository
){

    @Throws(Exception::class)
    suspend operator fun invoke(music: FavoriteMusic){
        repository.removeFavorite(music)
    }

}