package com.viwath.music_player.presentation.ui.screen.playlist.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.Routes
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.ui.screen.music_list.ShowDialog
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

/**
 * A composable function that displays the screen for a specific music playlist.
 *
 * This screen shows a list of songs within a selected playlist. It features a top app bar
 * displaying the playlist's name, a back button for navigation, and a "more" menu with options
 * to add more music or delete the entire playlist.
 *
 * The main content area either displays the list of songs or a message indicating that the
 * playlist is empty, with a button to add new songs. The screen handles loading states and
 * displays a progress indicator while fetching data. It also manages and displays error dialogs
 * if any issues occur during data fetching or playlist operations.
 *
 * When a song from the list is tapped, it is played via the `musicViewModel` and the UI navigates
 * to the music player screen.
 *
 * @param musicViewModel The view model for managing music playback and state.
 * @param playlistViewModel The view model for managing playlist data and state.
 * @param onMusicSelected A callback function invoked when a song is selected, typically used to
 *                        trigger navigation to the player screen.
 * @param onNavigateBack A callback function to handle back navigation.
 * @param navController The NavController for navigating to other screens, such as the `MusicPickerScreen`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMusicScreen(
    musicViewModel: MusicViewModel,
    playlistViewModel: PlaylistViewModel,
    onMusicSelected: (MusicDto) -> Unit = {},
    onNavigateBack: () -> Unit,
    navController: NavController
){
    // view-model state
    val state = playlistViewModel.state.value
    val currentMusic = musicViewModel.playbackState.collectAsState().value.currentMusic
    val isPaused = musicViewModel.playbackState.collectAsState().value.isPaused

    val context = LocalContext.current
    val playlist = remember (state.playlist) { state.playlist }
    val musicList = remember(state.playlistSongs){ state.playlistSongs }


    var showErrorDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    // load music
    LaunchedEffect(playlistViewModel){
        try {
            playlistViewModel.onEvent(PlaylistEvent.LoadPlaylist)
            playlistViewModel.onEvent(PlaylistEvent.LoadPlaylistSong)
        }catch (e: Exception){
            Log.e("PlaylistMusicScreen", "Failed to load playlist music: ", e)
            error = "Failed to load playlist music: ${e.message}"
            showErrorDialog = true
        }
    }

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()){
            error = state.error
            showErrorDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = playlist?.name ?: "Favorite",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }

                    DropDownMenus(
                        isShowMenu = showMenu,
                        onRemovePlaylistClick = {
                            playlist?.let { playlist ->
                                playlistViewModel.onEvent(PlaylistEvent.DeletePlaylist(playlist))
                                playlistViewModel.onEvent(PlaylistEvent.OnDeletePlaylist)
                            } ?: run {
                                Toast.makeText(context, "Invalid Playlist ID!", Toast.LENGTH_SHORT).show()
                            }
                            Log.d("PlaylistMusicScreen", "PlaylistMusicScreen: $playlist")
                            onNavigateBack()
                        },
                        onAddMusicClick = {
                            playlist?.let { playlist ->
                                navController.navigate(Routes.MusicPickerScreen.route + "/${playlist.playlistId}"){
                                    launchSingleTop = true
                                }
                            } ?: run {
                                Toast.makeText(context, "Invalid Playlist ID!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDismiss = {
                            showMenu = false
                        }
                    )
                }
            )// end of top bar
        }
    ){ innerPadding ->
        AmbientGradientBackground(modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Transparent)
        ){
            Log.d("UI", "PlaylistMusicScreen: ${musicList.isNotEmpty()}")
            if (musicList.isEmpty()){
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music note icon",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No Songs",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = {
                            playlist?.let { playlist ->
                                navController.navigate(Routes.MusicPickerScreen.route + "/${playlist.playlistId}"){
                                    launchSingleTop = true
                                }
                            } ?: run {
                                Toast.makeText(context, "Invalid Playlist ID!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ){
                        Text("Add", color = Color(0xFF800080))
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if (showErrorDialog){
                ShowDialog(
                    title = "Error",
                    message = error
                ) {
                    showErrorDialog = false
                }
            }

            MusicList(
                modifier = Modifier.fillMaxSize(),
                musicList = musicList,
                currentMusic = currentMusic,
                isPaused = isPaused,
                onMusicSelected = { selectedMusic ->
                    val isPlaying = currentMusic?.id == selectedMusic.id
                    if (!isPlaying) {
                        musicViewModel.onEvent(MusicEvent.OnPlay(selectedMusic, musicList))
                    }
                    onMusicSelected(selectedMusic)
                }
            )
        }

    }
}

@Composable
fun DropDownMenus(
    isShowMenu: Boolean = false,
    onDismiss: () -> Unit,
    onAddMusicClick: () -> Unit,
    onRemovePlaylistClick: () -> Unit
){
    DropdownMenu(
        expanded = isShowMenu,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = false)
    ) {
        DropdownMenuItem(
            text = { Text("Add Music", color = Color.White) },
            onClick = onAddMusicClick
        )
        DropdownMenuItem(
            text = { Text("Remove", color = Color.White) },
            onClick = onRemovePlaylistClick
        )
    }
}
