package com.viwath.music_player.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viwath.music_player.domain.model.MusicDto
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.music_list.MusicListScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onMusicListLoaded: (List<MusicDto>) -> Unit,
    onMusicSelected: (MusicDto) -> Unit
){
    var selectedTab by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }

    // Sync selected tab with pager state
    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab) {
            pagerState.animateScrollToPage(selectedTab)
        }
    }

    Box{
        AmbientGradientBackground(
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            TabBar(
                selectedTabIndex = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
                    .background(Color.Transparent)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) { page ->
                when(page) {
                    0 -> Text(
                        text = "Album Screen",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    1 -> MusicListScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        viewModel = viewModel,
                        onMusicListLoaded = { loadMusicList ->
                            onMusicListLoaded(loadMusicList)
                        },
                        onMusicSelected = { selectedMusic ->
                            onMusicSelected(selectedMusic)
                        }
                    )
                    2 -> Text(
                        text = "PlayList Screen",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

}

@Composable
private fun TabBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        TabItem("Album", Icons.Default.Album),
        TabItem("Music", Icons.Default.MusicNote),
        TabItem("PlayList", Icons.AutoMirrored.Filled.List)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
//                color = Color.Black.copy(alpha = 0.4f),
//                shape = RoundedCornerShape(24.dp)
                Color.Transparent
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            TabButton(
                tab = tab,
                isSelected = selectedTabIndex == index,
                showText = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                modifier = if (selectedTabIndex == index) Modifier.weight(1f) else Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun TabButton(
    tab: TabItem,
    isSelected: Boolean,
    showText: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF8B5CF6) else Color.Transparent,
        animationSpec = tween(300),
        label = "background_animation"
    )

    val animatedContent by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(300),
        label = "content_animation"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(animatedBackground)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                tint = animatedContent,
                modifier = Modifier.size(18.dp)
            )

            // Only show text when showText is true (i.e., when selected)
            if (showText && tab.title.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tab.title,
                    color = animatedContent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}