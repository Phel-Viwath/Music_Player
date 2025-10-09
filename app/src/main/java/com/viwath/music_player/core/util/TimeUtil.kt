package com.viwath.music_player.core.util

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatTime(): String{
    val totalSecond = this / 1000
    val minute = totalSecond / 60
    val second = totalSecond % 60
    return "%02d:%02d".format(minute, second)
}

fun dateFormatter(date: Long): String{
    val date = Date(date)
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}
