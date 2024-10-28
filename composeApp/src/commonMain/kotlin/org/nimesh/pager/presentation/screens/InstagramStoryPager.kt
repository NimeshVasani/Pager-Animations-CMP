package org.nimesh.pager.presentation.screens

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pager_animations.composeapp.generated.resources.Res
import pager_animations.composeapp.generated.resources.image_1
import pager_animations.composeapp.generated.resources.image_2
import pager_animations.composeapp.generated.resources.image_3
import pager_animations.composeapp.generated.resources.image_4
import pager_animations.composeapp.generated.resources.image_5
import pager_animations.composeapp.generated.resources.image_6
import pager_animations.composeapp.generated.resources.image_7
import kotlin.math.absoluteValue
import kotlin.math.min

@Composable
fun InstagramStoryPager(modifier: Modifier = Modifier, navController: NavController) {
    val imageList = listOf<DrawableResource>(
        Res.drawable.image_1,
        Res.drawable.image_2,
        Res.drawable.image_3,
        Res.drawable.image_4,
        Res.drawable.image_5,
        Res.drawable.image_6,
        Res.drawable.image_7
    )
    var showPager by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }

    val pagerState = rememberPagerState(
        pageCount = { imageList.size },
        initialPage = currentImageIndex
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentImageIndex) {
        if (showPager) {
            pagerState.scrollToPage(currentImageIndex) // Change to scrollToPage for immediate change
        }
    }

    Box {
        Column(
            modifier = Modifier.safeDrawingPadding().fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.clickable {
                    navController.navigateUp()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(text = "Back", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Instagram Story like pager animation or Cubic animation",
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                maxLines = 2,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )
            LazyRow {
                itemsIndexed(imageList) { index, item ->
                    Image(
                        painter = painterResource(item),
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                            .border(2.dp, color = Color.White, shape = CircleShape)
                            .clip(CircleShape)
                            .size(100.dp).clickable {
                                coroutineScope.launch {

                                }
                                currentImageIndex = index
                                showPager = true
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        if (showPager) {
            HorizontalPager(
                modifier = modifier,
                state = pagerState,
                verticalAlignment = Alignment.CenterVertically,
                snapPosition = SnapPosition.End,
            ) { page ->
                Box(
                    modifier = Modifier.background(Color.White)
                        .graphicsLayer {
                            val pageOffset = pagerState.offsetForPage(page)
                            val offScreenRight = pageOffset < 0f
                            val deg = 105f
                            val interpolated =
                                FastOutLinearInEasing.transform(pageOffset.absoluteValue)
                            rotationY = min(interpolated * if (offScreenRight) deg else -deg, 90f)
                            transformOrigin = TransformOrigin(
                                pivotFractionX = if (offScreenRight) 0f else 1f,
                                pivotFractionY = .5f
                            )
                        }.drawWithContent {
                            val pageOffset = pagerState.offsetForPage(page)

                            this.drawContent()
                            drawRect(
                                Color.Black.copy(
                                    (pageOffset.absoluteValue * .7f)
                                )
                            )
                        }
                        .background(Color.LightGray)
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    // you can create custom pause like instagram story timer
                                },
                                onPress = {

                                },
                                onTap = {
                                    val maxWidth = size.width
                                    if (it.x > maxWidth / 2) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    Image(
                        painter = painterResource(imageList[page]),
                        contentDescription = null,
                        modifier = Modifier.background(Color.White)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {
                            showPager = false
                        },
                        modifier = Modifier.safeDrawingPadding().align(Alignment.TopEnd)
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction
