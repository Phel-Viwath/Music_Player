package com.viwath.music_player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Constant
import com.viwath.music_player.core.util.Resource
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
    private val useCase: PlaylistUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _state = mutableStateOf(PlaylistState())
    val state : State<PlaylistState> get() = _state

    init {
        viewModelScope.launch {
            val playlistDeferred = async { loadPlaylists() }
            playlistDeferred.await()
        }
    }

    fun onEvent(event: PlaylistEvent){
        when(event){
            is PlaylistEvent.OnCreatePlaylist -> newPlaylist()
            is PlaylistEvent.OnDeletePlaylist -> deletePlaylist()
            is PlaylistEvent.LoadPlaylistSong -> loadPlaylistSong()
            is PlaylistEvent.LoadPlaylist -> loadPlaylist()
            is PlaylistEvent.NewPlaylist -> {
                _state.value = _state.value.copy(playlist = event.playlist)
            }
            is PlaylistEvent.DeletePlaylist -> {
                _state.value = _state.value.copy(playlist = event.playlist)
            }
            is PlaylistEvent.AddPlaylistSong -> addPlaylistSong(event.musicList)
        }
    }

    fun loadPlaylists(){
        viewModelScope.launch {
            useCase.getAllPlaylistUseCase().collect {
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
                Log.d("PlaylistViewModel", "deletePlaylist: $it")
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

    private fun loadPlaylistSong(){
        Log.d("PlaylistViewModel","loadPlaylistSong")
        var playlistId: Long? = null
        savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.let { id ->
            Log.d("PlaylistViewModel", "playlistId:$id")
            playlistId = id.toLong()
        }
        if (playlistId == 0L || playlistId == null){
            _state.value = _state.value.copy(
                error = "Invalid playlist ID",
                isLoading = false
            )
            return
        }
        viewModelScope.launch {
            Log.d("PlaylistViewModel", "loadPlaylistSong: in viewmodel scope")
            useCase.getPlaylistSongUseCase(playlistId).collect { result ->
                Log.d("PlaylistViewModel", "loadPlaylistSong: $result")
                when(result){
                    is Resource.Loading<*> -> { _state.value = _state.value.copy(isLoading = true) }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(
                            playlistSongs = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(
                            error = result.message ?: "Unknown error",
                            isLoading = false
                        )
                    }
                }
            }
        }

    }

    private fun addPlaylistSong(musicList: List<MusicDto>){
        var playlistId: Long? = null
        savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.let { id ->
            Log.d( "PlaylistViewmodel", "addPlaylistSong: playlist id is $id")
            playlistId = id.toLong()
        }
        if (playlistId == null || playlistId == 0L){
            _state.value = _state.value.copy(error = "Playlist Id is null")
            return
        }

        val playlistSong = musicList.mapIndexed { index, dto ->
            PlaylistSong(
                playlistId = playlistId,
                musicId = dto.id.toString(),
                musicUri = if (index == musicList.size - 1) dto.imagePath ?: "" else "",
            )
        }

        Log.d("PlaylistViewModel", "addPlaylistSong: $playlistSong")
        viewModelScope.launch {
            useCase.addPlaylistSongUseCase(
                playlistSong
            ).collect{ result ->
                Log.d("PlaylistViewModel", "addPlaylistSong: $result")
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

    private fun loadPlaylist(){
        savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.let {
            viewModelScope.launch {
                useCase.getPlaylistUseCase(it.toLong()).collect { result ->
                    when(result){
                        is Resource.Loading -> { _state.value = _state.value.copy(isLoading = true) }
                        is Resource.Success -> {
                            _state.value = _state.value.copy(
                                playlist = result.data,
                                isLoading = false
                            )
                        }
                        is Resource.Error<*> -> {
                            _state.value = _state.value.copy(
                                error = result.message ?: "Unknown error",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }


}