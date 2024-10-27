package org.nimesh.pager.presentation.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import endOffsetForPage
import kotlinx.coroutines.delay
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
import startOffsetForPage
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
fun FlipPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    orientation: FlipPagerOrientation,
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
            VerticalPager(
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
        }

        FlipPagerOrientation.Horizontal -> {
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

data class Headline(
    val title: String,
    val description: String,
    val category: String,
    val image: DrawableResource,
)

@Composable
fun HeadlineArticle(
    modifier: Modifier = Modifier,
    headline: Headline,
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
                painter = painterResource (headline.image) ,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = headline.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .56f)
            )
            Text(
                text = headline.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = headline.description,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .84f)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

}







val headlines = listOf(
    Headline(
        title = "Historic Win for Local Soccer Team",
        description = "In an unexpected turn of events, the local soccer team clinched a historic victory against their arch-rivals. The match, held at the city stadium, was attended by thousands of enthusiastic fans who witnessed a nail-biting finish. The team's captain led from the front, scoring the decisive goal in the final minutes. This win has reignited hopes of a league title for the first time in decades. Fans are ecstatic and celebrations have erupted across the city. The coach praised the team's resilience and tactical discipline. This triumph marks a significant milestone in the team's journey. Supporters are now eagerly looking forward to the upcoming fixtures. The community has rallied behind the team, showcasing immense pride and support.",
        category = "sports",
        image = Res.drawable.image_1
    ),
    Headline(
        title = "Major Breakthrough in Cancer Research",
        description = "Scientists at the renowned medical institute have announced a major breakthrough in cancer research. The new treatment, which has shown promising results in early trials, targets cancer cells with unprecedented precision. This innovative approach could potentially revolutionize cancer therapy, offering hope to millions of patients worldwide. The research team, led by Dr. Jane Smith, has been working tirelessly for years to achieve this milestone. The treatment has already received accolades from the scientific community. Further clinical trials are set to begin soon, with the aim of making the treatment widely available within the next few years. This development represents a significant leap forward in the fight against cancer. The medical community is optimistic about the future implications. Patients and their families are hopeful for more effective treatments.",
        category = "health",
        image = Res.drawable.image_2
    ),
    Headline(
        title = "International Summit on Climate Change Concludes",
        description = "The annual international summit on climate change concluded today with leaders from around the world pledging renewed commitment to combat global warming. The summit, held in Geneva, saw heated discussions and landmark agreements. Key resolutions include reducing carbon emissions by 50% over the next decade. This ambitious goal aims to limit global temperature rise to 1.5 degrees Celsius. Environmental activists welcomed the commitments but urged for swift implementation. Financial aid was promised to developing nations to help them transition to green energy. The summit emphasized the importance of global cooperation in addressing the climate crisis. Experts highlighted the urgent need for policy changes and innovation. The outcomes of this summit will shape future climate actions. Global leaders called for unity and sustained efforts to protect the planet.",
        category = "world",
        image = Res.drawable.image_3
    ),
    Headline(
        title = "New Tech Startup Disrupts the Market",
        description = "A new tech startup has taken the market by storm with its innovative product that promises to change the way we interact with technology. The company, founded by two university graduates, has developed a cutting-edge virtual assistant. This AI-powered assistant can seamlessly integrate with various devices and applications. Early adopters have praised its user-friendly interface and advanced features. The startup has secured significant funding from leading venture capitalists. Industry experts predict that this product will set new standards in the tech world. The founders envision a future where technology is more accessible and intuitive. Plans for further developments and expansions are already underway. The startup's success story is inspiring young entrepreneurs. The market is eagerly watching how this new player will evolve.",
        category = "technology",
        image = Res.drawable.image_4
    ),
    Headline(
        title = "Art Exhibition Showcases Local Talent",
        description = "The city's annual art exhibition opened its doors today, featuring a stunning array of artworks from local artists. The exhibition, held at the downtown gallery, includes paintings, sculptures, and digital art. Visitors are treated to a diverse collection that reflects the rich cultural heritage of the region. This year's theme focuses on the intersection of tradition and modernity. The exhibition has attracted art enthusiasts and collectors from across the country. Highlights include a series of paintings that depict urban life. The event aims to provide a platform for emerging artists to showcase their talent. Workshops and interactive sessions are also part of the program. The exhibition will run for two weeks, offering ample opportunity for visitors to explore and appreciate the art. Organizers are hopeful that this event will foster greater appreciation for the arts.",
        category = "arts",
        image = Res.drawable.image_5
    ),
    Headline(
        title = "Groundbreaking Ceremony for New Hospital",
        description = "A groundbreaking ceremony was held today for the construction of a new state-of-the-art hospital in the city. The hospital, which will be equipped with the latest medical technology, aims to provide high-quality healthcare to the community. Local officials, healthcare professionals, and residents attended the ceremony. The project is expected to create numerous jobs and boost the local economy. The hospital will include specialized departments for cardiology, oncology, and pediatrics. Plans also include a research center dedicated to medical innovations. The construction is scheduled to be completed within two years. The new facility will address the growing healthcare needs of the population. Officials emphasized the importance of this project for the well-being of the community. The initiative has received widespread support from various stakeholders.",
        category = "local",
        image = Res.drawable.image_6
    ),
    Headline(
        title = "Ancient Artifacts Discovered in Archaeological Dig",
        description = "Archaeologists have unearthed a treasure trove of ancient artifacts in a recent excavation. The site, located near the historic town, has revealed objects dating back thousands of years. These artifacts include pottery, tools, and jewelry, offering a glimpse into the lives of early inhabitants. The discovery has excited historians and researchers. It is expected to provide valuable insights into the region's ancient civilization. The excavation team, led by Dr. John Doe, has meticulously documented the findings. Plans are underway to study and preserve these artifacts. The local museum has expressed interest in displaying them to the public. This discovery underscores the historical significance of the area. Further excavations are planned to uncover more about this ancient culture. The findings have generated considerable interest in the academic community.",
        category = "history",
        image = Res.drawable.image_7
    ),

)