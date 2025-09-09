package com.viwath.music_player.presentation.ui.screen.music_detail

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.event.FavorEvent
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ControlContent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ImageContent
import com.viwath.music_player.presentation.ui.screen.state.FavoriteToggleState
import com.viwath.music_player.presentation.ui.screen.state.PlayingMode
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
    var isCurrentFavorite by remember { mutableStateOf(false) }
    val currentMusicFavorite = favorViewModel.isFavorite(currentMusic.id.toString())


    LaunchedEffect(music) {
        currentMusic = music
    }

    // Update favorite status whenever music.id changes OR when the screen becomes visible
    LaunchedEffect(music.id, music.isFavorite) {
        isFavorite = music.isFavorite
    }

    LaunchedEffect(favorViewModel) {
        isCurrentFavorite = currentMusicFavorite
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
                musicDto = currentMusic,
                favoriteToggleState = if (isCurrentFavorite) {
                    FavoriteToggleState.FAVORITE
                } else {
                    if (isFavorite) FavoriteToggleState.FAVORITE
                    else FavoriteToggleState.NOT_FAVORITE
                },
                playbackState = playbackState,
                playingMode = playbackState.playingMode,
                onFavoriteClick = {
                    isFavorite = !isFavorite
                    if (isFavorite) {
                        favorViewModel.onEvent(FavorEvent.AddCurrentFavorite(currentMusic.id.toString()))
                        favorViewModel.onEvent(FavorEvent.PasteInsertData(currentMusic))
                        favorViewModel.onEvent(FavorEvent.InsertFavorite)
                    }else{
                        favorViewModel.onEvent(FavorEvent.RemoveCurrentFavorite(currentMusic.id.toString()))
                        favorViewModel.onEvent(FavorEvent.PasteDeleteData(currentMusic))
                        favorViewModel.onEvent(FavorEvent.DeleteFavorite)
                    }
                },
                onSeekTo = { position -> viewModel.onEvent(MusicEvent.OnSeekTo(position)) },
                onPreviousClick = { viewModel.onEvent(MusicEvent.OnPlayPrevious) },
                onNextClick = { viewModel.onEvent(MusicEvent.OnPlayNext) },
                onRepeatClick = {
                    when(playbackState.playingMode){
                        PlayingMode.REPEAT_ALL -> {
                            viewModel.onEvent(MusicEvent.OnRepeatOne)
                            PlayingMode.REPEAT_ONE
                        }
                        PlayingMode.REPEAT_ONE -> {
                            viewModel.onEvent(MusicEvent.ShuffleMode(true))
                            PlayingMode.SHUFFLE
                        }
                        PlayingMode.SHUFFLE -> {
                            viewModel.onEvent(MusicEvent.ShuffleMode(false))
                            viewModel.onEvent(MusicEvent.OnRepeatAll)
                            PlayingMode.REPEAT_ALL
                        }
                    }
                },
                onPlayPauseClick = {
                    if (!playbackState.isPlaying)
                        viewModel.onEvent(MusicEvent.OnPlay(currentMusic))
                    else viewModel.onEvent(MusicEvent.OnPause)
                }
            )

        }
    }

}
