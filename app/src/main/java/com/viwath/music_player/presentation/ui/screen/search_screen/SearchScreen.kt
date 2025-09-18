package com.viwath.music_player.presentation.ui.screen.search_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.viwath.music_player.presentation.ui.screen.component.SearchBar3
import com.viwath.music_player.presentation.ui.screen.component.SearchList
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    musicViewModel: MusicViewModel = hiltViewModel()
){

    val focusManager = LocalFocusManager.current
    val state = musicViewModel.searchState.value

    Box(
        modifier = Modifier.fillMaxWidth()
            .background(if (state.searchText.isEmpty()) Color.Black.copy(alpha = 0.3f) else Color.Black)
            .padding(8.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar3(
                modifier = Modifier.fillMaxWidth(),
                onSearch = { query ->
                    // Handle search query
                    musicViewModel.onEvent(MusicEvent.SearchTextChange(query))
                },
                onClose = {
                    focusManager.clearFocus()
                    navController.popBackStack()
                }
            ) // end of search bar

            Spacer(modifier = Modifier.height(8.dp))

            SearchList(
                modifier = Modifier
                    .background(
                        color = if (state.searchText.isEmpty() || state.searchText.isBlank())
                            Color.Transparent
                        else MaterialTheme.colorScheme.background
                    ),
                musicList = state.musicList,
                onMusicSelected = {
                    musicViewModel.onEvent(MusicEvent.OnPlay(it))
                }
            )
        }
    }

}

