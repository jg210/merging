package uk.me.jeremygreen.merging.main

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import com.facebook.drawee.view.SimpleDraweeView
import uk.me.jeremygreen.merging.model.Face
import kotlin.properties.Delegates

class FacesView : SimpleDraweeView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    private val TAG = "FacesView"
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE // TODO get from android resource.
        style = Paint.Style.FILL
    }

    var faces: List<Face> by Delegates.observable(listOf()) { _, old, new ->
        if (old != new) {
            this.invalidate()
        }
    }

    private val facesDrawable = object : Drawable() {
        override fun draw(canvas: Canvas) {
            drawFaces(canvas)
        }
        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }
        override fun setColorFilter(colorFilter: ColorFilter?) {}
        override fun setAlpha(alpha: Int) {}
    }

    override fun onAttach() {
        super.onAttach()
        this.hierarchy.setOverlayImage(this.facesDrawable)
    }

    private fun drawFaces(canvas: Canvas) {
        val radius = 3
        val bounds = RectF()
        this.hierarchy.getActualImageBounds(bounds)
        faces.forEach { face ->
            Log.d(TAG, "drawing face contours for face id: ${face.id}")
            face.coordinates.forEach { coordinate ->
                val x = bounds.left + coordinate.x * bounds.width()
                val y = bounds.top + coordinate.y * bounds.height()
                //Log.d(TAG, "drawing point at (${x}, ${y})")
                canvas.drawOval(x - radius, y - radius, x + radius, y + radius, paint)
            }
        }
    }

}