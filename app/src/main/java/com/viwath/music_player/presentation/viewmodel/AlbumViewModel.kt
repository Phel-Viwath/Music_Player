package com.viwath.music_player.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.use_case.album_use_case.AlbumUseCase
import com.viwath.music_player.presentation.ui.screen.event.AlbumScreenEvent
import com.viwath.music_player.presentation.ui.screen.state.AlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val useCase: AlbumUseCase
): ViewModel(){

    private val _state = mutableStateOf(AlbumState())
    val state: State<AlbumState> get() = _state

    init {
        viewModelScope.launch {
            val albumsDeferred = async { loadAlbums() }
            albumsDeferred.await()
        }
    }

    fun onEvent(event: AlbumScreenEvent){
        when(event){
            is AlbumScreenEvent.GetAlbums -> {
                loadAlbums()
            }
            is AlbumScreenEvent.GetAlbum -> {
                loadAlbum(event.albumId)
            }
        }
    }


    private fun loadAlbums(){
        viewModelScope.launch {
            useCase.getAlbumsUseCase().collect{
                _state.value = _state.value.copy(
                    albums = it
                )
            }
        }
    }

    private fun loadAlbum(albumId: Long){
        viewModelScope.launch {
            useCase.getAlbumUseCase(albumId = albumId).collect { resource ->
                when(resource){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, music = resource.data ?: emptyList())
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