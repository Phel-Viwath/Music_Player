package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.PlaylistSong
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddPlaylistSongUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(playlistSongs: List<PlaylistSong>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val lastSong = playlistSongs.last()
            val playlistId = lastSong.playlistId
            val thumbnail = lastSong.musicUri
            Log.d("AddPlaylistSongUseCase", "invoke: $playlistId, $thumbnail")
            val updateThumbnail = repository.updatePlaylistThumbnail(playlistId, thumbnail)
            if (updateThumbnail <= 0){
                emit(Resource.Error("Update thumbnail failed"))
                return@flow
            }
            repository.addMusicToPlaylist(playlistSongs)
            emit(Resource.Success(true))
        }catch (e: Exception){
            FirebaseCrashlytics.getInstance().log("AddPlaylistSongUseCase")
            FirebaseCrashlytics.getInstance().recordException(e)
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}