package com.viwath.music_player.presentation.ui.screen.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
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
    isVisible: Boolean,
    musicDto: MusicDto,
    onDismiss: () -> Unit,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {

    val configuration = LocalWindowInfo.current
    val screenHeight = configuration.containerSize.height

    val bottomSheetState = rememberModalBottomSheetState (
        skipPartiallyExpanded = true
    )

    if (isVisible){
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = bottomSheetState,
            dragHandle = {
                // Custom small drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp), // total space for handle area
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(3.dp) // <- make handle thinner
                            .width(32.dp) // <- make handle shorter
                            .background(
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((screenHeight/5).dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                IconButton(
                    onClick = onDismiss
                ){
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
                            playlistViewModel.onEvent(PlaylistEvent.AddPlaylistSong(listOf(musicDto), playlistDto.playlistId))
                            onDismiss()
                        }

                    }
                )
            }

        }
    }

}