package com.viwath.music_player.presentation.ui.screen.music_detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.R
import com.viwath.music_player.domain.model.MusicDto

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    music: MusicDto
){

    Box(
        modifier = modifier
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = rememberAsyncImagePainter(music.imagePath ?: R.drawable.ic_launcher_foreground),
            contentDescription = "${music.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    BorderStroke(1.dp, Color.Transparent),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}