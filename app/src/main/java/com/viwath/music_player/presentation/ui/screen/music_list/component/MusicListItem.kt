package com.viwath.music_player.presentation.ui.screen.music_list.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.R
import com.viwath.music_player.domain.model.dto.MusicDto

@Composable
fun MusicListItem(
    music: MusicDto,
    onItemClick: (MusicDto) -> Unit,
    onItemMenuClick: (MusicDto) -> Unit,
    isPlaying: Boolean = false,
    isPaused: Boolean = false
){
    Log.d("MusicListItem", "MusicListItem image path is: ${music.imagePath}")
    val textColor = if (isPlaying) Color.Green else Color.White
    Box(modifier = Modifier.background(Color.Transparent)){
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .clickable{ onItemClick(music) }
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Box {
                Image(
                    painter = rememberAsyncImagePainter(music.imagePath ?: R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                if (isPlaying)
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.Green, shape = CircleShape)
                            .size(12.dp)
                            .padding(2.dp)
                    )
                if (isPaused)
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.Green, shape = CircleShape)
                            .size(12.dp)
                            .padding(2.dp)
                    )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = music.title,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = music.artist,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }

            IconButton(
                modifier = Modifier
                    .wrapContentSize(),
                onClick = { onItemMenuClick(music) },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }
}