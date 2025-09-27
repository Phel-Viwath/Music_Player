package com.viwath.music_player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Constant
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.PlaylistSong
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.PlaylistDto
import com.viwath.music_player.domain.use_case.ClearCacheUseCase
import com.viwath.music_player.domain.use_case.favorite_use_case.FavoriteUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.PlaylistUseCase
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.ui.screen.state.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val useCase: PlaylistUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val favoriteUseCase: FavoriteUseCase
): ViewModel(){

    private val _state = mutableStateOf(PlaylistState())
    val state : State<PlaylistState> get() = _state

    private val _currentMusic = mutableStateOf<MusicDto?>(null)
    val currentMusic: State<MusicDto?> get() = _currentMusic

    private val _message = MutableSharedFlow<String>()
    val message get() = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            val playlistDeferred = async { loadPlaylists() }
            playlistDeferred.await()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clearCacheUseCase()
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
            is PlaylistEvent.SortPlaylistSong -> {
                _state.value = _state.value.copy(sortOrder = event.sortOrder)
            }
        }
    }

    fun loadPlaylists(){
        val sortOrder = _state.value.sortOrder
        viewModelScope.launch {
            val favoriteMusicCount = getAllFavoriteMusic(sortOrder).size
            Log.d("PlaylistViewModel", "loadPlaylists: $favoriteMusicCount")
            val favoritePlaylist = PlaylistDto(
                playlistId = 0L,
                name = "Favorite",
                createAt = System.currentTimeMillis(),
                thumbnail = null,
                totalSong = favoriteMusicCount
            )
            useCase.getAllPlaylistUseCase().collect { playlistDtos ->
                val playlist = playlistDtos.toMutableList()
                playlist.add(0, favoritePlaylist)
                _state.value = _state.value.copy(
                    playlists = playlist
                )
            }
        }
    }

    private fun newPlaylist(){
        val playlist = _state.value.playlist
        viewModelScope.launch {
            if (playlist == null){
                _message.emit("Playlist is null")
                return@launch
            }
            useCase.newPlaylistUseCase(playlist).collect {
                when(it){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit("New playlist created")
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(it.message.toString())
                    }
                }
            }
        }
    }


    private fun deletePlaylist(){
        val playlist = _state.value.playlist
        viewModelScope.launch {
            if (playlist == null){
                _message.emit("Playlist is null")
                return@launch
            }
            useCase.deletePlaylistUseCase(playlist).collect {
                Log.d("PlaylistViewModel", "deletePlaylist: $it")
                when(it){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit("Playlist deleted")
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(it.message.toString())
                    }
                }
            }
        }
    }

    /// MusicPlaylist

    private fun loadPlaylistSong(){
        var playlistId: Long? = null

        viewModelScope.launch {
            savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.let { id ->
                Log.d("PlaylistViewModel", "playlistId:$id")
                playlistId = id.toLong()
            }
            if (playlistId == null){
                _message.emit("Playlist Id is null")
                return@launch
            }
            if (playlistId == 0L){
                val favoriteSong = getAllFavoriteMusic(_state.value.sortOrder)
                _state.value = _state.value.copy(
                    playlistSongs = favoriteSong,
                    isLoading = false
                )
                return@launch
            }
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
                        Log.e("PlaylistViewModel", "loadPlaylistSong error: ${result.message}")
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }

    }

    private fun addPlaylistSong(musicList: List<MusicDto>){
        var playlistId: Long? = null
        viewModelScope.launch {
            savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.let { id ->
                Log.d( "PlaylistViewmodel", "addPlaylistSong: playlist id is $id")
                playlistId = id.toLong()
            }
            if (playlistId == null || playlistId == 0L){
                _message.emit("Invalid playlist ID")
                return@launch
            }

            val playlistSong = musicList.mapIndexed { index, dto ->
                PlaylistSong(
                    playlistId = playlistId,
                    musicId = dto.id.toString(),
                    musicUri = if (index == musicList.size - 1) dto.imagePath ?: "" else "",
                )
            }
            useCase.addPlaylistSongUseCase(
                playlistSong
            ).collect{ result ->
                Log.d("PlaylistViewModel", "addPlaylistSong: $result")
                when(result){
                    is Resource.Loading<*> -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(result.message ?: "Unknown error")
                    }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit("Music added to playlist")
                    }
                }
            }
        }
    }

    private fun addPlaylistSong(musicList: List<MusicDto>, playlistId: Long){
        viewModelScope.launch {
            if (playlistId == 0L){
                _message.emit("Invalid playlist ID")
                return@launch
            }

            val playlistSong = musicList.mapIndexed { index, dto ->
                PlaylistSong(
                    playlistId = playlistId,
                    musicId = dto.id.toString(),
                    musicUri = if (index == musicList.size - 1) dto.imagePath ?: "" else "",
                )
            }
            useCase.addPlaylistSongUseCase(
                playlistSong
            ).collect{ result ->
                Log.d("PlaylistViewModel", "addPlaylistSong: $result")
                when(result){
                    is Resource.Loading<*> -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(result.message ?: "Unknown error")
                    }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit("Music added to playlist")
                    }
                }
            }
        }
    }


    private fun loadPlaylist(){
        viewModelScope.launch {
            val id = savedStateHandle.get<String>(Constant.PLAYLIST_ID)?.toLong()
            if (id == null){
                _message.emit("Invalid playlist ID")
                return@launch
            }
            if (id == 0L){
                return@launch
            }
            useCase.getPlaylistUseCase(id).collect { result ->
                when(result){
                    is Resource.Loading -> { _state.value = _state.value.copy(isLoading = true) }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            playlist = result.data,
                            isLoading = false
                        )
                    }
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _message.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    private suspend fun getAllFavoriteMusic(sortOrder: SortOrder): List<MusicDto> {
        val favoriteMusic = mutableListOf<MusicDto>()
        favoriteUseCase.getFavorUseCase(sortOrder).collect { result ->
            when(result){
                is Resource.Error -> {
                    Log.e("PlaylistViewModel", "getAllFavoriteMusic: ${result.message}")
                    _message.emit(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    favoriteMusic.addAll(result.data ?: emptyList())
                }
            }
        }
        return favoriteMusic
    }


}