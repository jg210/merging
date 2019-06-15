package uk.me.jeremygreen.merging

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.add_image_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.AssertionError
import java.util.*

class AddImageFragment : ScreenFragment() {

    private val TAG = "AddImageFragment"
    private val REQUEST_TAKE_PHOTO = 1
    private var file: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_image_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addImageScreen.setOnClickListener { handleTakePhoto() }
    }

    private fun handleTakePhoto() {
        val context = requireContext()
        val intent = createTakePhotoIntent(context)
        if (intent == null) {
            Log.w(TAG, "No camera application available.")
        } else {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    fun createTakePhotoIntent(context: Context): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(context.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val uuid = UUID.randomUUID()
        val file = File(imagesDir, "${uuid}.jpg")
        val imageUri: Uri = FileProvider.getUriForFile(
            context,
            "uk.me.jeremygreen.merging.fileprovider",
            file
        )
        this.file = file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            val file = file
            if (file == null) {
                throw AssertionError()
            }
            GlobalScope.launch(Dispatchers.IO) {
                imageViewModel.addImage(file.path)
            }
        }
    }

}