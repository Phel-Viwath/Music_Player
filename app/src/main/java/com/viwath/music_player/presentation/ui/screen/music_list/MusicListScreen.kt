package com.viwath.music_player.presentation.ui.screen.music_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.MusicDto
import com.viwath.music_player.presentation.ui.screen.music_list.component.MusicListItem
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
    val currentMusic = viewModel.currentMusic

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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
        ) {
            items(state.musicFiles, key = {it.id}) { music ->
                val isPlaying = currentMusic.value?.id == music.id
                MusicListItem(
                    music = music,
                    onItemClick = { selectedMusic ->
                        onMusicSelected(selectedMusic)
                        viewModel.playMusic(selectedMusic, state.musicFiles)
                    },
                    onItemMenuClick = {

                    },
                    isPlaying = isPlaying
                )
            }
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