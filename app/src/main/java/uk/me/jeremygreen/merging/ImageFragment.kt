package uk.me.jeremygreen.merging

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.image_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val TAG = "ImageFragment"
    private val BUNDLE_KEY__IMAGE_ID = "imageId"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val imageId: Long = bundle!!.getLong(BUNDLE_KEY__IMAGE_ID)
        val imageDraweeView = this.imageDraweeView
        GlobalScope.launch(Dispatchers.IO) {
            val image = imageViewModel.findById(imageId)
            withContext(Dispatchers.Main) {
                val uri = Uri.fromFile(File(image.file))
                Log.d(TAG, "updating image ${id} with: ${uri}")
                imageDraweeView.setImageURI(uri, null)
                imageDraweeView.setOnLongClickListener {
                    handleLongClick(image)
                    false // not consumed
                }
            }
        }
    }

    private fun handleLongClick(image: Image) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.confirmDeleteImage)
            setPositiveButton(R.string.ok, DialogInterface.OnClickListener(function = { _: DialogInterface, _: Int ->
                imageDraweeView.setOnLongClickListener { false }
                GlobalScope.launch(Dispatchers.IO) {
                    imageViewModel.delete(image)
                }
            }))
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(function = ::handleRemoveImageCancel))
            show()
        }
    }

    fun handleRemoveImageCancel(
        @Suppress("UNUSED_PARAMETER") dialog: DialogInterface,
        @Suppress("UNUSED_PARAMETER") which: Int) {
        // Empty
    }

}