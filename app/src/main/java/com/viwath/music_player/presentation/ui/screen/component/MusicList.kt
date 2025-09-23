package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.bottom_sheet.BottomSheetMenuItem
import com.viwath.music_player.presentation.ui.screen.bottom_sheet.MoreBottomSheet
import com.viwath.music_player.presentation.ui.screen.bottom_sheet.ShowBottomSheetMenu
import com.viwath.music_player.presentation.ui.screen.music_list.component.MusicListItem

@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    musicList: List<MusicDto>,
    currentMusic: MusicDto?,
    isPaused: Boolean,
    onMusicSelected: (MusicDto) -> Unit
) {

    var selectedMusicForMenu by remember { mutableStateOf<MusicDto?>(null) }

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
                    onItemMenuClick = { music ->
                        selectedMusicForMenu = music
                    },
                    isPlaying = isPlaying,
                    isPaused = isPause
                )
            }
        }
    }

   selectedMusicForMenu?.let { musicDto ->
       ShowBottomSheetMenu(
           isVisible = true,
           musicDto = musicDto,
       ) {
           selectedMusicForMenu = null
       }
   }


}