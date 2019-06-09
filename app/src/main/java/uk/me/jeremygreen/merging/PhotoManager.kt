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

class PhotoManager(
    val context: Context,
    val photosDir: File) {

    private val TAG = "PhotoManager"
    private val EXTENSION = ".jpg"
    val photos: List<File>
        get() {
            return photosDir.listFiles().sorted().filter { file ->
                file.name.endsWith(EXTENSION)
            }
        }
    private val changeListeners: MutableList<PhotoManager.ChangeListener> = mutableListOf()
    private var currentPhotoFile: File? = null

    init {
        photosDir.mkdirs()
    }

    fun addChangeListener(listener: PhotoManager.ChangeListener) {
        changeListeners.add(listener)
    }

    fun createTakePhotoIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(context.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            return null
        }
        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "uk.me.jeremygreen.merging.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        return intent
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        var nextIndex = 0
        photos.forEach { file ->
            val index = Integer.parseInt(file.nameWithoutExtension)
            if (index >= nextIndex) {
                nextIndex = index + 1
            }
        }
        val photoFile = File(photosDir, "${nextIndex}.jpg")
        currentPhotoFile = photoFile
        return photoFile
    }

    fun addImage() {
        if (currentPhotoFile == null) {
            throw IllegalStateException("addImage() called unexpectedly")
        }
        Log.i(TAG, "adding image: ${currentPhotoFile}")
        currentPhotoFile = null
        notifiyListeners()
    }

    private fun notifiyListeners() {
        val currentPhotos = photos
        changeListeners.forEach { listener ->
            listener.onPhotosChange(currentPhotos)
        }
    }

    fun removeImage(file: File) {
        if (file.parentFile.canonicalFile != photosDir.canonicalFile) {
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
        fun onPhotosChange(files: List<File>)
    }

}