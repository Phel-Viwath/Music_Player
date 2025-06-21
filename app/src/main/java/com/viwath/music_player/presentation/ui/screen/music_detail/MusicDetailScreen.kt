package com.viwath.music_player.presentation.ui.screen.music_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.domain.model.MusicDto
import com.viwath.music_player.presentation.ui.screen.event.FavorEvent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ControlContent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ImageContent
import com.viwath.music_player.presentation.ui.screen.state.FavoriteToggleState
import com.viwath.music_player.presentation.viewmodel.FavorMusicViewModel
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@Composable
fun MusicDetailScreen(
    modifier: Modifier = Modifier,
    music: MusicDto,
    viewModel: MusicViewModel = hiltViewModel(),
    favorViewModel: FavorMusicViewModel = hiltViewModel()
){

    val playbackState by viewModel.playbackState.collectAsState()
    var currentMusic = playbackState.currentMusic ?: music
    var isFavorite by remember(music.id) { mutableStateOf(music.isFavorite) }

    LaunchedEffect(music) {
        currentMusic = music
    }

    // Update favorite status whenever music.id changes OR when the screen becomes visible
    LaunchedEffect(music.id, music.isFavorite) {
        isFavorite = music.isFavorite
    }


    Box(modifier = modifier){
        currentMusic.imagePath?.let { path ->
            Image(
                painter = rememberAsyncImagePainter(path),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
                    .blur(radius = 100.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ImageContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                music = currentMusic
            )

            ControlContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                viewModel = viewModel,
                musicDto = currentMusic,
                favoriteToggleState = if (isFavorite) FavoriteToggleState.FAVORITE else FavoriteToggleState.NOT_FAVORITE,
                onFavoriteClick = {
                    isFavorite = !isFavorite
                    if (isFavorite) {
                        favorViewModel.onEvent(FavorEvent.PasteInsertData(currentMusic))
                        favorViewModel.onEvent(FavorEvent.InsertFavorite)
                    }else{
                        favorViewModel.onEvent(FavorEvent.PasteDeleteData(currentMusic))
                        favorViewModel.onEvent(FavorEvent.DeleteFavorite)
                    }
                }
            )

        }
    }

}
