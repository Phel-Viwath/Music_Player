package com.viwath.music_player.presentation.ui.screen

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.music_detail.MusicDetailScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMusic(
    currentMusic: MusicDto,
    musicViewModel: MusicViewModel,
    onDismiss: (Boolean) -> Unit,
){
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss(false)
        },
        sheetState = bottomSheetState,
        dragHandle = null,
    ) {
        MusicDetailScreen(
            modifier = Modifier.statusBarsPadding(),
            music = currentMusic,
            viewModel = musicViewModel,
            //visualizerViewModel = visualizerViewModel
        )
    }
}