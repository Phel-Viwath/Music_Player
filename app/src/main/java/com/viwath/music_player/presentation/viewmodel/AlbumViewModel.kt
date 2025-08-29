package com.viwath.music_player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.use_case.ClearCacheUseCase
import com.viwath.music_player.domain.use_case.album_use_case.AlbumUseCase
import com.viwath.music_player.presentation.ui.screen.event.AlbumScreenEvent
import com.viwath.music_player.presentation.ui.screen.state.AlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val useCase: AlbumUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _state = mutableStateOf(AlbumState())
    val state: State<AlbumState> get() = _state

    init {
        viewModelScope.launch {
            val albumsDeferred = async { loadAlbums() }
            albumsDeferred.await()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clearCacheUseCase()
        }
    }

    fun onEvent(event: AlbumScreenEvent){
        when(event){
            is AlbumScreenEvent.GetAlbums -> {
                loadAlbums()
            }
            is AlbumScreenEvent.GetAlbum -> {
                loadAlbum()
            }
        }
    }


    private fun loadAlbums(){
        viewModelScope.launch {
            useCase.getAlbumsUseCase().collect{
                when(it){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, albums = it.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = it.message ?: "")
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun loadAlbum(){
        var albumId: Long? = 0L
        savedStateHandle.get<String>("albumId")?.let {
            Log.d("AlbumViewModel", "loadAlbum id: $it")
            albumId = it.toLong()
        }
        Log.d("AlbumViewModel", "loadAlbum: $albumId")
        if (albumId == null || albumId == 0L){
            _state.value = _state.value.copy(error = "Album id is null", isLoading = false)
            return
        }
        viewModelScope.launch {
            useCase.getAlbumMusicUseCase(albumId = albumId).collect { resource ->
                when(resource){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, musics = resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.message ?: "")
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }



}