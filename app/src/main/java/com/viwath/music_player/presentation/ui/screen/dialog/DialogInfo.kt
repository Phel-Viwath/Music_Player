package com.viwath.music_player.presentation.ui.screen.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viwath.music_player.core.util.dateFormatter
import com.viwath.music_player.domain.model.dto.MusicDto

@Composable
fun DialogInfo(
    isVisible: Boolean,
    musicDto: MusicDto,
    onDismiss: () -> Unit
) {
    if (isVisible){
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            title = {
                Text(text = "Info")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    InfoItem(label = "Name", value = musicDto.title)
                    InfoItem(label = "Artist", value = musicDto.artist)
                    InfoItem(label = "Album", value = musicDto.album)
                    InfoItem(label = "Duration", value = "${musicDto.duration / 1000 / 60}:${musicDto.duration / 1000 % 60}")
                    InfoItem(label = "Add date", value = dateFormatter(musicDto.addDate))
                    InfoItem(label = "Path", value = musicDto.uri.substringBeforeLast("/"), isLast = true)
                }
            }
        )
    }
}

@Composable
fun InfoItem(label: String, value: String, isLast: Boolean = false){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 16.dp),
        verticalAlignment = Alignment.Top
    ){
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.widthIn(min = 50.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
            maxLines = if (label == "Path" || label == "Name") 2 else 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}