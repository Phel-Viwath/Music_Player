package com.viwath.music_player.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.viwath.music_player.presentation.ui.screen.MainApp
import com.viwath.music_player.presentation.ui.theme.Music_PlayerTheme
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.VisualizerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        }
        val readPermissionGranted = permissions[getReadPermission()] != true
        val notificationPermissionGranted = permissions[postNotificationPermission] == true

        if (readPermissionGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            viewModel.loadMusicFiles()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

        if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkPermissions()

        setContent {
            Music_PlayerTheme {
                MainApp(viewModel)
            }
        }
    }

    private fun checkPermissions() {
        val readPermission = getReadPermission()
        val permissions = mutableListOf(readPermission, Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val hasReadPermission = ContextCompat.checkSelfPermission(
            this, readPermission
        ) == PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        val hasRecordAudioPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasReadPermission || !hasNotificationPermission || !hasRecordAudioPermission) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            viewModel.loadMusicFiles()
        }
    }

    private fun getReadPermission(): String {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

}