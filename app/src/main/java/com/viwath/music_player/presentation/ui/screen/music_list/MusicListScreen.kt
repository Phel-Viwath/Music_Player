package com.viwath.music_player.presentation.ui.screen.music_list

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.ui.screen.dialog.AppDialog
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@Composable
fun MusicListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = hiltViewModel(),
    onMusicSelected: (MusicDto) -> Unit = {},
    onMenuClick: (MusicDto) -> Unit
){
    val state = viewModel.state.value
    val showDialog = remember { mutableStateOf(false) }
    val currentMusic = viewModel.playbackState.collectAsState().value.currentMusic
    val isPaused = viewModel.playbackState.collectAsState().value.isPaused

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()) showDialog.value = true
    }

    Box(
        modifier = modifier
    ){
        if (state.musicFiles.isNotEmpty()){
            MusicList(
                modifier = Modifier,
                musicList = state.musicFiles,
                currentMusic = currentMusic,
                isPaused = isPaused,
                onMusicSelected = { selectedMusic ->
                    val isPlaying = currentMusic?.id == selectedMusic.id
                    if (!isPlaying) {
                        viewModel.onEvent(MusicEvent.OnPlay(selectedMusic, state.musicFiles))
                    }
                    onMusicSelected(selectedMusic)
                },
                onMenuClick = onMenuClick
            )
        }

        AppDialog(
            showDialog = showDialog.value,
            title = null,
            message = state.error,
            confirmText = "OK",
            onDismissRequest = { showDialog.value = false }
        )

        if(state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

}
