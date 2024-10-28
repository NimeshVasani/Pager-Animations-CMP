package org.nimesh.pager.presentation.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import endOffsetForPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.nimesh.pager.presentation.pager
import pager_animations.composeapp.generated.resources.Res
import pager_animations.composeapp.generated.resources.image_1
import pager_animations.composeapp.generated.resources.image_2
import pager_animations.composeapp.generated.resources.image_3
import pager_animations.composeapp.generated.resources.image_4
import pager_animations.composeapp.generated.resources.image_5
import pager_animations.composeapp.generated.resources.image_6
import pager_animations.composeapp.generated.resources.image_7
import startOffsetForPage
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
fun BookAnimationPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    orientation: FlipPagerOrientation,
    navController: NavController,
    pageContent: @Composable (Int) -> Unit,
) {
    val overscrollAmount = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.isScrollInProgress }.collect {
            if (!it) overscrollAmount.floatValue = 0f
        }
    }
    val animatedOverscrollAmount by animateFloatAsState(
        targetValue = overscrollAmount.floatValue / 500,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = ""
    )
    val nestedScrollConnection = rememberFlipPagerOverscroll(
        orientation = orientation,
        overscrollAmount = overscrollAmount
    )

    when (orientation) {
        FlipPagerOrientation.Vertical -> {
            Column(
                modifier = modifier.safeDrawingPadding()
                    .fillMaxSize()
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
                Spacer(modifier = Modifier.height(16.dp))
                VerticalPager(
                    state = state,
                    modifier = modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                    pageContent = {
                        Content(
                            it,
                            state,
                            orientation,
                            pageContent,
                            animatedOverscrollAmount
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        FlipPagerOrientation.Horizontal -> {
            Column(
                modifier = modifier.safeDrawingPadding()
                    .fillMaxSize()
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
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalPager(
                    state = state,
                    modifier = modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                    pageContent = {
                        Content(
                            it,
                            state,
                            orientation,
                            pageContent,
                            animatedOverscrollAmount
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun Content(
    page: Int,
    state: PagerState,
    orientation: FlipPagerOrientation,
    pageContent: @Composable (Int) -> Unit,
    animatedOverscrollAmount: Float
) {
    var zIndex by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.offsetForPage(page) }.collect {
            zIndex = when (state.offsetForPage(page)) {
                in -.5f..(.5f) -> 3f
                in -1f..1f -> 2f
                else -> 1f
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .graphicsLayer {
                val pageOffset = state.offsetForPage(page)
                when (orientation) {
                    FlipPagerOrientation.Vertical -> translationY = size.height * pageOffset
                    FlipPagerOrientation.Horizontal -> translationX = size.width * pageOffset
                }
            },
        contentAlignment = Alignment.Center,
    ) {

        var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        val graphicsLayer = rememberGraphicsLayer()
        val isImageBitmapNull by remember {
            derivedStateOf {
                imageBitmap == null
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .alpha(if (state.isScrollInProgress && !isImageBitmapNull) 0f else 1f)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                },
            contentAlignment = Alignment.Center
        ) {
            pageContent(page)
        }

        LaunchedEffect(state.isScrollInProgress) {
            while (true) {
                if (graphicsLayer.size.width != 0)
                    imageBitmap = graphicsLayer.toImageBitmap()
                delay(if (state.isScrollInProgress) 16 else 300)
            }
        }

        LaunchedEffect(MaterialTheme.colorScheme.surface) {
            if (graphicsLayer.size.width != 0)
                imageBitmap = graphicsLayer.toImageBitmap()
        }

        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = when (orientation) {
                FlipPagerOrientation.Vertical -> PageFlapType.Top
                FlipPagerOrientation.Horizontal -> PageFlapType.Left
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )

        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = when (orientation) {
                FlipPagerOrientation.Vertical -> PageFlapType.Bottom
                FlipPagerOrientation.Horizontal -> PageFlapType.Right
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )
    }
}


@Composable
private fun rememberFlipPagerOverscroll(
    orientation: FlipPagerOrientation,
    overscrollAmount: MutableFloatState
): NestedScrollConnection {
    val nestedScrollConnection = remember(orientation) {
        object : NestedScrollConnection {

            private fun calculateOverscroll(available: Float) {
                val previous = overscrollAmount.floatValue
                overscrollAmount.floatValue += available * (.3f)
                overscrollAmount.floatValue = when {
                    previous > 0 -> overscrollAmount.floatValue.coerceAtLeast(0f)
                    previous < 0 -> overscrollAmount.floatValue.coerceAtMost(0f)
                    else -> overscrollAmount.floatValue
                }
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (overscrollAmount.floatValue != 0f) {
                    when (orientation) {
                        FlipPagerOrientation.Vertical -> calculateOverscroll(available.y)
                        FlipPagerOrientation.Horizontal -> calculateOverscroll(available.x)
                    }
                    return available
                }

                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                when (orientation) {
                    FlipPagerOrientation.Vertical -> calculateOverscroll(available.y)
                    FlipPagerOrientation.Horizontal -> calculateOverscroll(available.x)
                }
                return available
            }
        }
    }
    return nestedScrollConnection
}

sealed class FlipPagerOrientation {
    data object Vertical : FlipPagerOrientation()
    data object Horizontal : FlipPagerOrientation()
}

@Composable
internal fun BoxScope.PageFlap(
    modifier: Modifier = Modifier,
    pageFlap: PageFlapType,
    imageBitmap: () -> ImageBitmap?,
    state: PagerState,
    page: Int,
    animatedOverscrollAmount: () -> Float = { 0f },
) {
    val density = LocalDensity.current
    val size by remember {
        derivedStateOf {
            imageBitmap()?.let {
                with(density) {
                    DpSize(it.width.toDp(), it.height.toDp())
                }
            } ?: DpSize.Zero
        }
    }
    Canvas(
        modifier
            .size(size)
            .align(Alignment.TopStart)
            .graphicsLayer {
                shape = pageFlap.shape
                clip = true

                cameraDistance = 65f
                when (pageFlap) {
                    is PageFlapType.Top -> {
                        rotationX = min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Bottom -> {
                        rotationX = max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }

                    is PageFlapType.Left -> {
                        rotationY = -min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Right -> {
                        rotationY = -max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }
                }
            }
    ) {
        imageBitmap()?.let { imageBitmap ->
            drawImage(imageBitmap)
            drawImage(
                imageBitmap,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(
                        alpha = when (pageFlap) {
                            PageFlapType.Top, PageFlapType.Left -> max(
                                (state.endOffsetForPage(page).absoluteValue * .9f).coerceIn(
                                    0f..1f
                                ), animatedOverscrollAmount() * .3f
                            )

                            PageFlapType.Bottom, PageFlapType.Right -> max(
                                (state.startOffsetForPage(page) * .9f).coerceIn(
                                    0f..1f
                                ), (animatedOverscrollAmount() * -1) * .3f
                            )
                        },
                    )
                )
            )
        }
    }
}

internal sealed class PageFlapType(val shape: Shape) {
    data object Top : PageFlapType(TopShape)
    data object Bottom : PageFlapType(BottomShape)
    data object Left : PageFlapType(LeftShape)
    data object Right : PageFlapType(RightShape)
}

val TopShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width, size.height / 2))
}

val BottomShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, size.height / 2, size.width, size.height))
}

val LeftShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
}

val RightShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(size.width / 2, 0f, size.width, size.height))


}

data class ArticleItem(
    val title: String,
    val description: String,
    val thumbnail: DrawableResource,
)

@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    pageNo: String,
    articleItem: ArticleItem,
) {

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .widthIn(max = 480.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
        ) {
            Image(
                painter = painterResource(articleItem.thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = pageNo.toString(),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.padding(30.dp).align(Alignment.TopEnd),
                fontSize = 50.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(3f, 3f),
                        blurRadius = 3f
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "My life story",
                fontSize = 20.sp,
                color = Color.Gray
            )
            Text(
                text = articleItem.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = articleItem.description,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .84f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

val articleList = listOf(
    ArticleItem(
        title = "Exploring the Hidden Trails of Patagonia",
        description = "Join me on a journey through Patagonia's untouched landscapes. Each trail offers breathtaking views of rugged mountains, sparkling blue lakes, and glaciers. Hiking here, I learned to embrace solitude and the thrill of discovery, far from the usual tourist paths. This adventure taught me the true meaning of wilderness and the joy of finding peace in nature.",
        thumbnail = Res.drawable.image_1
    ),
    ArticleItem(
        title = "A Life-Changing Encounter in Tokyo",
        description = "Reflecting on an unexpected encounter with a kind stranger in the heart of Tokyo. Amidst the bustling city, this person’s kindness and wisdom reminded me of humanity's universal goodness. We shared stories over tea, and their insight deeply resonated, shifting my views on life, travel, and the beauty of human connection in the most unlikely places.",
        thumbnail = Res.drawable.image_2
    ),
    ArticleItem(
        title = "Sunrise over the Grand Canyon",
        description = "An unforgettable morning as I watched the first light kiss the cliffs of the Grand Canyon, casting shades of pink and gold across the vast expanse. Standing at the edge, with only the sound of the wind, I felt an overwhelming sense of awe and insignificance in the face of nature’s grandeur. This moment reaffirmed my love for travel and nature’s healing power.",
        thumbnail = Res.drawable.image_3
    ),
    ArticleItem(
        title = "Finding Peace in Bali’s Hidden Temples",
        description = "Venturing off the beaten path in Bali led me to serene, lesser-known temples nestled among lush forests. Away from the crowds, I found a quiet space to reflect and connect with Bali's rich spiritual heritage. These temples, adorned with intricate carvings and guarded by ancient statues, were a reminder of the enduring peace that can be found in cultural traditions.",
        thumbnail = Res.drawable.image_4
    ),
    ArticleItem(
        title = "A Culinary Adventure in Paris",
        description = "From bustling street markets to cozy bakeries, my Parisian culinary journey was a feast for the senses. I explored Paris beyond the famous landmarks, discovering local favorites and hidden gems. Each dish, from flaky croissants to savory coq au vin, told a story of culture and tradition. Paris taught me that food is a language of its own, connecting people and places.",
        thumbnail = Res.drawable.image_5
    ),
    ArticleItem(
        title = "Volunteering in Rural Kenya",
        description = "Living and volunteering in a rural Kenyan village was a transformative experience. I spent time helping with local projects, from building classrooms to teaching, while forming bonds with the community. Their resilience, optimism, and warmth left a lasting impact, teaching me the value of kindness, resourcefulness, and the strength of community even in challenging circumstances.",
        thumbnail = Res.drawable.image_6
    ),
    ArticleItem(
        title = "Retracing Family Roots in Ireland",
        description = "Traveling through the emerald landscapes of Ireland was more than a trip; it was a journey into my family’s history. From small villages to sweeping coastlines, I pieced together stories from my ancestors and felt a deep connection to the land they once called home. This journey was a powerful reminder of heritage, family bonds, and the role our roots play in shaping who we are.",
        thumbnail = Res.drawable.image_7
    ),
)


