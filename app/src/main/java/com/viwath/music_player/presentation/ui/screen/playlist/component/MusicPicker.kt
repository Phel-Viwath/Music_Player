package com.viwath.music_player.presentation.ui.screen.playlist.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.viwath.music_player.R
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPicker(
    modifier: Modifier = Modifier,
    musicList: List<MusicDto>,
    onMusicSelected: (List<MusicDto>) -> Unit,
    onNavigateBack: () -> Unit
){

    var selectedMusicIds by remember { mutableStateOf(setOf<String>()) }
    var isCheckAll by remember { mutableStateOf(false) }

    val selectedMusicList = musicList.filter { it.id.toString() in selectedMusicIds }
    val checkCount = selectedMusicList.size

    LaunchedEffect(checkCount, musicList.size){
        isCheckAll = checkCount == musicList.size && musicList.isNotEmpty()
    }

    LaunchedEffect(selectedMusicList){
        onMusicSelected(selectedMusicList)
    }

    Scaffold(
        modifier = modifier.background(Color.Transparent),
        topBar = {
            TopAppBar (
                title = {
                    Text(
                        text = if (checkCount == 0) "None Selected" else "$checkCount Item Selected",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Checkbox(
                        checked = isCheckAll,
                        onCheckedChange = { checked ->
                            selectedMusicIds = if (checked)
                                musicList.map { it.id.toString() }.toSet()
                            else
                                emptySet()
                        }
                    )
                }
            ) // end of top bar
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        /// save to playlist logic with viewModel
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (checkCount == 0) Color.Gray else Color(0xFF800080)
                    )
                ){
                    Text(
                        text = "Done",
                        color = Color.White
                    )
                }
            }
        }// end of bottom bar
    ){ innerPadding ->
        AmbientGradientBackground(modifier = Modifier.fillMaxSize()) // for background gradient
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.Transparent)
        ){
            items(
                items = musicList,
                key = { it.id }
            ){ musicDto ->
                val isChecked = selectedMusicIds.contains(musicDto.id.toString())
                MusicPickerItem(
                    musicDto = musicDto,
                    isCheck = isChecked,
                    onCheckChange = { isCheckedNow ->
                        selectedMusicIds = if (isCheckedNow){
                            selectedMusicIds + musicDto.id.toString()
                        }else{
                            selectedMusicIds - musicDto.id.toString()
                        }
                    }
                )
            }
        }// end of lazy column
    }
}


// create music picker item for putting in lazy column
@Composable
fun MusicPickerItem(
    musicDto: MusicDto,
    isCheck: Boolean,
    onCheckChange: (Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .clickable{ onCheckChange(!isCheck) }
            .padding(start = 14.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            painter = rememberAsyncImagePainter(musicDto.imagePath ?: R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = musicDto.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = musicDto.artist,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
        }
        Checkbox(
            checked = isCheck,
            onCheckedChange = onCheckChange
        )
    }
}