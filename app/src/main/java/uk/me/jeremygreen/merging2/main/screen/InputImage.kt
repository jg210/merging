package uk.me.jeremygreen.merging2.main.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.mlkit.vision.face.FaceDetectorOptions
import uk.me.jeremygreen.merging2.model.Face
import uk.me.jeremygreen.merging2.model.Image

//private const val TAG = "ImageFragment"
//private const val BUNDLE_KEY__IMAGE_ID = "imageId"
//

private const val TAG = "InputImage"


// Size of Bitmap used by face detection algorithm.
private const val BITMAP_WIDTH = 360
private const val BITMAP_HEIGHT = 480

@Suppress("unused")
private val faceDetectorOptions  =
    FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun InputImage(
    image: Image?,
    onLongClick : () -> Unit = {}
) {
    if (image == null) {
        return
    }
    val context = LocalContext.current
    LaunchedEffect (image.uri) {
        // TODO Move all this code somewhere more appropriate
        Log.i(TAG, "id: ${image.id} loading ${image.uri}")
        val bitmap = loadBitmap(context, image.uri)
        bitmap?.let { bitmap ->
            val onError: (Exception) -> Unit = { e: Exception ->
                Log.e(TAG, "id: ${image.id} findFaces() error", e)
            }
            val onSuccess: (List<Face>) -> Unit = { faces: List<Face> ->
                Log.i(TAG, "id: ${image.id} findFaces() found ${faces.size} faces")
                // TODO persist the faces
            }
            image.findFaces(bitmap, faceDetectorOptions, onError, onSuccess)
        }
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

@Suppress("unused")
private suspend fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(uri)
        .allowHardware(false) // Needed to get software Bitmap
        .size(BITMAP_WIDTH, BITMAP_HEIGHT)
        .build()

    val result = loader.execute(request)
    return if (result is SuccessResult) {
        (result.drawable as? BitmapDrawable)?.bitmap
    } else {
        // TODO analytics for failure
        // FirebaseCrashlytics.getInstance().recordException
        null
    }
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

