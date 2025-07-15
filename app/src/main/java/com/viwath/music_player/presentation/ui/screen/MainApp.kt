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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.PlaylistDto
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
    musicViewModel: MusicViewModel,
    playlistViewModel: PlaylistViewModel
){
    val navController = rememberNavController()
    var musicList by remember { mutableStateOf<List<MusicDto>>(emptyList()) }

    var selectedPlaylist by remember { mutableStateOf<PlaylistDto?>(null) }
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
                    // Bottom Navigation
//                    BottomNavigationBar(navController)
                }
            }
        }
    ){ innerPadding ->
        AmbientGradientBackground(modifier = Modifier.fillMaxSize())
        NavHost(
            modifier = Modifier,
            navController = navController,
            startDestination = Routes.HOME
        ){
            composable(Routes.HOME){
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = musicViewModel,
                    onMusicListLoaded = {
                        musicList = it
                    },
                    onMusicSelected = {
                        currentMusic = it
                        showMusicDetail = true
                    },
                    onNavigateToPlaylistMusic = { playlist ->
                        println("Navigation triggered for playlist: ${playlist.name}")
                        selectedPlaylist = playlist
                        navController.navigate(Routes.PLAYLIST_MUSIC)
                    },
                    initialTab = homeInitialTab
                )
            }
            composable(Routes.PLAYLIST_MUSIC){
                selectedPlaylist?.let { playlistDto ->
                    PlaylistMusicScreen(
                        playlistDto = playlistDto,
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
                        onAddMusicClick = {
                            navController.navigate(Routes.MUSIC_PICKER)
                        }
                    )
                }
            }
            composable(Routes.MUSIC_PICKER){
                MusicPicker(
                    modifier = Modifier,
                    musicList = musicList,
                    onMusicSelected = {

                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
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

//@Composable
//fun BottomNavigationBar(navController: NavHostController){
//    val items = listOf(
//        BottomNavItem.Home,
//        BottomNavItem.Downloads
//    )
//
//    val navBackStackEntry = navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry.value?.destination?.route
//
//    NavigationBar(
//        modifier = Modifier.height(56.dp),
//        containerColor = Color(0xFF8B5CF5)
//    ){
//        items.forEach { item ->
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        imageVector = item.icon,
//                        contentDescription = item.title
//                    )
//                },
//                selected = currentRoute == item.route,
//                onClick = {
//                    if(currentRoute != item.route){
//                        navController.navigate(item.route){
//                            popUpTo(navController.graph.startDestinationId){
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                }
//            )
//        }
//    }
//}
//
