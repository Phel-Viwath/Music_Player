package com.viwath.music_player.presentation.ui.screen.music_list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel,
    onTap: () -> Unit = {}
){

    val playbackState = musicViewModel.playbackState.collectAsState().value
    val isPlaying = playbackState.isPlaying
    val currentMusic = playbackState.currentMusic

    if (currentMusic != null){
        MiniPlayerCard(
            modifier = modifier.height(70.dp),
            isPlaying = isPlaying,
            currentMusic = currentMusic,
            onTap = onTap,
            onResumeClick = { musicViewModel.resumeMusic() },
            onPauseClick = { musicViewModel.pauseMusic() },
            onPlayNextClick = { musicViewModel.nextMusic() },
            duration = playbackState.duration,
            currentPosition = playbackState.currentPosition
        )
    }

}

@Composable
fun MiniPlayerCard(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentMusic: MusicDto,
    onTap: () -> Unit = {},
    onResumeClick: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayNextClick: () -> Unit,
    duration: Long,
    currentPosition: Long
){
    Card(
        modifier = modifier.fillMaxWidth().clickable { onTap() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // Semi-transparent for floating effect
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ){
                    currentMusic.imagePath?.let { path ->
                        Image(
                            painter = rememberAsyncImagePainter(path),
                            contentDescription = "Album Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentMusic.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentMusic.artist,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = {
                        if (isPlaying) {
                            onPauseClick()
                        } else {
                            onResumeClick()
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = onPlayNextClick){
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Play next",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

            }

            LinearProgressIndicator(
                progress = {
                    if (duration > 0) {
                        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Green,
                trackColor = Color.LightGray
            )
        }

    }
}