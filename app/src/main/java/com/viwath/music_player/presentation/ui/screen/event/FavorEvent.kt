package com.viwath.music_player.presentation.ui.screen.event

import com.viwath.music_player.domain.model.dto.MusicDto

sealed class FavorEvent {
    data object DeleteFavorite: FavorEvent()
    data object InsertFavorite: FavorEvent()

    data class AddCurrentFavorite(val id: String): FavorEvent()
    data class RemoveCurrentFavorite(val id: String): FavorEvent()

    data class PasteDeleteData(val music: MusicDto): FavorEvent()
    data class PasteInsertData(val music: MusicDto): FavorEvent()
    data class PasteCurrentMusicId(val id: String): FavorEvent()
}