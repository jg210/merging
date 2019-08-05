package uk.me.jeremygreen.merging.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private val TAG = "FacesDrawable"
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

    // From Drawable
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = 3
        faces.forEach { face ->
            Log.d(TAG, "drawing face contours for face id: ${face.id}")
            face.coordinates.forEach { coordinate ->
                val x = coordinate.x * canvas.width
                val y = coordinate.y * canvas.height
                //Log.d(TAG, "drawing point at (${x}, ${y})")
                canvas.drawOval(x - radius, y - radius, x + radius, y + radius, paint)
            }
        }
    }

}