package com.viwath.music_player.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.music_list.component.MiniPlayer
import com.viwath.music_player.presentation.ui.screen.playlist.component.MusicPicker
import com.viwath.music_player.presentation.ui.screen.playlist.component.PlaylistMusicScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

/*
* Bottom navigation has removed
* */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    musicViewModel: MusicViewModel
){
    val navController = rememberNavController()

    var currentMusic by remember { mutableStateOf<MusicDto?>(null) }
    var showMusicDetail by remember { mutableStateOf(false) }
    var homeInitialTab by remember { mutableIntStateOf(0) }

    Scaffold (
        bottomBar = {
            Box{
                Column {
                    if (!showMusicDetail && currentMusic != null){
                        MiniPlayer(
                            music = currentMusic!!,
                            musicViewModel = musicViewModel,
                            onTap = { showMusicDetail = true }
                        )
                    }
                }
            }
        }
    ){ innerPadding ->
        AmbientGradientBackground(modifier = Modifier.fillMaxSize())
        NavHost(
            modifier = Modifier,
            navController = navController,
            startDestination = Routes.HomeScreen.route
        ){
            composable(
                route = Routes.HomeScreen.route
            ){
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
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
                    onMusicSelected = {
                        currentMusic = it
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
        }
    }

    if (showMusicDetail && currentMusic != null){
        BottomSheetMusic(
            currentMusic = currentMusic!!,
            musicViewModel = musicViewModel,
            onDismiss = { showMusicDetail = it }
        )
    }

}
