package uk.me.jeremygreen.merging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException

class ImageManager(
    private val context: Context,
    private val imagesDir: File = File(context.filesDir, "photos")
) {

    private val TAG = "ImageManager"
    private val EXTENSION = ".jpg"
    val images: List<File>
        get() {
            return imagesDir.
                listFiles().
                filter { file -> file.name.endsWith(EXTENSION) }.
                sortedBy { file -> Integer.parseInt(file.nameWithoutExtension) }
        }
    private val changeListeners: MutableList<ImageManager.ChangeListener> = mutableListOf()
    private var currentImageFile: File? = null

    init {
        imagesDir.mkdirs()
    }

    fun addChangeListener(listener: ImageManager.ChangeListener) {
        changeListeners.add(listener)
    }

    fun createTakePhotoIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(context.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val imageFile = try {
            createImageFile()
        } catch (ex: IOException) {
            return null
        }
        val imageUri: Uri = FileProvider.getUriForFile(
            context,
            "uk.me.jeremygreen.merging.fileprovider",
            imageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return intent
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val lastFile = images.lastOrNull()
        val nextIndex = if (lastFile == null) {
            0
        } else {
            Integer.parseInt(lastFile.nameWithoutExtension) + 1
        }
        val imageFile = File(imagesDir, "${nextIndex}.jpg")
        currentImageFile = imageFile
        return imageFile
    }

    fun addImage() {
        if (currentImageFile == null) {
            throw IllegalStateException("addImage() called unexpectedly")
        }
        Log.i(TAG, "adding image: ${currentImageFile}")
        currentImageFile = null
        notifiyListeners()
    }

    private fun notifiyListeners() {
        val currentImages = images
        changeListeners.forEach { listener ->
            listener.onImagesChange(currentImages)
        }
    }

    fun removeImage(file: File) {
        if (file.parentFile.canonicalFile != imagesDir.canonicalFile) {
            throw IllegalArgumentException(file.path)
        }
        if (!file.delete()) {
            if (file.exists()) {
                throw IOException("failed to delete: ${file.path}")
            } else {
                throw IOException("already absent: ${file.path}")
            }
        }
        notifiyListeners()
    }

    interface ChangeListener {
        fun onImagesChange(files: List<File>)
    }

}