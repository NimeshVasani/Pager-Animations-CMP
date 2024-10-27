package org.nimesh.pager

import MoviePagerAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.nimesh.pager.presentation.screens.BookAnimationPager
import org.nimesh.pager.presentation.screens.FlipPager
import org.nimesh.pager.presentation.screens.FlipPagerOrientation
import org.nimesh.pager.presentation.screens.HeadlineArticle
import org.nimesh.pager.presentation.screens.InstagramStoryPager
import org.nimesh.pager.presentation.screens.MainScreen
import org.nimesh.pager.presentation.screens.MoviePagerAnimation
import org.nimesh.pager.presentation.screens.WallAnimationPager
import org.nimesh.pager.presentation.screens.headlines

@Composable
@Preview
fun App() {
    MaterialTheme {

        Scaffold {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = MainScreen) {
                composable<MainScreen> {
                    MainScreen(
                        navController = navController
                    )
                }
                composable<InstagramStoryPager> {
                    InstagramStoryPager()
                }
                composable<BookAnimationPager> {
                    var orientation: FlipPagerOrientation by remember {
                        mutableStateOf(FlipPagerOrientation.Vertical)
                    }
                    val state = rememberPagerState { headlines.size }
                    FlipPager(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                        orientation = orientation,
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                        ) {
                            HeadlineArticle(
                                modifier = Modifier.align(Alignment.Center),
                                headline = headlines[page],
                            )
                        }
                    }
                }
                composable<WallAnimationPager> {
                    WallAnimationPager()
                }
                composable<MoviePagerAnimation> {
                    MoviePagerAnimation()
                }
            }
        }
    }
}