package uk.me.jeremygreen.merging.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.datasource.BaseBitmapReferenceDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File

@Entity(tableName = "images")
data class Image(

    /**
     * This database table stores id -> file mappings. It's not possible to
     * just use the set of stored files since the UI can still get updates
     * after deciding to delete the image, but before it has been removed
     * from the screen.
     * 
     * Use autoGenerate=true since rely on ordering by primary key
     * to time order the photos. Could use UTC timestamp instead,
     * but can't rely on it being correct.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val file: String,

    /**
     * [ProcessingStage]
     */
    val processingStage: Int

) {

    @delegate:Ignore
    val uri: Uri by lazy { Uri.fromFile(File(this.file)) }

    inline fun processBitmap(
        width: Int,
        height: Int,
        /**
         * https://frescolib.org/docs/closeable-references.html
         */
        crossinline callback: (CloseableReference<Bitmap>) -> Unit
    ) {

        val decodeOptions = ImageDecodeOptions.newBuilder().build()
        val rotationOptions = RotationOptions.autoRotate()
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(this.uri)
            .setImageDecodeOptions(decodeOptions)
            .setRotationOptions(rotationOptions)
            .setLocalThumbnailPreviewsEnabled(false)
            .setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH)
            .setProgressiveRenderingEnabled(false)
            .setResizeOptions(ResizeOptions(width, height))
            .build()
        val imagePipeline = Fresco.getImagePipeline()
        val callerContext = null
        val dataSource: DataSource<CloseableReference<CloseableImage>> =
            imagePipeline.fetchDecodedImage(imageRequest, callerContext)
        val dataSubscriber: DataSubscriber<CloseableReference<CloseableImage>> =
            object : BaseBitmapReferenceDataSubscriber() {

                override fun onNewResultImpl(bitmapReference: CloseableReference<Bitmap>?) {
                    checkNotNull(bitmapReference) { "null bitmapReference" }
                    callback(bitmapReference)
                }

                override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                    error("DataSource failure")
                }

            }
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance())
    }

    /**
     * Find faces, decrement Bitmap reference count, invoke appropriate callback.
     */
    inline fun findFaces(
        closeableReference: CloseableReference<Bitmap>,
        faceDetectorOptions: FaceDetectorOptions,
        crossinline onError: (Exception) -> Unit,
        crossinline onSuccess: (List<Face>) -> Unit
    ) {
        val rotationDegrees = 0
        val bitmap = closeableReference.get()
        val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)
        val detector = FaceDetection.getClient(faceDetectorOptions)
        val task = detector.process(inputImage)
        val onProcessingComplete = {
            closeableReference.use {
                detector.close()
            }
        }
        task.addOnSuccessListener { mlKitFaces ->
            onProcessingComplete()
            val faces = mlKitFaces.map { mlKitFace ->
                val allContours = mlKitFace.allContours
                val coordinates: List<Coordinate> = allContours.flatMap { contour ->
                    contour.points.map { point ->
                        Coordinate(0, 0, point.x / bitmap.width, point.y / bitmap.height)
                    }
                }
                Face(0, this.id, coordinates)
            }
            onSuccess(faces)
        }
        task.addOnFailureListener { e ->
            onProcessingComplete()
            onError(e)
        }
    }

}
