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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.viwath.music_player.core.util.Constant
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.Screen
import com.viwath.music_player.presentation.ui.screen.music_detail.MusicDetailScreen
import com.viwath.music_player.presentation.ui.screen.music_list.MusicListScreen
import com.viwath.music_player.presentation.ui.theme.Music_PlayerTheme
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    private var musicList: List<Music> = emptyList()
    private var currentMusic: Music? = null

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
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    bottomBar = {
//                        NavigationBar{
//                            val currentDestination = navController.currentBackStackEntry?.destination?.route
//
//                        }
//                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "music_list_screen"
                    ){
                        composable(
                            route = Screen.MusicListScreenRoute.route
                        ){
                            MusicListScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(innerPadding)
                                    .background(Color.Black),
                                navController = navController,
                                viewModel = viewModel,
                                onMusicListLoaded = { loadMusicList ->
                                    musicList = loadMusicList
                                },
                                onMusicSelected = { selectedMusic ->
                                    currentMusic = selectedMusic
                                }
                            )
                        }

                        composable(
                            route = Screen.MusicDetailScreenRoute.route + "/{musicId}",
                            arguments = listOf(navArgument(Constant.PARAM_ID){type = NavType.LongType})
                        ) {
                            MusicDetailScreen(
                                modifier = Modifier
                                    .padding(innerPadding),
                                music = currentMusic ?: throw Exception("Music is null"),
                            )
                        }
                    }

                }
            }
        }
    }

    private fun checkPermissions() {
        val readPermission = getReadPermission()
        val permissions = mutableListOf(readPermission)

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

        if (!hasReadPermission || !hasNotificationPermission) {
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