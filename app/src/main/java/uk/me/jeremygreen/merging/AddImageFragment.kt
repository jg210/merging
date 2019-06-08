package uk.me.jeremygreen.merging

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.add_image_screen.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddImageFragment : ScreenFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_image_screen, container, false)
        //addImageScreen.setOnClickListener { takePhoto }
        return view
    }



//    private val REQUEST_TAKE_PHOTO = 1
//    private val PICTURES: String = Environment.DIRECTORY_PICTURES
//    var currentPhotoPath: String? = null


//    private fun takePhoto() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.resolveActivity(packageManager)?.also {
//            val photoFile: File? = try {
//                createImageFile()
//            } catch (ex: IOException) {
//                null
//            }
//            photoFile?.also {
//                val photoURI: Uri = FileProvider.getUriForFile(
//                    this,
//                    "uk.me.jeremygreen.merging.fileprovider",
//                    it
//                )
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
//            }
//        }
//    }

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_",
//            ".jpg",
//            storageDir
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
//            setImage()
//        }
//    }

}