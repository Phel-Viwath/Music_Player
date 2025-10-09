package com.viwath.music_player.core.util.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionManager(
    val activity: ComponentActivity,
) {

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ results ->
        onPermissionResult?.invoke(results)
    }

    private var onPermissionResult: ((Map<String, Boolean>) -> Unit)? = null


    fun requestPermission(
        type: PermissionType,
        onResult: (granted: Boolean) -> Unit
    ) {
        val permissions = type.permissions
        if (permissions.isEmpty()) {
            onResult(true)
            return
        }

        onPermissionResult = { result ->
            val granted = result.all { it.value }
            onResult(granted)
        }

        launcher.launch(permissions.toTypedArray())
    }

    fun hasPermission(type: PermissionType): Boolean {
        return type.permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

        }
    }
}