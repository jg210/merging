package uk.me.jeremygreen.merging

import android.content.Context
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException

class ImageManager(
    private val context: Context,
    private val imagesDir: File = File(context.filesDir, "photos")
) {

    private val TAG = "ImageManager"
    val images: List<Image>
        get() {
            return imagesDir.
                listFiles().
                filter { file -> file.name.endsWith(Image.EXTENSION) }.
                map { file -> Integer.parseInt(file.nameWithoutExtension) }.
                sorted().
                map { id -> Image(imagesDir, id) }
        }

    private val changeListeners: MutableList<ImageManager.ChangeListener> = mutableListOf()

    init {
        imagesDir.mkdirs()
    }

    fun imageForId(id: Int): Image {
        val image = Image(imagesDir, id)
        if (!image.exists()) {
            throw java.lang.AssertionError("image doesn't exist: ${image}")
        }
        return image
    }

    fun addChangeListener(listener: ImageManager.ChangeListener) {
        changeListeners.add(listener)
    }

    fun nextImage(): Image {
        val lastImage = images.lastOrNull()
        return if (lastImage == null) {
            Image(imagesDir,0)
        } else {
            lastImage.nextImage()
        }
    }

    fun notifiyListeners() {
        val currentImages = images
        changeListeners.forEach { listener ->
            listener.onImagesChange(currentImages)
        }
    }

    fun remove(image: Image) {
        if (image.file.parentFile.canonicalFile != imagesDir.canonicalFile) {
            throw AssertionError(image.file.path)
        }
        if (!image.file.delete()) {
            if (image.file.exists()) {
                throw IOException("failed to delete: ${image.file.path}")
            } else {
                throw IOException("already absent: ${image.file.path}")
            }
        }
        notifiyListeners()
    }

    interface ChangeListener {
        fun onImagesChange(images: List<Image>)
    }

}