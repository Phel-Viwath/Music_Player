package com.viwath.music_player.presentation.ui.screen.music_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viwath.music_player.R
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.state.FavoriteToggleState
import com.viwath.music_player.presentation.ui.screen.state.PlaybackState
import com.viwath.music_player.presentation.ui.screen.state.PlayingMode

@Composable
fun ControlContent(
    modifier: Modifier = Modifier,
    musicDto: MusicDto,
    favoriteToggleState: FavoriteToggleState,
    playbackState: PlaybackState,
    playingMode: PlayingMode,
    onFavoriteClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onMoreClick: () -> Unit
){
    val favoriteIcon = if (favoriteToggleState == FavoriteToggleState.FAVORITE)
        Icons.Default.Favorite
    else Icons.Default.FavoriteBorder

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {

                Column(modifier = Modifier.weight(0.85f)) {
                    Text(
                        text = playbackState.currentMusic?.title ?: musicDto.title,
                        color = Color.White,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }

                // Favorite
                IconButton(
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(start = 8.dp),
                    onClick = {
                        onFavoriteClick()
                    }
                ) {
                    Icon(
                        imageVector = favoriteIcon,
                        tint  = if (favoriteToggleState == FavoriteToggleState.FAVORITE) Color.Green else Color.White,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }

            // artist name
            Text(
                text = playbackState.currentMusic?.artist ?: musicDto.artist,
                color = Color.LightGray,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            )

            // Custom progress bar
            CustomProgressBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                currentPosition = playbackState.currentPosition,
                duration = playbackState.duration,
                onSeekTo = { position -> onSeekTo(position) },
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                height = 36.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // repeat
                IconButton(
                    onClick = onRepeatClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    val icon = when (playingMode) {
                        PlayingMode.REPEAT_ALL -> R.drawable.ic_repeat
                        PlayingMode.REPEAT_ONE -> R.drawable.ic_repeat_once
                        PlayingMode.SHUFFLE -> R.drawable.ic_shuffle
                    }
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp),
                    )
                }

                // previous
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_skip_previous_outline),
                        contentDescription = "previous",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp),
                    )
                }

                // play/pause
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = if (!playbackState.isPlaying)
                            painterResource(R.drawable.ic_pause)
                        else painterResource(R.drawable.ic_play),
                        contentDescription = if (playbackState.isPlaying) "pause" else "play",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp),
                    )
                }

                // next
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_skip_next_outline),
                        contentDescription = "next",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp),
                    )
                }

                // more
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }

            }
        }
    }
}