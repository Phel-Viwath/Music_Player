package com.viwath.music_player.presentation.ui.screen.event

sealed class ResultMsg {
    data class Error(val message: String) : ResultMsg()
    data class Success(val message: String) : ResultMsg()
}