package com.viwath.music_player.core.util.permission

import android.Manifest
import android.os.Build

enum class PermissionType(val permissions: List<String>) {
    AUDIO_AND_NOTIFICATION(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.FOREGROUND_SERVICE
            )
        else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    )
}