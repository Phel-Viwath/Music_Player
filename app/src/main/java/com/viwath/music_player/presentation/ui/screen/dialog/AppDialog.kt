package com.viwath.music_player.presentation.ui.screen.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AppDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmText: String,
    onDismissRequest: () -> Unit
) {
    if (showDialog){
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
                TextButton(onClick = onDismissRequest) {
                    Text(confirmText)
                }
            },
        )
    }
}


@Composable
fun AppDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit = {},
    onDismissRequest: () -> Unit
) {
    if (showDialog){
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
                TextButton(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(dismissText)
                }
            }
        )
    }
}