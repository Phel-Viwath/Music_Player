package com.viwath.music_player.presentation.ui.screen.album_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.component.Dialog
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.viewmodel.AlbumViewModel
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    navController: NavController,
    viewModel: AlbumViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel()
){
    val state = viewModel.state.value
    val playbackState = musicViewModel.playbackState.collectAsState().value
    val currentMusic = playbackState.currentMusic

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.error){
        if (state.error.isNotBlank())
            showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                }
            )// end of top bar
        }
    ){ innerPadding ->
        AmbientGradientBackground(modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ){
            MusicList(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(),
                musicList = state.musics,
                currentMusic = currentMusic,
                onMusicSelected = { selectedMusic ->
                    val isPlaying = currentMusic?.id == selectedMusic.id
                    if (!isPlaying) {
                        musicViewModel.playMusic(selectedMusic, state.musics)
                    }
                }
            )
        }

        if (showDialog){
            Dialog(
                "V-Music",
                state.error,
                onDismissRequest = { showDialog = false }
            )
        }

    }


}