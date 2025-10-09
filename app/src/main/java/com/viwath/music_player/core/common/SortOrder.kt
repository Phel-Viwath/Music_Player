package com.viwath.music_player.core.common

enum class SortOrder {
    TITLE,
    DURATION,
    DATE;

    fun displayName() : String = name.lowercase().replaceFirstChar { it.uppercase() }
}