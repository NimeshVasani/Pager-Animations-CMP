import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.nimesh.pager.presentation.screens.offsetForPage
import pager_animations.composeapp.generated.resources.Res
import pager_animations.composeapp.generated.resources.image_1
import pager_animations.composeapp.generated.resources.image_2
import pager_animations.composeapp.generated.resources.image_3
import pager_animations.composeapp.generated.resources.image_4
import pager_animations.composeapp.generated.resources.image_5
import pager_animations.composeapp.generated.resources.image_6
import pager_animations.composeapp.generated.resources.image_7

@Composable
fun CardStackAnimation(
    navController: NavController
) {

    val horizontalState = rememberPagerState(initialPage = 2, pageCount = { imageList.size })

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
            Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null, tint = Color.White)
            Text(text = "Back", color = Color.White)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Card stack animation in Pager",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            maxLines = 2,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            state = horizontalState,
            pageSpacing = 1.dp,
            beyondViewportPageCount = 9,
        ) { page ->
            Box(
                modifier = Modifier
                    .zIndex(page * 10f)
                    .padding(
                        start = 64.dp,
                        end = 32.dp,
                    )
                    .graphicsLayer {
                        val startOffset = horizontalState.startOffsetForPage(page)
                        translationX = size.width * (startOffset * .99f)

                        alpha = (2f - startOffset) / 2f

                        val blur = (startOffset * 20f).coerceAtLeast(0.1f)
                        renderEffect = BlurEffect(
                            blur, blur, TileMode.Decal
                        )


                        val scale = 1f - (startOffset * .1f)
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = Color(0xFFF58133),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(imageList[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

    }
}


val imageList = listOf(
    Res.drawable.image_1,
    Res.drawable.image_2,
    Res.drawable.image_3,
    Res.drawable.image_4,
    Res.drawable.image_5,
    Res.drawable.image_6,
    Res.drawable.image_7
)

fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

// OFFSET ONLY FROM THE RIGHT
fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}