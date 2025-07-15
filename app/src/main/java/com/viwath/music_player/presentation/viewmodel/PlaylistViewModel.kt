package com.viwath.music_player.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.use_case.playlist_use_case.PlaylistUseCase
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.ui.screen.state.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val useCase: PlaylistUseCase
): ViewModel(){

    private val _state = mutableStateOf(PlaylistState())
    val state : State<PlaylistState> get() = _state


    init {
        viewModelScope.launch {
            val playlistDeferred = async { loadPlaylist() }
            playlistDeferred.await()
        }
    }

    fun onEvent(event: PlaylistEvent){
        when(event){
            is PlaylistEvent.OnCreatePlaylist -> newPlaylist()
            is PlaylistEvent.OnDeletePlaylist -> deletePlaylist()
            is PlaylistEvent.LoadPlaylistSong -> loadPlaylistSong(event.playlistId)
            is PlaylistEvent.NewPlaylist -> {
                _state.value = _state.value.copy(playlist = event.playlist)
            }
            is PlaylistEvent.DeletePlaylist -> {
                _state.value = _state.value.copy(playlist = event.playlist)
            }
            is PlaylistEvent.AddPlaylistSong -> addPlaylistSong(event.musicList, event.playlist)
        }
    }

    fun loadPlaylist(){
        viewModelScope.launch {
            useCase.getPlaylistUseCase().collect {
                _state.value = _state.value.copy(
                    playlists = it
                )
            }
        }
    }

    private fun newPlaylist(){
        val playlist = _state.value.playlist
        if (playlist == null){
            _state.value = _state.value.copy(error = "Playlist is null")
            return
        }
        viewModelScope.launch {
            useCase.newPlaylistUseCase(playlist).collect {
                when(it){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, success = "New playlist created.")
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = it.message!!)
                    }
                }
            }
        }
    }


    private fun deletePlaylist(){
        val playlist = _state.value.playlist
        if (playlist == null){
            _state.value = _state.value.copy(error = "Playlist is null")
            return
        }
        viewModelScope.launch {
            useCase.deletePlaylistUseCase(playlist).collect {
                when(it){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, success = "Playlist deleted")
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = it.message!!)
                    }
                }
            }
        }
    }

    /// MusicPlaylist

    private fun loadPlaylistSong(playlistId: Long){
        viewModelScope.launch {
            useCase.getPlaylist(playlistId).collect { result ->
                when(result){
                    is Resource.Loading<*> -> { _state.value = _state.value.copy(isLoading = true) }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(
                            playlistSongs = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(
                            error = result.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }


    private fun addPlaylistSong(musicList: List<MusicDto>, playlist: Playlist){
        if (musicList.isEmpty()){
            _state.value = _state.value.copy(error = "Playlist is null")
            return
        }
        val playlistSong = musicList.mapIndexed { index, dto ->
            PlaylistSong(
                playlistId = playlist.playlistId!!,
                musicId = dto.id.toString(),
                musicUri = if (index == 0) dto.uri else "",
            )
        }
        viewModelScope.launch {
            useCase.addPlaylistSongUseCase(
                playlistSong
            ).collect{ result ->
                when(result){
                    is Resource.Loading<*> -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(error = result.message ?: "Unknown error", isLoading = false)
                    }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(success = "Music added to playlist", isLoading = false)
                    }
                }
            }
        }

    }

}