package com.viwath.music_player.presentation.ui.screen.playlist.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.model.dto.PlaylistDto
import com.viwath.music_player.domain.model.dto.toPlaylist
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMusicScreen(
    playlistDto: PlaylistDto,
    musicViewModel: MusicViewModel,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    onMusicSelected: (MusicDto) -> Unit = {},
    onNavigateBack: () -> Unit,
    onAddMusicClick:() -> Unit
){

    var showMenu by remember { mutableStateOf(false) }
    var musicList by remember { mutableStateOf(emptyList<MusicDto>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // load music
    LaunchedEffect(playlistDto.playlistId){
        playlistDto.playlistId?.let { playlistId ->
            try {
                isLoading = true
                playlistViewModel.onEvent(PlaylistEvent.LoadPlaylistSong(playlistId))
            }catch (e: Exception){
                error = "Failed to load playlist music: ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }
    val state = playlistViewModel.state.value
    LaunchedEffect(state.playlistSongs){
        musicList = state.playlistSongs
        isLoading = false
    }
    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()) {
            error = state.error
            isLoading = false
        }
    }


    val currentMusic = musicViewModel.currentMusic.value
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = playlistDto.name,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }

                    DropDownMenus(
                        isShowMenu = showMenu,
                        onRemovePlaylistClick = {
                            playlistViewModel.onEvent(PlaylistEvent.DeletePlaylist(playlistDto.toPlaylist()))
                            onNavigateBack()
                        },
                        onAddMusicClick = onAddMusicClick,
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if (error.isNotBlank()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = error,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            if (musicList.isNotEmpty()){
                MusicList(
                    modifier = Modifier.fillMaxSize(),
                    musicList = musicList,
                    currentMusic = null,
                    onMusicSelected = { selectedMusic ->
                        val isPlaying = currentMusic?.id == selectedMusic.id
                        if (!isPlaying) {
                            musicViewModel.playMusic(selectedMusic, musicList)
                        }
                        onMusicSelected(selectedMusic)
                    }
                )
            }

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
                    onClick = onAddMusicClick
                ){
                    Text("Add", color = Color(0xFF800080))
                }
            }
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
            text = { Text("Add Music") },
            onClick = {
                onAddMusicClick()
                isShowMenu
            }
        )
        DropdownMenuItem(
            text = { Text("Remove") },
            onClick = {
                onRemovePlaylistClick()
                isShowMenu
            }
        )

    }
}