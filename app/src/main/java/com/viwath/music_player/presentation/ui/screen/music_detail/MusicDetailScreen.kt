package com.viwath.music_player.presentation.ui.screen.music_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ControlContent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.ImageContent
import com.viwath.music_player.presentation.ui.screen.music_detail.component.VisualizerScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.VisualizerViewModel

@Composable
fun MusicDetailScreen(
    modifier: Modifier = Modifier,
    music: Music,
    viewModel: MusicViewModel = hiltViewModel(),
    visualizerViewModel: VisualizerViewModel = hiltViewModel()
){

    val playbackState by viewModel.playbackState.collectAsState()
    var currentMusic = playbackState.currentMusic ?: music

    LaunchedEffect(music) {
        currentMusic = music
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
            VisualizerScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .weight(1f),
                viewModel = visualizerViewModel
            )
//            ImageContent(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                music = currentMusic
//            )

            ControlContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                viewModel = viewModel,
                music = currentMusic
            ){

            }

        }
    }

}
