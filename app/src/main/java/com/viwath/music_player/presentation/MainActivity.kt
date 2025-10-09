package com.viwath.music_player.presentation

import android.content.Intent
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.viwath.music_player.core.util.permission.PermissionManager
import com.viwath.music_player.core.util.permission.PermissionType
import com.viwath.music_player.presentation.ui.screen.MainApp
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.theme.Music_PlayerTheme
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private val musicViewModel: MusicViewModel by viewModels()
    private val _shouldOpenMusicDetail = MutableStateFlow(false)
    val shouldOpenMusicDetail = _shouldOpenMusicDetail.asStateFlow()

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

        permissionManager = PermissionManager(this)

        checkAndRequestPermissions()
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

    private fun checkAndRequestPermissions() {
        // Request AUDIO permission first
        if (!permissionManager.hasPermission(PermissionType.AUDIO_AND_NOTIFICATION)) {
            permissionManager.requestPermission(PermissionType.AUDIO_AND_NOTIFICATION) { granted ->
                if (granted) {
                    Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show()
                    musicViewModel.onEvent(MusicEvent.OnLoadMusic)
                } else {
                    Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            musicViewModel.onEvent(MusicEvent.OnLoadMusic)
        }

    }

}