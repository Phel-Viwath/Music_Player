package com.viwath.music_player.presentation.ui.screen

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.music_list.component.MiniPlayer
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.VisualizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    musicViewModel: MusicViewModel,
    visualizerViewModel: VisualizerViewModel
){
    val navController = rememberNavController()

    var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }
    var currentMusic by remember { mutableStateOf<Music?>(null) }
    var showMusicDetail by remember { mutableStateOf(false) }
    var showMiniPlayer by remember { mutableStateOf(false) }


    Scaffold (
        bottomBar = { BottomNavigationBar(navController) }
    ){ innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = BottomNavItem.Home.route
        ){
            composable(BottomNavItem.Home.route){
                HomeScreen(
                    viewModel = musicViewModel,
                    onMusicListLoaded = { musicList = it },
                    onMusicSelected = {
                        currentMusic = it
                        showMusicDetail = true
                    }

                )
            }
            composable(BottomNavItem.Downloads.route){}
        }
    }

    if (showMusicDetail && currentMusic != null){
        BottomSheetMusic(
            currentMusic = currentMusic!!,
            musicViewModel = musicViewModel,
            visualizerViewModel = visualizerViewModel,
            onDismiss = { showMusicDetail = it }
        )
    }

    if (!showMusicDetail && currentMusic != null){
        MiniPlayer(
            music = currentMusic!!,
            musicViewModel = musicViewModel,
            onTap = {}
        )
    }




}

@Composable
fun BottomNavigationBar(navController: NavHostController){
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Downloads
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier.height(60.dp),
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = MaterialTheme.colorScheme.surface
    ){
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(item.title)
                },
                selected = currentRoute == item.route,
                onClick = {
                    if(currentRoute != item.route){
                        navController.navigate(item.route){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

