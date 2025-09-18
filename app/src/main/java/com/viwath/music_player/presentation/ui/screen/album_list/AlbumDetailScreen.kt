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
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.dialog.Dialog
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.ui.screen.event.AlbumScreenEvent
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.viewmodel.AlbumViewModel
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

/**
 * A Composable function that displays the detail screen for a specific album.
 * It shows a list of music tracks belonging to the album and allows the user to play them.
 *
 * This screen features a top app bar with navigation controls and an ambient gradient background.
 * It observes the state from [AlbumViewModel] to display the album's music list and handles
 * potential errors by showing a dialog. It also interacts with [MusicViewModel] to control
 * music playback.
 *
 * @param navController The [NavController] used for navigation actions, such as going back.
 * @param viewModel The [AlbumViewModel] instance for managing the state of the album details.
 *        It is provided by Hilt.
 * @param musicViewModel The [MusicViewModel] instance for controlling music playback.
 *        It is provided by Hilt.
 * @param onMusicSelected A callback function that is invoked when a music track is selected
 *        from the list. It passes the selected [MusicDto].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    navController: NavController,
    viewModel: AlbumViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel(),
    onMusicSelected: (MusicDto) -> Unit = {}
){
    val state = viewModel.state.value
    val playbackState = musicViewModel.playbackState.collectAsState().value
    val currentMusic = playbackState.currentMusic
    val isPaused = playbackState.isPaused

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel){
        viewModel.onEvent(AlbumScreenEvent.GetAlbum)
    }

    LaunchedEffect(state.albumDetailError){
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
                isPaused = isPaused,
                onMusicSelected = { selectedMusic ->
                    val isPlaying = currentMusic?.id == selectedMusic.id
                    if (!isPlaying) {
                        musicViewModel.onEvent(MusicEvent.OnPlay(selectedMusic, state.musics))
                    }
                    onMusicSelected(selectedMusic)
                }
            )
        }

        if (showDialog){
            Dialog(
                "V-Music",
                state.albumDetailError,
                onDismissRequest = { showDialog = false }
            )
        }

    }


}