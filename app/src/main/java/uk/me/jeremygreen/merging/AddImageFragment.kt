package uk.me.jeremygreen.merging

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.add_image_screen.*

class AddImageFragment : ScreenFragment() {

    private val TAG = "AddImageFragment"
    private val REQUEST_TAKE_PHOTO = 1

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
        val intent = imageManager.createTakePhotoIntent()
        if (intent == null) {
            Log.w(TAG, "No camera application available.")
        } else {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            imageManager.addImage()
        }
    }

}