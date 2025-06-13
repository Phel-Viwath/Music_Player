package com.viwath.music_player.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.music_list.MusicListScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onMusicListLoaded: (List<Music>) -> Unit,
    onMusicSelected: (Music) -> Unit
){

    var selectedTab by remember { mutableIntStateOf(0) }

    Column {
        TabLayout(
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )

        when(selectedTab){
            0 -> MusicListScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black),
                viewModel = viewModel,
                onMusicListLoaded = { loadMusicList ->
                   onMusicListLoaded(loadMusicList)
                },
                onMusicSelected = { selectedMusic ->
                    onMusicSelected(selectedMusic)
                }
            )
            1 -> Text(text = "Album Screen")
            2 -> Text(text = "PlayList Screen")
        }

    }
}

@Composable
fun TabLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
){
    val tabItems = listOf(
        TabItem(title = "Music", icon = Icons.Default.MusicNote),
        TabItem(title = "Album", icon = Icons.Default.Album),
        TabItem(title = "PlayList", icon = Icons.AutoMirrored.Filled.List)
    )

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabItems.forEachIndexed { index, tabItem ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = tabItem.title) },
                icon = { Icon(imageVector = tabItem.icon, contentDescription = null) }
            )
        }
    }
}