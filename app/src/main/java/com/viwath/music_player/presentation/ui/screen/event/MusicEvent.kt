package com.viwath.music_player.presentation.ui.screen.event

import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.dto.MusicDto

sealed class MusicEvent {
    data class Order(val sortOrder: SortOrder): MusicEvent()
    data class SearchTextChange(val searchText: String): MusicEvent()

    data object SearchClick: MusicEvent()

    data object OnLoadMusic: MusicEvent()

    data object GetOrder: MusicEvent()

    data object OnPlayNext: MusicEvent()
    data object OnPlayPrevious: MusicEvent()
    data object OnPause: MusicEvent()
    data object OnResume: MusicEvent()
    data object OnRepeatOne: MusicEvent()
    data object OnRepeatAll: MusicEvent()

    data class AddToPlayNext(val music: MusicDto, val musics: List<MusicDto> = emptyList()): MusicEvent()
    data class AddToPlayLast(val music: MusicDto): MusicEvent()
    data class DeleteMusic(val music: MusicDto): MusicEvent()

    data class ShuffleMode(val isShuffle: Boolean): MusicEvent()
    data class OnSeekTo(val position: Long): MusicEvent()
    data class OnPlay(val music: MusicDto, val musics: List<MusicDto> = emptyList()): MusicEvent()

}