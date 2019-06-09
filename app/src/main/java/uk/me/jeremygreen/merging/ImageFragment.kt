package uk.me.jeremygreen.merging

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.image_screen.*
import java.io.File

class ImageFragment : ScreenFragment() {

    companion object {
        fun newInstance(file: File): ImageFragment {
            return ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_KEY__IMAGE_FILE, file.path)
                }
            }
        }
    }

    val BUNDLE_KEY__IMAGE_FILE = "imageFile"
    lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        file = File(bundle!!.getString(BUNDLE_KEY__IMAGE_FILE))
        return inflater.inflate(R.layout.image_screen, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        imageView.setOnLongClickListener {
            photoManager.removeImage(file)
            false // not consumed
        }
        // Can't load image until know its size, so postpone until after layout.
        imageView.addOnLayoutChangeListener(View.OnLayoutChangeListener() {
                _, _, _, _, _, _, _, _, _ ->
            val targetW: Int = imageView.width
            val targetH: Int = imageView.height
            val bmOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true // Decode, just getting bounds.
                BitmapFactory.decodeFile(file.path, this)
                val photoW: Int = outWidth
                val photoH: Int = outHeight
                val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)
                inJustDecodeBounds = false  // Decode again, fully.
                inSampleSize = scaleFactor
            }
            BitmapFactory.decodeFile(file.path, bmOptions)?.also { bitmap ->
                imageView.setImageBitmap(bitmap)
            }
        })
    }

}