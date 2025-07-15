package com.viwath.music_player.presentation.ui.screen.playlist.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viwath.music_player.domain.model.dto.PlaylistDto

@Composable
fun PlaylistItem(
    playlistItem: PlaylistDto,
    onItemClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Image(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Playlist Icon",
            modifier = Modifier.size(64.dp).background(Color.White)
        )

        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
            Text(
                text = playlistItem.name,
                fontSize = 18.sp
            )
            Text(
                text = "${playlistItem.totalSong}",
                fontSize = 12.sp
            )
        }

        Image(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(24.dp)
        )
    }
}