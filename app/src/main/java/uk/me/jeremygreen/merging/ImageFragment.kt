package uk.me.jeremygreen.merging

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.net.Uri
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
        imageDraweeView.setOnLongClickListener {
            handleLongClick()
            false // not consumed
        }
        val uri = Uri.fromFile(file)
        imageDraweeView.setImageURI(uri, null)
    }

    fun handleLongClick() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.confirm_delete_photo)
            setPositiveButton(R.string.ok, DialogInterface.OnClickListener(function = ::handleRemoveImage))
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(function = ::handleRemoveImageCancel))
            show()
        }
    }

    fun handleRemoveImage(
        @Suppress("UNUSED_PARAMETER") dialog: DialogInterface,
        @Suppress("UNUSED_PARAMETER") which: Int
    ) {
        photoManager.removeImage(file)
    }

    fun handleRemoveImageCancel(
        @Suppress("UNUSED_PARAMETER") dialog: DialogInterface,
        @Suppress("UNUSED_PARAMETER") which: Int) {
        // Empty
    }

}