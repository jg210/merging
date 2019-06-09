package uk.me.jeremygreen.merging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoManager(
    val context: Context,
    val photosDir: File) {

    private val TAG = "PhotoManager"
    private var currentPhotoFile: File? = null

    init {
        photosDir.mkdirs()
    }

    fun createTakePhotoIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(context.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val photoFile: File = try {
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            photosDir
        )
        currentPhotoFile = file
        return file
    }

    fun addImage() {
        if (currentPhotoFile == null) {
            throw IllegalStateException("addImage() called unexpectedly")
        }
        Log.i(TAG, "adding image: ${currentPhotoFile}")
        currentPhotoFile = null
    }

}