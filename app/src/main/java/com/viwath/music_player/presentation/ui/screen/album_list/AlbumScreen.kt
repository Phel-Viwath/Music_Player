package com.viwath.music_player.presentation.ui.screen.album_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viwath.music_player.domain.model.Album
import com.viwath.music_player.presentation.ui.screen.Routes
import com.viwath.music_player.presentation.ui.screen.component.Dialog
import com.viwath.music_player.presentation.ui.screen.event.AlbumScreenEvent
import com.viwath.music_player.presentation.viewmodel.AlbumViewModel


@Composable
fun AlbumScreen(
    modifier: Modifier = Modifier,
    albumViewModel: AlbumViewModel = hiltViewModel(),
    navController: NavController,
){

    // properties
    val gridCells = GridCells.Fixed(2)
    val state = albumViewModel.state.value

    var showMessageDialog by remember { mutableStateOf(false) }

    // side effect
    LaunchedEffect(state.error){
        if (state.error.isNotBlank()){
            showMessageDialog = true
        }
    }

    state.albums.forEach {
        Log.d("AlbumScreen", "all albums: $it")
    }

    // Layout
    LazyVerticalGrid(
        columns = gridCells,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(6.dp)
    ){
        items(
            state.albums.size,
            key = {index -> state.albums[index].albumId}
        ){ index ->
            AlbumItem(state.albums[index]){ albumId ->
                Log.d("AlbumScreen", "Album Id is: $albumId")
                navController.navigate(Routes.AlbumDetailScreen.route + "/${albumId}"){
                    launchSingleTop = true
                }
            }
        }
    }// end grid

    if (showMessageDialog){
        Dialog(
            "V-Music",
            state.error,
            onDismissRequest = {showMessageDialog = false}
        )
    }
}

@Composable
fun AlbumItem(
    album: Album,
    onItemClick: (Long) -> Unit
){
    Column(
        modifier = Modifier.padding(8.dp)
            .clickable { onItemClick(album.albumId) }
    ){
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(shape = RoundedCornerShape(8.dp))
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                    .align(Alignment.BottomCenter)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.98f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    .align(Alignment.BottomCenter)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.96f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .align(Alignment.BottomCenter)
            ){
                // image
                if (album.albumArtUri != null){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(album.albumArtUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Music Folder Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }else{
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = "Music Folder Icon",
                        tint = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }// end box

        // tittle
        Text(
            text = album.albumName,
            maxLines = 1,
            fontSize = 18.sp
        )
        // total song
        Text(
            text = if (album.songCount == 1) "${album.songCount} Song" else "${album.songCount} Songs",
            maxLines = 1,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}