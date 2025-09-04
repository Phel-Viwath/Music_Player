package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.music_list.component.MusicListItem

@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    musicList: List<MusicDto>,
    currentMusic: MusicDto?,
    isPaused: Boolean,
    onMusicSelected: (MusicDto) -> Unit
) {
    Box(
        modifier = modifier
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
        ) {
            items(
                items = musicList,
                key = {music -> music.id }
            ) { music ->
                val isPlaying = currentMusic?.id == music.id
                val isPause = if (currentMusic?.id == music.id) isPaused  else false
                MusicListItem(
                    music = music,
                    onItemClick = { selectedMusic ->
                        onMusicSelected(selectedMusic)
                    },
                    onItemMenuClick = {
                        //
                    },
                    isPlaying = isPlaying,
                    isPaused = isPause
                )
            }
        }
    }
}