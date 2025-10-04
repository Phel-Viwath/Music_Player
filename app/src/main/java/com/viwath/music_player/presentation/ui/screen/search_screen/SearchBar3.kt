package com.viwath.music_player.presentation.ui.screen.search_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SearchBar3(
    modifier: Modifier,
    onSearch: (String) -> Unit,
    onClose: () -> Unit
){

    var isFocused by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-focus on appear
    LaunchedEffect(Unit){
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // hide keyboard when leave
    DisposableEffect(Unit){
        onDispose {
            keyboardController?.hide()
        }
    }

    Row(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (isFocused) Modifier else Modifier
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        // Back button
        IconButton(
            onClick = {
                keyboardController?.hide()
                onClose()
            },
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused){
                        keyboardController?.show()
                    }
                },
            value = text,
            onValueChange = { newText ->
                text = newText
                onSearch(newText)
            },
            placeholder = { Text(
                text = "Search music",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp
            ) },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            text = ""
                            onSearch("")
                            // Keep focus and keyboard visible after clearing
                            focusRequester.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFF8B5CF6),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(text) }),
            maxLines = 1,
            singleLine = true,
        )

    }



}