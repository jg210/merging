package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import uk.me.jeremygreen.merging.model.ImageViewModel
import java.io.File

/**
 * Base class for the ViewPager2 Fragments in the screen package.
 */
abstract class ScreenFragment : Fragment() {

    protected lateinit var imageViewModel: ImageViewModel
    protected lateinit var imagesDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        imagesDir = MainActivity.imagesDir(activity)
        imagesDir.mkdirs()
        imageViewModel = ViewModelProviders.of(activity).get(ImageViewModel::class.java)
    }

}