package com.viwath.music_player.presentation.ui.screen.playlist.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viwath.music_player.domain.model.dto.PlaylistDto

@Composable
fun PlaylistItem(
    playlistItem: PlaylistDto,
    onItemClick: () -> Unit
){
    Log.d("PlaylistItem thumbnail", "PlaylistItem: ${playlistItem.thumbnail}")
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        MusicFolderIcon(
            imageUri = playlistItem.thumbnail
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

@Composable
fun MusicFolderIcon(
    imageUri: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
) {
    Box(modifier = modifier.size(size).background(Color.Transparent)){
        Box(
            modifier = modifier.size(size)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(Color.Gray.copy(alpha = 8f), shape = RoundedCornerShape(8.dp))
        )
        Box(
            modifier = modifier.size(size - 2.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        )
        Box(
            modifier = modifier.size(size - 4.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        ){
            if (!imageUri.isNullOrBlank())
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Music Folder Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            else
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Music Folder Icon",
                    tint = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
        }
    }

}

