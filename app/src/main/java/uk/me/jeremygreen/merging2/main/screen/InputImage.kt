package uk.me.jeremygreen.merging2.main.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import uk.me.jeremygreen.merging2.model.entity.Image

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun InputImage(
    image: Image?,
    onLongClick : () -> Unit = {}
) {
    if (image == null) {
        return
    }
    return AsyncImage(
            model = image.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().combinedClickable(
                enabled = true,
                onLongClick = {
                    // TODO Alert dialogue with Delete/Cancel - R.string.confirmDeleteImage
                    onLongClick()
                },
                onClick = {}
            )
        )
    // TODO show faces
}

//private fun drawFaces(canvas: Canvas) {
//
//    val bounds = RectF()
//    // TODO get bounds
//    Log.d(TAG, "drawFaces() bounds: ${bounds.toShortString()}")
//    this.faces.forEach { face ->
//        Log.d(TAG, "drawing face contours for face id: ${face.id}")
//        face.coordinates.forEach { coordinate ->
//            val x = bounds.left + coordinate.x * bounds.width()
//            val y = bounds.top + coordinate.y * bounds.height()
//            //Log.d(TAG, "drawing point at (${x}, ${y})")
//            canvas.drawOval(
//                x - FACE_DOT_RADIUS,
//                y - FACE_DOT_RADIUS,
//                x + FACE_DOT_RADIUS,
//                y + FACE_DOT_RADIUS, paint
//            )
//        }
//    }
//}

