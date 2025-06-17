package com.viwath.music_player.domain.use_case

data class FavoriteUseCase(
    val addFavorUseCase: AddFavorUseCase,
    val removeFavorUseCase: RemoveFavorUseCase,
    val getFavorUseCase: GetFavorUseCase
)
