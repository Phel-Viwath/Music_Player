package com.viwath.music_player.presentation.ui.screen.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SearchBar3(
    modifier: Modifier,
    onSearch: (String) -> Unit,
    onClose: () -> Unit
){

    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit){
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    DisposableEffect(Unit){
        onDispose {
            keyboardController?.hide()
        }
    }

    TextField(
        modifier = modifier
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
            fontSize = 12.sp
        ) },
        leadingIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                onClose()
            }){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
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
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(alpha = 0.8f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.White
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(text) })
    )

}