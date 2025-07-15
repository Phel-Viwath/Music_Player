package com.viwath.music_player.presentation.ui.screen.playlist.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Alternative Material 3 implementation
@Composable
fun NewPlaylistDialogM3(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreatePlaylist: (String) -> Unit
) {
    var playlistTitle by remember { mutableStateOf("") }
    val maxCharacters = 61

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF2C2C2E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Text(
                    text = "New Playlist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = playlistTitle,
                        onValueChange = { newValue ->
                            if (newValue.length <= maxCharacters) {
                                playlistTitle = newValue
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Enter title",
                                color = Color(0xFF8E8E93)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF3A3A3C),
                            unfocusedContainerColor = Color(0xFF3A3A3C),
                            focusedBorderColor = Color(0xFF6B46C1),
                            unfocusedBorderColor = Color(0xFF48484A),
                            cursorColor = Color.White,
                            focusedPlaceholderColor = Color(0xFF8E8E93),
                            unfocusedPlaceholderColor = Color(0xFF8E8E93)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = "${playlistTitle.length}/$maxCharacters",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (playlistTitle.isNotBlank()) {
                            onCreatePlaylist(playlistTitle)
                            playlistTitle = ""
                            onDismiss()
                        }
                    },
                    enabled = playlistTitle.isNotBlank()
                ) {
                    Text(
                        text = "Done",
                        color = if (playlistTitle.isNotBlank())
                            Color(0xFF6B46C1) else Color(0xFF48484A)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        playlistTitle = ""
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
