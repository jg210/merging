package uk.me.jeremygreen.merging.main

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import uk.me.jeremygreen.merging.model.Face

class FacesDrawable : Drawable() {

    var faces: List<Face> = listOf()

    // From Drawable
    override fun draw(canvas: Canvas) {
        val paint = Paint().apply {
            color = 0xFFFFFF // TODO get from android resource.
        }
        faces.forEach { face ->
            face.coordinates.forEach { coordinate ->
                canvas.drawPoint(coordinate.x * canvas.width, coordinate.y * canvas.height, paint)
            }
        }
    }

    // From Drawable
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    // From Drawable
    override fun setAlpha(alpha: Int) {}

    // From Drawable
    override fun setColorFilter(colorFilter: ColorFilter?) {}

}