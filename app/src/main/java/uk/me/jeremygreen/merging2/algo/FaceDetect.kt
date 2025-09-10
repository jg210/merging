package uk.me.jeremygreen.merging2.algo

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.SingletonImageLoader
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
import kotlinx.coroutines.tasks.await
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
        application: Application
    ): List<FaceWithCoordinates> {
        val bitmap = loadBitmap(application, image.uri)
        val rotationDegrees = 0
        val inputImage = fromBitmap(bitmap, rotationDegrees)
        getClient(FACE_DETECTOR_OPTIONS).use { detector ->
            val mlKitFaces = detector.process(inputImage).await()
            return facesWithCoordinates(mlKitFaces, bitmap, image)
        }
    }

    private suspend fun loadBitmap(context: Application, uri: Uri): Bitmap {
        // Forcing context to be an Application reduces chance of leaking an
        // Activity Context.
        val imageLoader = SingletonImageLoader.get(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false) // Needed to get software Bitmap
            .size(BITMAP_WIDTH, BITMAP_HEIGHT)
            .build()

        val result = imageLoader.execute(request)
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
