@file:Suppress("DEPRECATION")

package com.viwath.music_player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.SortOrder
import com.viwath.music_player.domain.use_case.GetMusicsUseCase
import com.viwath.music_player.presentation.MusicPlayerManager
import com.viwath.music_player.presentation.ui.screen.state.MusicState
import com.viwath.music_player.presentation.ui.screen.state.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val useCase: GetMusicsUseCase,
    private val musicPlayerManager: MusicPlayerManager
): ViewModel(){
    private val _state = mutableStateOf(MusicState())
    val state: State<MusicState> get() = _state

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState.asStateFlow()

    init {
        Log.d("MusicViewModel", "Init ViewModel: Called")
        loadMusicFiles()
        musicPlayerManager.bindService()

        viewModelScope.launch {
            musicPlayerManager.isConnected.collect { isConnected ->
                if (isConnected){
                    musicPlayerManager.getServicePlaybackState()?.let { servicePlaybackState ->
                        launch{
                            servicePlaybackState.collect { playbackState ->
                                _playbackState.value = playbackState
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadMusicFiles(){
        viewModelScope.launch {
            useCase(SortOrder.TITLE).collect { result ->
                Log.d("MusicViewModel", "loadMusicFiles: $result")
                when(result){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, musicFiles = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message ?: "Unknown error", isLoading = false)
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }
    fun playMusic(music: Music, musics: List<Music> = emptyList()) {
        musicPlayerManager.playMusic(music, musics)
    }

    fun pauseMusic() {
        musicPlayerManager.pauseMusic()
    }

    fun nextMusic() {
        musicPlayerManager.nextMusic()
    }

    fun previousMusic() {
        musicPlayerManager.previousMusic()
    }

    fun stopMusic() {
        musicPlayerManager.stopMusic()
    }

    fun seekTo(position: Long) {
        musicPlayerManager.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayerManager.unbindService()
    }
}