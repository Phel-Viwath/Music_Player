package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.viwath.music_player.domain.model.dto.MusicDto

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    musicList: List<MusicDto>,
    onMusicSelected: (MusicDto) -> Unit
){

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        items(musicList.size){ index ->
            SearchListItem(musicList[index]){ selectedMusic -> onMusicSelected(selectedMusic) }
        }
    }

}

@Composable
fun SearchListItem(
    musicDto: MusicDto,
    onMusicSelected: (MusicDto) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onMusicSelected(musicDto) }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Text(
                text = musicDto.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = musicDto.artist,
                style = MaterialTheme.typography.bodyMedium ,
                color = Color.Gray,
                maxLines = 1
            )

        }

    }
}