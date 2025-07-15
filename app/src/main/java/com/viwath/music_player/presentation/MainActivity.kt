package com.viwath.music_player.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.viwath.music_player.presentation.ui.screen.MainApp
import com.viwath.music_player.presentation.ui.theme.Music_PlayerTheme
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicViewModel by viewModels()
    private val playlistViewModel : PlaylistViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val readPermissionGranted = permissions[getReadPermission()] == true
        val notificationPermissionGranted = permissions[getNotificationPermission()] == true

        if (readPermissionGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            musicViewModel.loadMusicFiles()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

        if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backgroundColor = Color(0xFF191834)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = backgroundColor.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = backgroundColor.toArgb()
            )
        )
        checkPermissions()

        setContent {
            Music_PlayerTheme {
                MainApp(musicViewModel, playlistViewModel)
            }
        }
    }

    private fun checkPermissions() {
        val readPermission = getReadPermission()
        val notificationPermission = getNotificationPermission()
        val permissions = mutableListOf(readPermission, notificationPermission)

        val hasReadPermission = ContextCompat.checkSelfPermission(
            this, readPermission
        ) == PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, notificationPermission
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!hasReadPermission || !hasNotificationPermission) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            musicViewModel.loadMusicFiles()
        }
    }

    private fun getReadPermission(): String {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun getNotificationPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        }
    }

}