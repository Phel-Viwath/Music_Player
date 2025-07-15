package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun Dialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit
){
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {onDismissRequest()}) {
                Text("Ok")
            }
        }
    )
}