package uk.me.jeremygreen.merging2.algo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.google.mlkit.vision.common.InputImage.fromBitmap
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection.getClient
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.me.jeremygreen.merging2.model.FaceWithCoordinates
import uk.me.jeremygreen.merging2.model.entity.Coordinate
import uk.me.jeremygreen.merging2.model.entity.Image
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FaceDetect {

    /**
     * Stored in [Image] table to indicate (1) that faces found and (2) which algorithm used to do this.
     * Change the value to force face detection to run again.
     */
    const val VERSION = 1L

    /** Width of [Bitmap] used by face-detection algorithm. */
    private const val BITMAP_WIDTH = 360

    /** Height of [Bitmap] used by face-detection algorithm. */
    private const val BITMAP_HEIGHT = 480

    private val FACE_DETECTOR_OPTIONS =
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

    internal suspend fun findFaces(
        image: Image,
        context: Context
    ): List<FaceWithCoordinates> {
        val bitmap = loadBitmap(context, image.uri)
        // Convert callback API into suspend function.
        return suspendCancellableCoroutine { continuation ->
            val rotationDegrees = 0
            val inputImage = fromBitmap(bitmap, rotationDegrees)
            val detector = getClient(FACE_DETECTOR_OPTIONS)
            val task = detector.process(inputImage)
            task.addOnSuccessListener { mlKitFaces ->
                continuation.resume(
                    facesWithCoordinates(mlKitFaces, bitmap, image)
                )
            }
            task.addOnFailureListener { e -> continuation.resumeWithException(e) }
            task.addOnCompleteListener { detector.close() }
        }
    }

    private suspend fun loadBitmap(context: Context, uri: Uri): Bitmap {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false) // Needed to get software Bitmap
            .size(BITMAP_WIDTH, BITMAP_HEIGHT)
            .build()

        val result = loader.execute(request)
        when (result) {
            is SuccessResult -> return result.image.toBitmap()
            is ErrorResult -> throw result.throwable
        }
    }

    private fun facesWithCoordinates(
        mlKitFaces: List<Face>,
        bitmap: Bitmap,
        image: Image
    ): List<FaceWithCoordinates> = mlKitFaces.map { mlKitFace ->
        val allContours = mlKitFace.allContours
        val coordinates: List<Coordinate> = allContours.flatMap { contour ->
            contour.points.map { point ->
                Coordinate(0, 0, point.x / bitmap.width, point.y / bitmap.height)
            }
        }
        FaceWithCoordinates(0, image.id, coordinates)
    }

}
