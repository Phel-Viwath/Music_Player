package com.viwath.music_player.domain.use_case.playlist_use_case

data class PlaylistUseCase(
    val newPlaylistUseCase: NewPlaylistUseCase,
    val getAllPlaylistUseCase: GetAllPlaylistUseCase,
    val deletePlaylistUseCase: DeletePlaylistUseCase,
    val getPlaylistSongUseCase: GetPlaylistSongsUseCase,
    val addPlaylistSongUseCase: AddPlaylistSongUseCase,
    val getPlaylistUseCase: GetPlaylistUseCase
)
