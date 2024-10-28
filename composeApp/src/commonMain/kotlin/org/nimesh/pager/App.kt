package org.nimesh.pager

import CardStackAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.nimesh.pager.presentation.screens.ArticleCard
import org.nimesh.pager.presentation.screens.BookAnimationPager
import org.nimesh.pager.presentation.screens.FlipPagerOrientation
import org.nimesh.pager.presentation.screens.HorizontalBookAnimationPager
import org.nimesh.pager.presentation.screens.InstagramStoryPager
import org.nimesh.pager.presentation.screens.MainScreen
import org.nimesh.pager.presentation.screens.MoviePagerAnimation
import org.nimesh.pager.presentation.screens.VerticalBookAnimationPager
import org.nimesh.pager.presentation.screens.WallAnimationPager
import org.nimesh.pager.presentation.screens.articleList

@Composable
@Preview
fun App() {
    MaterialTheme {

        Scaffold(
            containerColor = Color.Black
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = MainScreen) {
                composable<MainScreen> {
                    MainScreen(
                        navController = navController
                    )
                }
                composable<InstagramStoryPager> {
                    InstagramStoryPager(navController = navController)
                }
                composable<VerticalBookAnimationPager> {
                    var orientation: FlipPagerOrientation by remember {
                        mutableStateOf(FlipPagerOrientation.Vertical)
                    }
                    val state = rememberPagerState { articleList.size }
                    BookAnimationPager(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                        orientation = orientation,
                        navController = navController,
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp)),
                        ) {
                            ArticleCard(
                                modifier = Modifier.align(Alignment.Center),
                                pageNo = if (page + 1 < 10) "0${page + 1}" else {
                                    page + 1
                                }.toString(),
                                articleItem = articleList[page],
                            )
                        }
                    }
                }
                composable<HorizontalBookAnimationPager> {
                    var orientation: FlipPagerOrientation by remember {
                        mutableStateOf(FlipPagerOrientation.Horizontal)
                    }
                    val state = rememberPagerState { articleList.size }
                    BookAnimationPager(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                        orientation = orientation,
                        navController = navController,
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp)),
                        ) {
                            ArticleCard(
                                modifier = Modifier.align(Alignment.Center),
                                pageNo = if (page + 1 < 10) "0${page + 1}" else {
                                    page + 1
                                }.toString(),
                                articleItem = articleList[page],
                            )
                        }
                    }
                }
                composable<WallAnimationPager> {
                    WallAnimationPager(navController = navController)
                }
                composable<MoviePagerAnimation> {
                    CardStackAnimation(navController = navController)
                }
            }
        }
    }
}