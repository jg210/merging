package uk.me.jeremygreen.merging2.main.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import coil.compose.AsyncImage
import uk.me.jeremygreen.merging2.model.AppViewModel
import uk.me.jeremygreen.merging2.model.FaceWithCoordinates
import uk.me.jeremygreen.merging2.model.entity.Image

private const val TAG = "InputImage"

private const val FACE_DOT_RADIUS: Float = 3f

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun InputImage(
    image: Image?,
    onLongClick : () -> Unit = {},
    appViewModel: AppViewModel
) {
    if (image == null) {
        return
    }
    val facesLiveData = appViewModel.findFacesByImageId(image.id)
    val faces = facesLiveData.observeAsState(listOf()).value
    return AsyncImage(
            model = image.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().combinedClickable(
                enabled = true,
                onLongClick = {
                    // TODO Alert dialogue with Delete/Cancel - R.string.confirmDeleteImage
                    onLongClick()
                },
                onClick = {},
            ).drawWithContent {
                drawContent()
                drawFaces(this, faces)
            }

        )
}

private fun drawFaces(
    scope: ContentDrawScope,
    faces: List<FaceWithCoordinates>
) {
    val bounds = Rect(Offset.Zero, scope.size)
    Log.d(TAG, "drawFaces() bounds: ${bounds}")
    faces.forEach { face ->
        Log.d(TAG, "drawing face contours for face id: ${face.id}")
        face.coordinates.forEach { coordinate ->
            val x = bounds.left + coordinate.x * bounds.width
            val y = bounds.top + coordinate.y * bounds.height
            //Log.d(TAG, "drawing point at (${x}, ${y})")
            scope.drawCircle(
                color = Color.White,
                radius = FACE_DOT_RADIUS,
                center = Offset(x, y)
            )
        }
    }
}
