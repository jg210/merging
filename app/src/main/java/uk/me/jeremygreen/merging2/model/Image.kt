package uk.me.jeremygreen.merging2.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File

@Entity(tableName = "images")
internal data class Image(

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

    val file: String

) {

    @delegate:Ignore
    val uri: Uri by lazy { Uri.fromFile(File(this.file)) }


    /**
     * Find faces in the bitmap, then invoke appropriate callback.
     */
    inline fun findFaces(
        bitmap: Bitmap,
        faceDetectorOptions: FaceDetectorOptions,
        crossinline onError: (Exception) -> Unit,
        crossinline onSuccess: (List<FaceWithCoordinates>) -> Unit
    ) {
        val rotationDegrees = 0
        val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)
        val detector = FaceDetection.getClient(faceDetectorOptions)
        val task = detector.process(inputImage)
        val onProcessingComplete = {
            detector.close()
        }
        task.addOnSuccessListener { mlKitFaces ->
            onProcessingComplete()
            val facesWithCoordinates = mlKitFaces.map { mlKitFace ->
                val allContours = mlKitFace.allContours
                val coordinates: List<Coordinate> = allContours.flatMap { contour ->
                    contour.points.map { point ->
                        Coordinate(0, 0, point.x / bitmap.width, point.y / bitmap.height)
                    }
                }
                FaceWithCoordinates(0, this.id, coordinates)
            }
            onSuccess(facesWithCoordinates)
        }
        task.addOnFailureListener { e ->
            onProcessingComplete()
            onError(e)
        }
    }

}
