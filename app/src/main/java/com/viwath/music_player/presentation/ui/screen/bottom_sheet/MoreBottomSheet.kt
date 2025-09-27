package com.viwath.music_player.presentation.ui.screen.bottom_sheet

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.dialog.DialogInfo
import com.viwath.music_player.presentation.ui.screen.event.FavorEvent
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.viewmodel.FavoriteViewModel
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ShowBottomSheetMenu(
    isVisible: Boolean,
    musicDto: MusicDto,
    musicViewModel: MusicViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    coroutinesScope: CoroutineScope = rememberCoroutineScope(),
    onDismiss: () -> Unit
){

    val context = LocalContext.current

    val musicState = musicViewModel.state.value
    val message = musicViewModel.message
    val favoriteMessage = favoriteViewModel.message
    val favorState = favoriteViewModel.state.value
    val isFavorite = favorState.isFavorite

    var showPlaylist by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible, musicDto.id){
        favoriteViewModel.onEvent(FavorEvent.CheckFavorite(musicDto.id))
    }

    LaunchedEffect(Unit){
        message.collect { value ->
            Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit){
        favoriteMessage.collect { value ->
            Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
        }
    }

    MoreBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        musicDto = musicDto,
        menuItems = listOf(
            BottomSheetMenuItem(
                id = "favorite",
                title = "Favorite",
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                iconTint = if (isFavorite) Color.Green else Color.White,
                action = {
                    coroutinesScope.launch {
                        if (!isFavorite) {
                            favoriteViewModel.onEvent(FavorEvent.PasteInsertData(musicDto))
                            delay(100)
                            favoriteViewModel.onEvent(FavorEvent.InsertFavorite)
                        }
                        else{
                            favoriteViewModel.onEvent(FavorEvent.PasteDeleteData(musicDto))
                            delay(100)
                            favoriteViewModel.onEvent(FavorEvent.DeleteFavorite)
                        }
                        delay(100)
                        favoriteViewModel.onEvent(FavorEvent.CheckFavorite(musicDto.id))
                    }
                }
            ),
            BottomSheetMenuItem(
                id = "play_next",
                title = "Play Next",
                icon = Icons.Default.ArrowDownward,
                action = {
                    musicViewModel.onEvent(MusicEvent.AddToPlayNext(musicDto, musicState.musicFiles))
                    onDismiss()
                }
            ),
            BottomSheetMenuItem(
                id = "play_last",
                title = "Play Last",
                icon = Icons.Default.Replay,
                action = {
                    musicViewModel.onEvent(MusicEvent.AddToPlayLast(musicDto))
                    onDismiss()
                }
            ),
            BottomSheetMenuItem(
                id = "share_song",
                title = "Share",
                icon = Icons.Default.Share,
                action = {
                    // Handle share action
                }
            ),
            BottomSheetMenuItem(
                id = "add_to",
                title = "Add to",
                icon = Icons.Default.Add,
                action = {
                    // Handle add to action
                    showPlaylist = true
                }
            ),
            BottomSheetMenuItem(
                id = "info",
                title = "Info",
                icon = Icons.Default.Info,
                action = {
                    // Handle info action
                    showInfo = true
                }
            ),
            BottomSheetMenuItem(
                id = "delete_song",
                title = "Delete",
                icon = Icons.Default.Delete,
                action = {
                    // Handle delete action
                    musicViewModel.onEvent(MusicEvent.DeleteMusic(musicDto))
                    onDismiss()
                }
            )
        )
    )

    BottomSheetPlaylist(
        isVisible = showPlaylist,
        musicDto = musicDto,
        onDismiss = { showPlaylist = false },
    )

    DialogInfo(
        isVisible = showInfo,
        musicDto = musicDto,
        onDismiss = { showInfo = false }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    musicDto: MusicDto,
    menuItems: List<BottomSheetMenuItem>,
    modifier: Modifier = Modifier
){

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(key1 = isVisible){
        if (isVisible)
            bottomSheetState.show()
        else
            bottomSheetState.hide()
    }

    if (isVisible){
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = bottomSheetState,
            modifier = modifier
        ){
            BottomSheetContent(
                trackTitle = musicDto.title,
                menuItems = menuItems
            )
        }
    }



}

@Composable
fun BottomSheetContent(
    trackTitle: String,
    menuItems: List<BottomSheetMenuItem>
){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        item {
            Text(
                text = trackTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(menuItems){ menuItem ->
            BottomSheetMoreMenuItem(
                menuItem = menuItem,
                onClick = {
                    menuItem.action()
                }
            )
        }
    }

}

@Composable
fun BottomSheetMoreMenuItem(
    menuItem: BottomSheetMenuItem,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ){
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                imageVector = menuItem.icon,
                contentDescription = menuItem.title,
                tint = if (menuItem.enable) menuItem.iconTint else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = menuItem.title,
                fontSize = 16.sp,
                color = if (menuItem.enable) Color.White else Color.Gray,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
