package com.viwath.music_player.domain.use_case.playlist_use_case

data class PlaylistUseCase(
    val newPlaylistUseCase: NewPlaylistUseCase,
    val getPlaylistUseCase: GetAllPlaylistUseCase,
    val deletePlaylistUseCase: DeletePlaylistUseCase,
    val getPlaylist: GetPlaylistSongsUseCase,
    val addPlaylistSongUseCase: AddPlaylistSongUseCase
)
