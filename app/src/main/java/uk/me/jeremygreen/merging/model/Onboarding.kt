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

/**
 * This table stores a set of accepted onboarding-text versions.
 */
@Entity(tableName = "onboarding")
data class Onboarding(

    @PrimaryKey
    val version: Long

)