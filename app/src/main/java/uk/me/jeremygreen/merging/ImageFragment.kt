package uk.me.jeremygreen.merging

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.image_screen.*
import java.io.File

class ImageFragment : ScreenFragment() {

    companion object {
        fun newInstance(imageId: Long): ImageFragment {
            return ImageFragment().apply {
                arguments = Bundle().apply {
                    putLong(BUNDLE_KEY__IMAGE_ID, imageId)
                }
            }
        }
    }

    val BUNDLE_KEY__IMAGE_ID = "imageId"
    lateinit var image: Image

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        val imageId: Long = bundle!!.getLong(BUNDLE_KEY__IMAGE_ID)
        image = imageViewModel.findById(imageId)
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
        val uri = Uri.fromFile(File(image.file))
        imageDraweeView.setImageURI(uri, null)
    }

    fun handleLongClick() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.confirmDeleteImage)
            setPositiveButton(R.string.ok, DialogInterface.OnClickListener(function = ::handleRemoveImage))
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(function = ::handleRemoveImageCancel))
            show()
        }
    }

    fun handleRemoveImage(
        @Suppress("UNUSED_PARAMETER") dialog: DialogInterface,
        @Suppress("UNUSED_PARAMETER") which: Int
    ) {
        imageDraweeView.setOnLongClickListener { false }
        imageViewModel.delete(image)
    }

    fun handleRemoveImageCancel(
        @Suppress("UNUSED_PARAMETER") dialog: DialogInterface,
        @Suppress("UNUSED_PARAMETER") which: Int) {
        // Empty
    }

}