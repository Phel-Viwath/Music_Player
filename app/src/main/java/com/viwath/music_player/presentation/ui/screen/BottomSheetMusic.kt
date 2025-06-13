package com.viwath.music_player.presentation.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.music_detail.MusicDetailScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import com.viwath.music_player.presentation.viewmodel.VisualizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMusic(
    currentMusic: Music,
    musicViewModel: MusicViewModel,
    visualizerViewModel: VisualizerViewModel,
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
        dragHandle = null
    ) {
        MusicDetailScreen(
            music = currentMusic,
            viewModel = musicViewModel,
            visualizerViewModel = visualizerViewModel
        )
    }
}