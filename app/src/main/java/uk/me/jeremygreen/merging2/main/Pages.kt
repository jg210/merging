package uk.me.jeremygreen.merging2.main

import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import uk.me.jeremygreen.merging2.main.screen.AddImage
import uk.me.jeremygreen.merging2.main.screen.InputImage
import uk.me.jeremygreen.merging2.main.screen.MergedImage
import uk.me.jeremygreen.merging2.model.AppViewModel
import uk.me.jeremygreen.merging2.model.entity.Image

private const val TAG = "Pages"

@Composable
internal fun Pages(
    images: ImmutableList<Image>?,
    page: Int,
    appViewModel: AppViewModel
) {
    Log.i(TAG, "Pages: page=$page images=${images?.size}")
    if (images.isNullOrEmpty()) {
        AddImage()
        return
    }
    val isLastPage = page == pagerPageCount(images) - 1
    if (isLastPage && isMergedImageShown(images)) {
        MergedImage()
        return
    }
    if (page < 0 || page >= images.size) {
        // Non-zero beyondBoundsPageCount causes out-of-range page to be provided.
        return
    }
    val image = images[page]
    InputImage(image, onLongClick = { appViewModel.delete(image) }, appViewModel)
}
