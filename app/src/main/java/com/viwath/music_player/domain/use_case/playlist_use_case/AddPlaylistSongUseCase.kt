package com.viwath.music_player.domain.use_case.playlist_use_case

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
            repository.addMusicToPlaylist(playlistSongs)
            emit(Resource.Success(true))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}