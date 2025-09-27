package com.viwath.music_player.presentation.ui.screen.bottom_sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.event.FavorEvent
import com.viwath.music_player.presentation.ui.screen.event.PlaylistEvent
import com.viwath.music_player.presentation.ui.screen.playlist.PlaylistScreen
import com.viwath.music_player.presentation.viewmodel.FavoriteViewModel
import com.viwath.music_player.presentation.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPlaylist(
    musicDto: MusicDto,
    onDismiss: () -> Unit,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState (
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        shape = RectangleShape
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            IconButton(onClick = onDismiss){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            PlaylistScreen(
                modifier = Modifier.fillMaxWidth(),
                onItemClick = { playlistDto ->
                    if (playlistDto.playlistId == 0L){
                        favoriteViewModel.onEvent(FavorEvent.PasteInsertData(musicDto))
                        favoriteViewModel.onEvent(FavorEvent.InsertFavorite)
                        onDismiss()
                    }else{
                        playlistViewModel.onEvent(PlaylistEvent.AddPlaylistSong(listOf(musicDto)))
                        onDismiss()
                    }

                }
            )
        }

    }

}