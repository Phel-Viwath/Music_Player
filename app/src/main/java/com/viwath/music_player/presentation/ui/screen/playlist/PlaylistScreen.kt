package com.viwath.music_player.presentation.ui.screen.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.dto.PlaylistDto
import com.viwath.music_player.presentation.ui.screen.component.Dialog
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.ui.screen.playlist.component.NewPlaylistDialogM3
import com.viwath.music_player.presentation.ui.screen.playlist.component.PlaylistItem
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel = hiltViewModel(),
    onNavigateToPlaylistMusic: (PlaylistDto) -> Unit = {}
){

    // properties
    val context = LocalContext.current
    val dialogTitle = "V-Music"
    val state = viewModel.state.value

    var showDialogNewPlaylist by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }

    // side-effect
    LaunchedEffect(state.error){
        if (state.error.isNotBlank())
           showMessageDialog = true
    }

    LaunchedEffect(state.success){
        if (state.success.isNotBlank())
            Toast.makeText(context, state.success, Toast.LENGTH_SHORT).show()
    }

    // Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ){
        Column {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFF6C63FF).copy(alpha = 0.2f),
                    contentColor = Color.White,
                ),
                onClick = { showDialogNewPlaylist = true },
            ){
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add new play",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Playlist",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            ///

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(state.playlists, key = { "${it.playlistId}" }){ playlistItem ->
                    PlaylistItem(playlistItem){
                        // navigate to playlist music
                        onNavigateToPlaylistMusic(playlistItem)
                    }
                }
            }
        } // end column

        NewPlaylistDialogM3 (
            isVisible = showDialogNewPlaylist,
            onDismiss = { showDialogNewPlaylist = false },
            onCreatePlaylist = { playlistTitle ->
                val playlist = Playlist(
                    playlistId = null,
                    name = playlistTitle,
                    thumbnail = null
                )
                viewModel.onEvent(PlaylistEvent.NewPlaylist(playlist))
                viewModel.onEvent(PlaylistEvent.OnCreatePlaylist)
            }
        )
    }


    // show dialog message
    if (showMessageDialog){
        Dialog(
            dialogTitle,
            state.error,
            onDismissRequest = {showMessageDialog = false}
        )
    }

}
