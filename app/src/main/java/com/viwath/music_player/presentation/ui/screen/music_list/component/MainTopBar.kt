package com.viwath.music_player.presentation.ui.screen.music_list.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viwath.music_player.R
import com.viwath.music_player.core.common.SortOrder
import com.viwath.music_player.presentation.ui.screen.music_detail.component.DropDownMenu3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    isShowSortMenu: Boolean = false,
    selectedOption: SortOrder,
    onSearchIconClick: () -> Unit,
    currentOrderOption: (SortOrder) -> Unit
){

    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Icon(
                painter = painterResource(R.drawable.v_music),
                contentDescription = "Android Icon",
                tint = Color.White
            ) // end of icon
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            // order
            if (isShowSortMenu){
                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Android Icon",
                            tint = Color.White
                        )
                    }
                    DropDownMenu3(
                        modifier = Modifier,
                        expanded = expanded,
                        options = SortOrder.entries,
                        optionLabel = { it.displayName() },
                        selectedOption = currentOrderOption,
                        onDismissRequest = { expanded = false },
                        currentOption = selectedOption,
                        selectedColor = Color.Green
                    )
                }
            }

            // search
            IconButton(
                onClick = onSearchIconClick
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

