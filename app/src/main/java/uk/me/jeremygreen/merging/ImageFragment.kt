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
import uk.me.jeremygreen.merging.model.Image
import java.io.File

class ImageFragment : ScreenFragment() {

    companion object {

        fun createFactory(image: Image): ScreenFragmentFactory<ImageFragment> {
            if (image.id < 0) {
                throw IllegalStateException("might collide with non-image id: ${image.id}")
            }
            return object: ScreenFragmentFactory<ImageFragment> {
                override val id: Long = image.id
                override fun createInstance(): ImageFragment {
                    return ImageFragment().apply {
                        arguments = Bundle().apply {
                            putLong(BUNDLE_KEY__IMAGE_ID, image.id)
                        }
                    }
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
            setPositiveButton(R.string.ok, { _: DialogInterface, _: Int ->
                imageDraweeView.setOnLongClickListener { false }
                GlobalScope.launch(Dispatchers.IO) {
                    imageViewModel.delete(image)
                }
            })
            setNegativeButton(R.string.cancel, { _: DialogInterface, _: Int -> })
            show()
        }
    }

}