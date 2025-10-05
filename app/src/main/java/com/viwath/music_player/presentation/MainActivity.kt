package com.viwath.music_player.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.viwath.music_player.presentation.ui.screen.MainApp
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.theme.Music_PlayerTheme
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicViewModel by viewModels()

    private val _shouldOpenMusicDetail = MutableStateFlow(false)
    val shouldOpenMusicDetail = _shouldOpenMusicDetail.asStateFlow()


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val readPermissionGranted = permissions[getReadPermission()] == true
        val notificationPermissionGranted = permissions[getNotificationPermission()] == true

        if (readPermissionGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            musicViewModel.onEvent(MusicEvent.OnLoadMusic)
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

        if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Add this for handling delete permission
    private val deleteRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Permission granted, notify ViewModel to retry delete
            musicViewModel.onEvent(MusicEvent.OnDeletePermissionGranted)
        } else {
            Toast.makeText(this, "Delete permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkPermissions()

        handleIntent(intent)

        setContent {
            Music_PlayerTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainApp(
                        musicViewModel,
                        shouldOpenMusicDetail
                    )
                }

                ObserveDeletePermission()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    @Composable
    fun ObserveDeletePermission(viewModel: MusicViewModel = hiltViewModel()){
        val deleteIntent = viewModel.deletePermissionIntent.collectAsStateWithLifecycle().value
        LaunchedEffect(deleteIntent){
            deleteIntent?.let { intentSender ->
                try {
                    deleteRequestLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                }catch (e: Exception){
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    //
    private fun handleIntent(intent: Intent?){
        if (intent?.getBooleanExtra("OPEN_MUSIC_DETAIL", false) == true)
            _shouldOpenMusicDetail.value = true
    }
    fun resetMusicDetailFlag() {
        _shouldOpenMusicDetail.value = false
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
            musicViewModel.onEvent(MusicEvent.OnLoadMusic)
        }
    }

    private fun getReadPermission(): String {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun getNotificationPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.POST_NOTIFICATIONS
        else ""
    }


}