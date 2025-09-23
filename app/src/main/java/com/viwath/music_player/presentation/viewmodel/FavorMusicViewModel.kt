package com.viwath.music_player.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.dto.toFavoriteMusic
import com.viwath.music_player.domain.use_case.favorite_use_case.FavoriteUseCase
import com.viwath.music_player.presentation.ui.screen.event.FavorEvent
import com.viwath.music_player.presentation.ui.screen.state.FavorMusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavorMusicViewModel @Inject constructor(
    private val useCase: FavoriteUseCase
): ViewModel(){

    private val _state = mutableStateOf(FavorMusicState())
    val state: State<FavorMusicState> get() = _state

    private val _setOfFavoriteId = mutableStateSetOf<String?>()

    init {
        loadFavorMusicList(SortOrder.DATE)
    }

    fun onEvent(event: FavorEvent){
        when(event){
            is FavorEvent.PasteDeleteData -> {
                _state.value = _state.value.copy(deleteMusic = event.music)
            }
            is FavorEvent.PasteInsertData -> {
                _state.value = _state.value.copy(music = event.music)
            }
            is FavorEvent.PasteCurrentMusicId -> {
                _setOfFavoriteId.add(event.id)
            }

            is FavorEvent.DeleteFavorite -> deleteFavorMusic()
            is FavorEvent.InsertFavorite -> insertFavorite()
            is FavorEvent.AddCurrentFavorite -> addCurrentFavorite(event.id)
            is FavorEvent.RemoveCurrentFavorite -> removeCurrentFavorite(event.id)
        }
    }

    fun loadFavorMusicList(sortOrder: SortOrder){
        viewModelScope.launch {
            useCase.getFavorUseCase(sortOrder).collect {
                when(it){
                    is Resource.Loading-> {
                        _state.value = _state.value.copy(idle = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = it.message, idle = false)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(favorMusicList = it.data, idle = false)
                    }
                }
            }
        }
    }

    fun isFavorite(id: String): Boolean {
        return _setOfFavoriteId.contains(id)
    }

    private fun addCurrentFavorite(id: String){
        if (_setOfFavoriteId.contains(id)) return
        _setOfFavoriteId.add(id)
    }

    private fun removeCurrentFavorite(id: String){
        if (!_setOfFavoriteId.contains(id)) return
        _setOfFavoriteId.remove(id)
    }

    private fun insertFavorite(){
        val favoriteMusic = _state.value.music?.toFavoriteMusic()
        if (favoriteMusic == null) {
            _state.value.copy(error = "empty music.")
            return
        }
        viewModelScope.launch {
            useCase.addFavorUseCase(favoriteMusic)
        }
    }

    private fun deleteFavorMusic(){
        val favorMusic = _state.value.deleteMusic?.toFavoriteMusic()
        if (favorMusic == null) {
            _state.value.copy(error = "empty music.")
            return
        }
        viewModelScope.launch {
            useCase.removeFavorUseCase(favorMusic)
        }
    }


    

}