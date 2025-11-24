package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewPlaylistUseCase @Inject constructor(
    private val repository: MusicRepository
){
    operator fun invoke(playlist: Playlist): Flow<Resource<Boolean>>  = flow{
        emit(Resource.Loading())
        try {
            repository.newPlayList(playlist)
            emit(Resource.Success(true))
        }catch (e: Exception){
            FirebaseCrashlytics.getInstance().log("NewPlaylistUseCase")
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("NewPlaylistUseCase", "invoke: ${e.message}")
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}