package com.viwath.music_player.presentation.ui.screen.music_list

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.MusicList
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@Composable
fun MusicListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = hiltViewModel(),
    onMusicListLoaded: (List<MusicDto>) -> Unit = {},
    onMusicSelected: (MusicDto) -> Unit = {}
){
    val state = viewModel.state.value
    val showDialog = remember { mutableStateOf(false) }
    val currentMusic = viewModel.currentMusic.value

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank())
            showDialog.value = true
    }

    // Notify parent about loaded music list
    LaunchedEffect(state.musicFiles) {
        if (state.musicFiles.isNotEmpty()) {
            onMusicListLoaded(state.musicFiles)
        }
    }

    Log.d("MusicListScreen", "MusicListScreen: ${state.musicFiles}")
    Box(
        modifier = modifier
    ){
        if (state.musicFiles.isNotEmpty()){
            MusicList(
                modifier = Modifier,
                musicList = state.musicFiles,
                currentMusic = currentMusic,
                onMusicSelected = { selectedMusic ->
                    val isPlaying = currentMusic?.id == selectedMusic.id
                    if (!isPlaying) {
                        viewModel.playMusic(selectedMusic, state.musicFiles)
                    }
                    onMusicSelected(selectedMusic)
                }
            )
        }

        if (showDialog.value){
            ShowDialog(
                title = "Error",
                message = state.error
            ) {
                showDialog.value = false
            }
        }
        if(state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

}

@Composable
fun ShowDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit
){
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {onDismissRequest()}) {
                Text("Ok")
            }
        }
    )
}