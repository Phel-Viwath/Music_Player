package com.viwath.music_player.presentation.ui.screen.search_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    musicViewModel: MusicViewModel = hiltViewModel(),
    onTab: () -> Unit,
    selectedMusic: (MusicDto) -> Unit
){

    val focusManager = LocalFocusManager.current
    val searchState = musicViewModel.searchState.value
    val state = musicViewModel.state.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Transparent if empty → so HomeScreen is visible behind
            .background(
                if (searchState.searchText.isEmpty()) Color.Black.copy(alpha = 0.3f)
                else Color.Black
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar3(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        Color(0xFF1A0B3D),
                        shape = RoundedCornerShape(16.dp)
                    ),
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

            when{
                searchState.musicList.isNotEmpty() -> {
                    SearchList(
                        modifier = Modifier
                            .background(
                                if (searchState.searchText.isEmpty())
                                    Color.Black.copy(alpha = 0.3f)
                                else Color.Black
                            ),
                        query = searchState.searchText,
                        musicList = searchState.musicList,
                        onMusicSelected = {
                            musicViewModel.onEvent(MusicEvent.OnPlay(it, state.musicFiles))
                            selectedMusic(it)
                            onTab()
                            navController.popBackStack()
                        }
                    )
                }
                searchState.searchText.isNotEmpty() && searchState.musicList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

}

