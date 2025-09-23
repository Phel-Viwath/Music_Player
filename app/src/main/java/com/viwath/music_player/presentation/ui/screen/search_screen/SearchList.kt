package com.viwath.music_player.presentation.ui.screen.search_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.viwath.music_player.domain.model.dto.MusicDto

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    musicList: List<MusicDto>,
    query: String,
    onMusicSelected: (MusicDto) -> Unit
){

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        items(musicList.size){ index ->
            SearchListItem(
                char = query,
                musicDto = musicList[index]
            ){ selectedMusic ->
                onMusicSelected(selectedMusic)
            }
        }
    }

}

@Composable
fun SearchListItem(
    char: String,
    musicDto: MusicDto,
    onMusicSelected: (MusicDto) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onMusicSelected(musicDto) }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ){

            HilightText(
                text = musicDto.title,
                query = char,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Text(
                text = musicDto.artist,
                style = MaterialTheme.typography.bodyMedium ,
                color = Color.Gray,
                maxLines = 1
            )

        }

    }
}

@Composable
fun HilightText(
    modifier: Modifier = Modifier,
    text: String,
    query: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    normalColor: Color = Color.White,
    highlightColor: Color = Color.Green,
    caseSensitive: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
){
    if(query.isEmpty()){
        Text(
            modifier = modifier,
            text = text,
            style = style,
            color = normalColor,
        )
        return
    }


    val annotatedString = remember(text, query, caseSensitive){
        buildAnnotatedString {
            val searchText = if (caseSensitive) text else text.lowercase()
            val searchQuery = if (caseSensitive) query else query.lowercase()


            var lastIndex = 0
            var startIndex = searchText.indexOf(searchQuery)

            while (startIndex != -1){
                withStyle(style = SpanStyle(color = normalColor)){
                    append(text.substring(lastIndex, startIndex))
                }

                withStyle(style = SpanStyle(color = highlightColor)){
                    append(text.substring(startIndex, startIndex + query.length))
                }

                lastIndex = startIndex + query.length
                startIndex = searchText.indexOf(searchQuery, lastIndex)
            }
            // Add remaining text
            if (lastIndex < text.length) {
                withStyle(style = SpanStyle(color = normalColor)) {
                    append(text.substring(lastIndex))
                }
            }
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = style
    )
}