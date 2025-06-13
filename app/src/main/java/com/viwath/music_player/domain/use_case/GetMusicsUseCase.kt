package com.viwath.music_player.domain.use_case

import android.util.Log
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.SortOrder
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMusicsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(sortOrder: SortOrder): Flow<Resource<List<Music>>> = flow {
        emit(Resource.Loading())
        try {
            val musicFiles = musicRepository.getMusicFiles()
            Log.d("GetMusicUseCase", "invoke: $musicFiles")
            val musics = when (sortOrder) {
                SortOrder.TITLE -> musicFiles.sortedBy { it.title }
                SortOrder.DURATION -> musicFiles.sortedBy { it.duration }
                SortOrder.DATE -> musicFiles.sortedBy { it.addDate }
            }
            emit(Resource.Success(musics))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)
}