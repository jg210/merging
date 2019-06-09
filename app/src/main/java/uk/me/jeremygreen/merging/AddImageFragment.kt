package uk.me.jeremygreen.merging

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.add_image_screen.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddImageFragment(val photoManager: PhotoManager) : ScreenFragment() {

    private val TAG = "AddImageFragment"
    private val REQUEST_TAKE_PHOTO = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_image_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addImageScreen.setOnClickListener { takePhoto() }
    }

    private fun takePhoto() {
        val intent = photoManager.createTakePhotoIntent()
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            photoManager.addImage()
        }
    }

}