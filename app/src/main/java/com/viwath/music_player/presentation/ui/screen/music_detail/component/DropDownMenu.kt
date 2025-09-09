package com.viwath.music_player.presentation.ui.screen.music_detail.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> DropDownMenu3(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    options: List<T>,
    optionLabel: (T) -> String,
    selectedOption: (T) -> Unit,
    onDismissRequest: () -> Unit,
    currentOption: T? = null,
    selectedColor: Color = Color.Blue,
    unselectedColor: Color = Color.White
){
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ){
        options.forEach { option ->
            val isSelected = option == currentOption
            DropdownMenuItem(
                text = {
                    Text(
                        text = optionLabel(option),
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                },
                trailingIcon = {
                    if (isSelected)
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "menu item icon",
                            tint = selectedColor
                        )
                    else null
                },
                onClick = {
                    selectedOption(option)
                    onDismissRequest()
                }

            )
        }
    }
}