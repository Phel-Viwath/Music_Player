package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.viwath.music_player.domain.model.dto.PlaylistDto
import com.viwath.music_player.domain.model.dto.toPlaylistDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllPlaylistUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(): Flow<List<PlaylistDto>> = flow{
        try {
            val allSongPlaylist = repository.getAllPlaylistSongs()
            val allPlaylist = repository.getPlaylists()
            allPlaylist.collect { playlists ->
                playlists.forEach { playlist ->
                    val playlistSongs = allSongPlaylist.first().filter { it.playlistId == playlist.playlistId }
                    val playlistDto = playlist.toPlaylistDto().copy(totalSong = playlistSongs.size)
                    emit(listOf(playlistDto))
                }
            }
        }catch (e: Exception){
            Log.e("GetAllPlaylistUseCase", "invoke: ${e.message}")
        }
    }
}