package com.viwath.music_player.domain.use_case.playlist_use_case

import android.util.Log
import com.viwath.music_player.domain.model.dto.PlaylistDto
import com.viwath.music_player.domain.model.dto.toPlaylistDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetAllPlaylistUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(): Flow<List<PlaylistDto>>{
        return try {
            val allSongPlaylist = repository.getAllPlaylistSongs()
            val allPlaylist = repository.getPlaylists()
            combine(allPlaylist, allSongPlaylist) { playlists, songs ->
                playlists.map { playlist ->
                    val playlistSongs = songs.filter { it.playlistId == playlist.playlistId }
                    playlist.toPlaylistDto().copy(totalSong = playlistSongs.size)
                }
            }
        }catch (e: Exception){
            Log.e("GetAllPlaylistUseCase", "invoke: ${e.message}")
            emptyFlow()
        }
    }
}