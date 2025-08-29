package com.viwath.music_player.domain.use_case

import com.viwath.music_player.domain.repository.MusicRepository
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke() {
        repository.clearCache()
    }
}