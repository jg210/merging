package uk.me.jeremygreen.merging.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.model.Face
import uk.me.jeremygreen.merging.model.Image
import java.io.File
import kotlin.properties.Delegates

class FacesView : SimpleDraweeView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val TAG = "FacesView"
        private const val FACE_DOT_RADIUS = 3
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(R.color.imageOverlay, context.theme)
        } else {
            @Suppress("DEPRECATION") // No other API if target API 21.
            resources.getColor(R.color.imageOverlay)
        }
        style = Paint.Style.FILL
    }

    private var imageLoadingComplete: Boolean = false
    private var imageSet: Boolean = false

    var faces: List<Face> by Delegates.observable(listOf()) { _, _, new ->
        val newIds = new.map { face -> face.id }
        Log.d(TAG, "faces set to: ${newIds}")
        // TODO Room sends Faces with no coordinates before sending them with coordinates, despite use of transactions.
        this.facesDrawable.invalidateSelf()
    }

    private val facesDrawable = object : Drawable() {
        override fun draw(canvas: Canvas) {
            drawFaces(canvas)
        }
        @Deprecated("Deprecated in Java",
            ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat")
        )
        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }
        override fun setColorFilter(colorFilter: ColorFilter?) {}
        override fun setAlpha(alpha: Int) {}
    }

    fun setImage(
        image: Image,
        longClickListener: (image: Image) -> Unit
    ) {
        check(!this.imageSet) {"cannot call setImage() twice - ${image.id}" }
        if (this.imageLoadingComplete) {
            throw AssertionError()
        }
        this.imageSet = true
        val uri = Uri.fromFile(File(image.file))
        Log.d(TAG, "updating image ${id} with: ${uri}")
        val listener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                Log.d(TAG, "image loading complete: ${image.id}")
                this@FacesView.imageLoadingComplete = true
                this@FacesView.facesDrawable.invalidateSelf()
            }
        }
        this.controller = Fresco.newDraweeControllerBuilder()
            .setUri(uri)
            .setOldController(this.controller)
            .setControllerListener(listener)
            .build()
        this.setOnLongClickListener {
            longClickListener(image)
            false // not consumed
        }
    }

    override fun onAttach() {
        super.onAttach()
        this.hierarchy.setOverlayImage(this.facesDrawable)
    }

    private fun drawFaces(canvas: Canvas) {
        if (!this.imageLoadingComplete) {
            // Need to have loaded image for bounds to be correct.
            Log.d(TAG, "not drawing faces since image loading not complete.")
            return
        }
        val bounds = RectF()
        this.hierarchy.getActualImageBounds(bounds)
        Log.d(TAG, "drawFaces() bounds: ${bounds.toShortString()}")
        this.faces.forEach { face ->
            Log.d(TAG, "drawing face contours for face id: ${face.id}")
            face.coordinates.forEach { coordinate ->
                val x = bounds.left + coordinate.x * bounds.width()
                val y = bounds.top + coordinate.y * bounds.height()
                //Log.d(TAG, "drawing point at (${x}, ${y})")
                canvas.drawOval(
                    x - FACE_DOT_RADIUS,
                    y - FACE_DOT_RADIUS,
                    x + FACE_DOT_RADIUS,
                    y + FACE_DOT_RADIUS, paint
                )
            }
        }
    }

}