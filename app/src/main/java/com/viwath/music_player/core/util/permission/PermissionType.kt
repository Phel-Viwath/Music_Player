package com.viwath.music_player.core.util.permission

import android.Manifest
import android.os.Build

enum class PermissionType(val permissions: List<String>) {
    AUDIO_AND_NOTIFICATION(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    )
}