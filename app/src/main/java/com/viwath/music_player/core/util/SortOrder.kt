package com.viwath.music_player.core.util

enum class SortOrder {
    TITLE,
    DURATION,
    DATE;

    fun displayName() : String = name.lowercase().replaceFirstChar { it.uppercase() }
}