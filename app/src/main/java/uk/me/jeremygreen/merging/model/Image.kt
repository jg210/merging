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
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapReferenceDataSubscriber
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.common.RotationOptions
import java.io.File
import java.lang.NullPointerException

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

    val file: String

) {

    @delegate:Ignore
    val uri: Uri by lazy { Uri.fromFile(File(this.file)) }

   inline fun processBitmap(
       width: Int,
       height: Int,
       /**
        * https://frescolib.org/docs/closeable-references.html
        */
       crossinline callback: (CloseableReference<Bitmap>) -> Unit) {

       val decodeOptions = ImageDecodeOptions.newBuilder().build()
       val rotationOptions = RotationOptions.autoRotate()
       val imageRequest = ImageRequestBuilder
           .newBuilderWithSource(this.uri)
           .setImageDecodeOptions(decodeOptions)
           .setRotationOptions(rotationOptions)
           .setLocalThumbnailPreviewsEnabled(true)
           .setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH)
           .setProgressiveRenderingEnabled(false)
           .setResizeOptions(ResizeOptions(width, height))
           .build()
       val imagePipeline = Fresco.getImagePipeline()
       val callerContext = null
        val dataSource: DataSource<CloseableReference<CloseableImage>> =
            imagePipeline.fetchDecodedImage(imageRequest, callerContext)
       val dataSubscriber: DataSubscriber<CloseableReference<CloseableImage>> =
          object: BaseBitmapReferenceDataSubscriber() {
              override fun onNewResultImpl(bitmapReference: CloseableReference<Bitmap>?) {
                  if (bitmapReference == null) {
                      throw NullPointerException()
                  }
                  callback(bitmapReference)
              }
              override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
                  throw RuntimeException()
              }
          }
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());
   }

}