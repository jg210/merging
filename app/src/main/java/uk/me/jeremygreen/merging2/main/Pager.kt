package uk.me.jeremygreen.merging2.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import uk.me.jeremygreen.merging2.model.entity.Image

@Composable
internal fun Pager(
    images: ImmutableList<Image>,
    innerPadding: PaddingValues
) {
    val pagerState = rememberPagerState (
        pageCount = {
            pagerPageCount(images)
        }
    )
    Column(
        modifier = Modifier.padding(innerPadding),
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            modifier =  Modifier.fillMaxHeight()
        ) { page ->
            ScrollToAnyNewImage(images, pagerState)
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Pages(images, page)
            }
        }
    }

}

@Composable
private fun ScrollToAnyNewImage(
    images: ImmutableList<Image>,
    pagerState: PagerState
) {
    var previousImageCount by rememberSaveable { mutableIntStateOf(0) }
    val imageCount = images.size
    val thereIsNewImage = imageCount > previousImageCount
    LaunchedEffect (thereIsNewImage, imageCount) {
        if (thereIsNewImage) {
            // Assign ids in order and return images in id order, so newest image is last.
            pagerState.animateScrollToPage(imageCount - 1)
        }
        previousImageCount = imageCount
    }
}

internal fun pagerPageCount(images: ImmutableList<Image>?): Int {
    if (images.isNullOrEmpty()) {
        return 1 // add image page
    }
    return if (isMergedImageShown(images)) {
        images.size + 1
    } else {
        images.size
    }
}

internal fun isMergedImageShown(images: ImmutableList<Image>?) = !images.isNullOrEmpty() && images.size > 1
