@file:Suppress("DEPRECATION")

package com.viwath.music_player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.MyPrefs
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.toMusic
import com.viwath.music_player.domain.use_case.music_use_case.MusicUseCase
import com.viwath.music_player.presentation.MusicPlayerManager
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.screen.state.MusicState
import com.viwath.music_player.presentation.ui.screen.state.PlaybackState
import com.viwath.music_player.presentation.ui.screen.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicPlayerManager: MusicPlayerManager,
    private val useCase: MusicUseCase,
    private val myPrefs: MyPrefs
): ViewModel(){
    private val _state = mutableStateOf(MusicState())
    val state: State<MusicState> get() = _state

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState.asStateFlow()

    private val _currentMusic = mutableStateOf<MusicDto?>(null)
    val currentMusic: State<MusicDto?> get() = _currentMusic

    private val _searchState = mutableStateOf(SearchState())
    val searchState: State<SearchState> get() = _searchState

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        musicPlayerManager.bindService()
        // get order from shared preferences
        getOrder()
        // load music files

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

        loadMusicFiles()
    }

    fun onEvent(event: MusicEvent){
        when(event){
            is MusicEvent.Order -> { saveOrder(event.sortOrder) }
            is MusicEvent.SearchClick -> searchMusic()
            is MusicEvent.SearchTextChange -> {
                _searchState.value = _searchState.value.copy(searchText = event.searchText)
                searchMusic()
            }

            is MusicEvent.OnLoadMusic -> loadMusicFiles()
            is MusicEvent.OnPlayNext -> nextMusic()
            is MusicEvent.OnPlayPrevious -> previousMusic()
            is MusicEvent.OnPause -> pauseMusic()
            is MusicEvent.OnResume -> resumeMusic()
            is MusicEvent.OnRepeatOne -> repeatOne()
            is MusicEvent.OnRepeatAll -> repeatAll()

            is MusicEvent.AddToPlayNext -> addToPlayNext(event.music)
            is MusicEvent.AddToPlayLast -> addToPlayLast(event.music)
            is MusicEvent.DeleteMusic -> deleteMusic(event.music)

            is MusicEvent.ShuffleMode -> shuffleMode(event.isShuffle)
            is MusicEvent.OnSeekTo -> seekTo(event.position)
            is MusicEvent.OnPlay -> playMusic(event.music, event.musics)
            is MusicEvent.GetOrder -> getOrder()
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayerManager.unbindService()
    }

    private fun loadMusicFiles(){
        viewModelScope.launch {
            useCase.getMusicsUseCase(SortOrder.TITLE).collect { result ->
                Log.d("MusicViewModel", "loadMusicFiles: $result")
                when(result){
                    is Resource.Success -> {
                        val data = when(_state.value.sortOrder){
                            SortOrder.TITLE -> result.data?.sortedBy { it.title }
                            SortOrder.DURATION -> result.data?.sortedBy { it.duration }
                            SortOrder.DATE -> result.data?.sortedBy { it.addDate }
                        }
                        data?.let {
                            musicPlayerManager.setPlaylist(
                                it.map{ musicDto ->  musicDto.toMusic() }
                            )
                        }
                        _state.value = _state.value.copy(isLoading = false, musicFiles = data ?: emptyList())
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
    private fun playMusic(musicDto: MusicDto, musics: List<MusicDto> = emptyList()) {
        val music = musicDto
        val musics = musics.map { it.toMusic() }
        _currentMusic.value = music
        musicPlayerManager.playMusic(music.toMusic(), musics)
    }

    private fun pauseMusic(): Unit = musicPlayerManager.pauseMusic()

    private fun resumeMusic(): Unit = musicPlayerManager.resumeMusic()

    private fun nextMusic(): Unit = musicPlayerManager.nextMusic()

    private fun previousMusic(): Unit = musicPlayerManager.previousMusic()

    private fun seekTo(position: Long): Unit = musicPlayerManager.seekTo(position)

    private fun shuffleMode(isShuffle: Boolean): Unit = musicPlayerManager.shuffleMode(isShuffle)

    private fun repeatOne(): Unit = musicPlayerManager.repeatOne()

    private fun repeatAll(): Unit = musicPlayerManager.repeatAll()

    private fun addToPlayNext(musicDto: MusicDto): Unit = musicPlayerManager.addToPlayNext(musicDto.toMusic())

    private fun addToPlayLast(musicDto: MusicDto): Unit = musicPlayerManager.addToPlayLast(musicDto.toMusic())

//    fun stopMusic() {
//        musicPlayerManager.stopMusic()
//    }

    private fun saveOrder(order: SortOrder) {
        try {
            myPrefs.saveString("order", order.name)
        }catch (e: IllegalStateException){
            Log.e("MusicViewModel", "saveOrder: ${e.message}")
        }catch (e: Exception){
            Log.e("MusicViewModel", "saveOrder: ${e.message}")
        }
    }

    private fun getOrder(){
        try {
            val order = myPrefs.getString("order", SortOrder.TITLE.name)
            val sortOrder = SortOrder.valueOf(order)
            _state.value = _state.value.copy(sortOrder = sortOrder)
        }catch (e: Exception){
            Log.e("MusicViewModel", "getOrder: ${e.message}")
        }
    }


    // search
    private fun searchMusic(){
        val searchText = _searchState.value.searchText
        if (searchText.isEmpty()){
            _searchState.value = _searchState.value.copy(musicList = emptyList())
            return
        }
        val musicList = _state.value.musicFiles
        viewModelScope.launch {
            try {
                val musicList = musicList.filter { musicDto ->
                    musicDto.title.contains(searchText, ignoreCase = true)
                }
                _searchState.value = _searchState.value.copy(musicList = musicList)
            }catch (e: Exception){
                Log.e("MusicViewModel", "searchMusic: ${e.message}")
            }
        }
    }

    private fun deleteMusic(music: MusicDto){
        viewModelScope.launch {
            useCase.deleteMusicUseCase(music.toMusic()).collect{ result ->
                when(result){
                    is Resource.Success -> {
                        _message.emit("Music deleted successfully")
                    }
                    is Resource.Error<*> -> {
                        _message.emit(result.message ?: "Unknown error")
                    }
                    is Resource.Loading<*> -> {}
                }
            }
        }
    }

}