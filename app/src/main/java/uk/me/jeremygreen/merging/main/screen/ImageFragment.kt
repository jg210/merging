package uk.me.jeremygreen.merging.main.screen

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.image_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.main.ScreenFragment
import uk.me.jeremygreen.merging.main.ScreenFragmentFactory
import uk.me.jeremygreen.merging.model.Coordinate
import uk.me.jeremygreen.merging.model.Face
import uk.me.jeremygreen.merging.model.Image
import uk.me.jeremygreen.merging.model.ProcessingStage
import java.io.File
import kotlin.math.roundToInt


class ImageFragment : ScreenFragment() {

    companion object {

        fun createFactory(image: Image): ScreenFragmentFactory<ImageFragment> {
            if (image.id < 0) {
                throw IllegalStateException("might collide with non-image id: ${image.id}")
            }
            return object:
                ScreenFragmentFactory<ImageFragment> {
                override val id: Long = image.id
                override fun createInstance(): ImageFragment {
                    return ImageFragment().apply {
                        arguments = Bundle().apply {
                            putLong(BUNDLE_KEY__IMAGE_ID, image.id)
                        }
                    }
                }
                override fun screenName(): String = "Image"
            }
        }

    }

    private val TAG = "ImageFragment"
    private val BUNDLE_KEY__IMAGE_ID = "imageId"

    private val faceDetectorOptions by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val imageId: Long = bundle!!.getLong(BUNDLE_KEY__IMAGE_ID)
        val imageDraweeView = this.imageDraweeView
        appViewModel.faceCount(imageId).observe(this, Observer { count ->
            this.faceCount.text = count.toString()
        })
        launch(Dispatchers.IO) {
            val image = appViewModel.findById(imageId)
            launch(Dispatchers.Main) {
                val uri = Uri.fromFile(File(image.file))
                Log.d(TAG, "updating image ${id} with: ${uri}")
                imageDraweeView.setImageURI(uri, null)
                imageDraweeView.setOnLongClickListener {
                    handleLongClick(image)
                    false // not consumed
                }
            }
            // https//firebase.google.com/docs/ml-kit/android/detect-faces suggests size to use.
            image.processBitmap(360, 480) { closeableReference ->
                Log.i(TAG, "decoded bitmap for image id: ${imageId}")
                // The IO thread has done it's work reading the Bitmap. Don't want to block this thread any more,
                // so clone the reference and hand it to Dispatcher.Default coroutine to do the CPU-intensive
                // face-detection work.
                val clonedReference = closeableReference.clone()
                launch(Dispatchers.Default) {
                    try {
                        Log.i(TAG, "detecting faces for image id: ${imageId}")
                        val bitmap = clonedReference.get()
                        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
                        val detector = FirebaseVision.getInstance().getVisionFaceDetector(faceDetectorOptions)
                        val task = detector.detectInImage(firebaseImage)
                        task.addOnSuccessListener { firebaseVisionFaces ->
                            Log.i(TAG, "detected ${firebaseVisionFaces.size} faces for image id: ${imageId}")
                            firebaseVisionFaces.forEach { firebaseVisionFace ->
                                Log.i(TAG, firebaseVisionFace.toString())
                            }
                            // TODO Refactor into some method.
                            val faces = firebaseVisionFaces.map { firebaseVisionFace ->
                                val firebaseVisionFaceContour = firebaseVisionFace.getContour(FirebaseVisionFaceContour.ALL_POINTS)
                                val coordinates: List<Coordinate> = firebaseVisionFaceContour.points.map { point ->
                                    Coordinate(0, 0, point.x.roundToInt(), point.y.roundToInt())
                                }
                                Face(0, imageId,  coordinates)
                            }
                            val processedImage = image.copy(processingStage = ProcessingStage.facesDetected)
                            appViewModel.addAll(processedImage, faces)
                            Log.i(TAG, "added ${faces.size} faces to database.")
                        }
                        task.addOnFailureListener {
                            e -> Log.e(TAG, "face detection failed", e)
                        }
                    } finally {
                        clonedReference.close()
                    }
                }
            }
        }
    }
    
    private fun handleLongClick(image: Image) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.confirmDeleteImage)
            setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                imageDraweeView.setOnLongClickListener { false }
                appViewModel.delete(image)
            }
            setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> }
            show()
        }
    }

}