package com.viwath.music_player.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.viwath.music_player.core.util.SortOrder
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.presentation.ui.screen.album_list.AlbumScreen
import com.viwath.music_player.presentation.ui.screen.component.AmbientGradientBackground
import com.viwath.music_player.presentation.ui.screen.event.MusicEvent
import com.viwath.music_player.presentation.ui.screen.music_list.MusicListScreen
import com.viwath.music_player.presentation.ui.screen.music_list.component.MainTopBar
import com.viwath.music_player.presentation.ui.screen.playlist.PlaylistScreen
import com.viwath.music_player.presentation.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onMusicSelected: (MusicDto) -> Unit,
    navController: NavController,
    initialTab: Int = 0,
    coroutinesScope: CoroutineScope = rememberCoroutineScope()
){
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = initialTab)
    val scope = rememberCoroutineScope()
    val selectedTab = pagerState.currentPage
    val selectedOrder = viewModel.state.value.sortOrder


    Scaffold(
        topBar = {
            MainTopBar(
                selectedOption = selectedOrder,
            ){ order ->
                coroutinesScope.launch(Dispatchers.Main){
                    viewModel.onEvent(MusicEvent.Order(order))
                    delay(500)
                    viewModel.onEvent(MusicEvent.GetOrder)
                    delay(500)
                    viewModel.onEvent(MusicEvent.OnLoadMusic)
                }
            }
        }
    ){ innerPadding ->

        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            top = 0.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = innerPadding.calculateBottomPadding()
        )

        AmbientGradientBackground(
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            TabBar(
                modifier = Modifier.background(Color.Transparent),
                selectedTabIndex = selectedTab,
                onTabSelected = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) { page ->
                when(page) {
                    0 -> MusicListScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        viewModel = viewModel,
                        onMusicSelected = { selectedMusic ->
                            onMusicSelected(selectedMusic)
                        }
                    )

                    1 -> AlbumScreen(navController = navController)

                    2 -> PlaylistScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        navController = navController
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
        TabItem("Music", Icons.Default.MusicNote),
        TabItem("Album", Icons.Default.Album),
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
            .padding(horizontal = 4.dp),
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