package uk.me.jeremygreen.merging2.main.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.mlkit.vision.face.FaceDetectorOptions
import uk.me.jeremygreen.merging2.model.Image

//private const val TAG = "ImageFragment"
//private const val BUNDLE_KEY__IMAGE_ID = "imageId"
//


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
    return AsyncImage(
            model = image.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().combinedClickable(
                enabled = true,
                onLongClick = { onLongClick() },
                onClick = {}
            )
        )
    // TODO show faces
}

@Suppress("unused")
private suspend fun loadBitmap(context: Context, imageUrl: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .allowHardware(false) // Needed to get software Bitmap
        .size(BITMAP_WIDTH, BITMAP_HEIGHT)
        .build()

    val result = loader.execute(request)
    return if (result is SuccessResult) {
        (result.drawable as? BitmapDrawable)?.bitmap
    } else {
        // TODO analytics for failure
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



// private fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val bundle = arguments
//        val imageId: Long = bundle!!.getLong(BUNDLE_KEY__IMAGE_ID)
//        val facesView = binding.faces
//        appViewModel.faces(imageId).observe(viewLifecycleOwner) { faces ->
//            facesView.faces = faces
//        }
//        launch(Dispatchers.IO) {
//            val image = appViewModel.findById(imageId)
//            launch(Dispatchers.Main) {
//                facesView.setImage(image, ::handleLongClick)
//            }
//            val processingStage = appViewModel.getProcessingStage(imageId)
//            if (processingStage == ProcessingStage.unprocessed) {
//                processFaces(image, imageId)
//            }
//        }
//    }
//
//    private fun processFaces(image: Image, imageId: Long) {
//        // https//firebase.google.com/docs/ml-kit/android/detect-faces suggests size to use.
//        image.processBitmap(BITMAP_WIDTH, BITMAP_HEIGHT) { closeableReference ->
//            Log.i(TAG, "decoded bitmap for image id: ${imageId}")
//            // The IO thread has done it's work reading the Bitmap. Don't want to block this thread any more,
//            // so clone the reference and hand it to Dispatcher.Default coroutine to do the CPU-intensive
//            // face-detection work.
//            val clonedReference = closeableReference.clone()
//            launch(Dispatchers.Default) {
//                Log.i(TAG, "detecting faces for image id: ${imageId}")
//                image.findFaces(clonedReference, faceDetectorOptions, ::handleFaceDetectionError) { faces ->
//                    Log.i(TAG, "detected ${faces.size} faces for image id: ${imageId}")
//                    val processedImage = image.copy(processingStage = ProcessingStage.facesDetected)
//                    appViewModel.addAll(processedImage, faces)
//                }
//            }
//        }
//    }
//
//    private fun handleFaceDetectionError(e: Exception) {
//        Log.e(TAG, "face detection failed", e)
//        FirebaseCrashlytics.getInstance().recordException(e)
//    }
//
//    private fun handleLongClick(image: Image) {
//        AlertDialog.Builder(requireContext()).apply {
//            setMessage(R.string.confirmDeleteImage)
//            setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
//                binding.faces.setOnLongClickListener { false }
//                appViewModel.delete(image)
//            }
//            setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> }
//            show()
//        }
//    }

