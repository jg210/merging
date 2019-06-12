package uk.me.jeremygreen.merging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

class Image(
    private val imagesDir: File,
    /**
     * The id is used to persist the image to storage. Images can be deleted, so there can be
     * gaps in the ids. It is generally not the same as the position of the image within the UI.
     */
    val id: Int
) {

    companion object {
        val EXTENSION = ".jpg"
    }

    val file: File = File(imagesDir, "${id}${EXTENSION}")

    /**
     * It is only useful to call this for the highest-id Image when want to
     * store a new image.
     */
    fun nextImage(): Image {
        return Image(imagesDir, id + 1)
    }

    fun createTakePhotoIntent(context: Context): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(context.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val imageUri: Uri = FileProvider.getUriForFile(
            context,
            "uk.me.jeremygreen.merging.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return intent
    }

    fun exists() : Boolean {
        return file.exists()
    }

    override fun toString(): String {
        return file.path
    }

}