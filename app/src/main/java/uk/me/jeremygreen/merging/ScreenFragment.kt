package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.io.File


abstract class ScreenFragment : Fragment() {

    protected val imagesDir: File by lazy {
        File(requireContext().filesDir, "photos")
    }

    protected lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagesDir.mkdirs()
        imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }

}