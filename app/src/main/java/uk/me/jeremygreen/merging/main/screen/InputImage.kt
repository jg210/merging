package uk.me.jeremygreen.merging.main.screen

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.model.Image
import uk.me.jeremygreen.merging.model.ProcessingStage

private const val TAG = "ImageFragment"
private const val BUNDLE_KEY__IMAGE_ID = "imageId"

private const val BITMAP_WIDTH = 360
private const val BITMAP_HEIGHT = 480

private val faceDetectorOptions  =
    FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

@Composable
internal fun InputImage(image: Image?) {
    Text(image?.uri.toString())
    // TODO show FacesView
}

//    <uk.me.jeremygreen.merging.main.FacesView
//    android:id="@+id/faces"
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    fresco:placeholderImage="@color/imagePlaceholder"
//    fresco:backgroundImage="@color/imagePlaceholder"
//    fresco:actualImageScaleType="fitCenter"
//    />

//        fun createFactory(image: Image): ScreenFragmentFactory<InputImage> {
//            require(image.id >= 0) { "might collide with non-image id: ${image.id}" }
//            return object:
//                ScreenFragmentFactory<InputImage> {
//                override val id: Long = image.id
//                override fun createInstance(): InputImage {
//                    return InputImage().apply {
//                        arguments = Bundle().apply {
//                            putLong(BUNDLE_KEY__IMAGE_ID, image.id)
//                        }
//                    }
//                }
//                override fun screenName(): String = "Image"
//            }
//        }
//
//    }

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

