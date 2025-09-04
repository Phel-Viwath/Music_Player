package com.viwath.music_player.presentation.ui.screen.music_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viwath.music_player.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(){
    TopAppBar(
        title = {
            Icon(
                painter = painterResource(R.drawable.v_music),
                contentDescription = "Android Icon",
                tint = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            IconButton(
                onClick = {},
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Android Icon",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = {}
            ){
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White
                )
            }
        },
    )
}