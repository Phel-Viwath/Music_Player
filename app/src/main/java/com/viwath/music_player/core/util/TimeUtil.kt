package com.viwath.music_player.core.util

fun Long.formatTime(): String{
    val totalSecond = this / 1000
    val minute = totalSecond / 60
    val second = totalSecond % 60
    return "%02d:%02d".format(minute, second)
}