package com.viwath.music_player.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.album_list.AlbumDetailScreen
import com.viwath.music_player.presentation.ui.screen.component.BottomSheetMusic
import com.viwath.music_player.presentation.ui.screen.component.MiniPlayer
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.screen.playlist.component.MusicPicker
import com.viwath.music_player.presentation.ui.screen.playlist.component.PlaylistMusicScreen
import com.viwath.music_player.presentation.ui.screen.search_screen.SearchScreen
import com.viwath.music_player.presentation.viewmodel.AlbumViewModel
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

/*
* Bottom navigation has removed
* */

/**
 * The main entry point and container for the application's UI.
 * This composable function sets up the overall screen structure, including navigation,
 * a persistent mini-player, and the main content area.
 *
 * It manages the navigation flow between different screens like the home screen,
 * playlist details, album details, and a music picker. It also handles the state
 * for the currently playing music and the visibility of the full-screen music player.
 *
 * @param musicViewModel The [MusicViewModel] instance used for controlling music playback
 * and accessing music-related data. It is passed down to child composables that
 * need to interact with the music player.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    musicViewModel: MusicViewModel
){
    val navController = rememberNavController()
    val playbackState = musicViewModel.playbackState.collectAsState().value

    var currentMusic by remember { mutableStateOf<MusicDto?>(null) }
    var showMusicDetail by remember { mutableStateOf(false) }
    var homeInitialTab by remember { mutableIntStateOf(0) }

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            bottomBar = {
                if (!showMusicDetail && currentMusic != null){
                    MiniPlayer(
                        modifier = Modifier.height(70.dp),
                        onTap = { showMusicDetail = true },
                        isPlaying = playbackState.isPlaying,
                        currentMusic = currentMusic!!,
                        onResumeClick = { musicViewModel.onEvent(MusicEvent.OnResume) },
                        onPauseClick = { musicViewModel.onEvent(MusicEvent.OnPause) },
                        onPlayNextClick = { musicViewModel.onEvent(MusicEvent.OnPlayNext) },
                        duration = playbackState.duration,
                        currentPosition = playbackState.currentPosition,
                        onSeekTo = { position ->
                            musicViewModel.onEvent(MusicEvent.OnSeekTo(position))
                        }
                    )
                }
            },
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        ){ innerPadding ->
            NavHost(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                navController = navController,
                startDestination = Routes.HomeScreen.route
            ){
                composable(
                    route = Routes.HomeScreen.route
                ){
                    HomeScreen(
                        viewModel = musicViewModel,
                        onMusicSelected = {
                            currentMusic = it
                            showMusicDetail = true
                        },
                        navController = navController,
                        initialTab = homeInitialTab
                    )
                }
                composable(
                    route = Routes.PlaylistMusicScreen.route + "/{playlistId}"
                ){ backStackEntry ->
                    val playlistViewModel: PlaylistViewModel = hiltViewModel()
                    PlaylistMusicScreen(
                        musicViewModel = musicViewModel,
                        playlistViewModel = playlistViewModel,
                        onNavigateBack = {
                            homeInitialTab = 2
                            navController.popBackStack()
                        },
                        onMusicSelected = { selectedMusic ->
                            currentMusic = selectedMusic
                            showMusicDetail = true
                        },
                        navController = navController
                    )
                }

                composable(
                    Routes.MusicPickerScreen.route + "/{playlistId}",
                ){ backStackEntry ->
                    val playlistViewModel: PlaylistViewModel = hiltViewModel()
                    MusicPicker(
                        modifier = Modifier,
                        playlistViewModel = playlistViewModel,
                        navController = navController
                    )
                }

                composable(
                    Routes.AlbumDetailScreen.route + "/{albumId}"
                ) {
                    val albumViewModel: AlbumViewModel = hiltViewModel()
                    AlbumDetailScreen(
                        navController = navController,
                        viewModel = albumViewModel,
                        onMusicSelected = { selectedMusic ->
                            currentMusic = selectedMusic
                            showMusicDetail = true
                        }
                    )
                }

                composable(
                    route = Routes.SearchScreen.route
                ){
                    SearchScreen(
                        navController = navController
                    )
                }
            }// end nav host
        }// end scaffold
    }// end box



    if (showMusicDetail && currentMusic != null){
        BottomSheetMusic(
            currentMusic = currentMusic!!,
            musicViewModel = musicViewModel,
            onDismiss = { showMusicDetail = it }
        )
    }

}
