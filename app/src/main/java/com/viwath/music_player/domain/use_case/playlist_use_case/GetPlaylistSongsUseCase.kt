package com.viwath.music_player.domain.use_case.playlist_use_case

import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.toMusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlaylistSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    private val musicList = mutableListOf<MusicDto>()

    operator fun invoke(id: Long): Flow<Resource<List<MusicDto>>> = flow{
        emit(Resource.Loading())
        try {
            val musicFiles = repository.getMusicFiles()
            val playlist = repository.getPlaylistSongs(id).map { playlistSongs ->
                playlistSongs.mapNotNull { playlistSong ->
                    musicFiles.find { music ->
                        music.id.toString() == playlistSong.musicId || music.uri == playlistSong.musicUri
                    }
                }
            }
            playlist.collect {
                val musicDto = it.map { music ->
                    music.toMusicDto()
                }
                musicList.addAll(musicDto)
            }
            emit(Resource.Success(musicList))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "Unknown error"))
        }

    }
}