package com.viwath.music_player.core.util

import android.content.IntentSender

sealed class DeleteResult {
    object Success : DeleteResult()
    data class NeedPermission(val intentSender: IntentSender) : DeleteResult()
}