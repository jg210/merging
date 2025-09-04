package uk.me.jeremygreen.merging2.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import uk.me.jeremygreen.merging2.model.entity.Image

@Composable
internal fun Pager(
    images: ImmutableList<Image>,
    deleteImage: (Image) -> Unit,
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
            Pages(images, page, deleteImage)
        }
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
