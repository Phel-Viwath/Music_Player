package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.toMusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlaylistSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(id: Long): Flow<Resource<List<MusicDto>>> = flow{
        Log.d("GetPlaylistSongsUseCase", "invoke: GetPlaylistSongsUseCase called.")
        emit(Resource.Loading())
        try {
            Log.d("GetPlaylistSongsUseCase", "invoke: Try block called.")
            val musicFiles = repository.getMusicFiles()
            repository.getPlaylistSongs(id).collect { playlistSongs ->
                val matchingFiles = playlistSongs.mapNotNull { playlistSong ->
                    musicFiles.find{ music ->
                        music.id.toString() == playlistSong.musicId || music.uri == playlistSong.musicUri
                    }
                }

                val musicDtoList = matchingFiles.map { it.toMusicDto() }
                Log.d("GetPlaylistSongsUseCase", "invoke: $musicDtoList")
                emit(Resource.Success(musicDtoList))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }

    }
}