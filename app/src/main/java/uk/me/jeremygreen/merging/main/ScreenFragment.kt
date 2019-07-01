package uk.me.jeremygreen.merging.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import uk.me.jeremygreen.merging.model.ImageViewModel
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Base class for the ViewPager2 Fragments in the screen package.
 */
abstract class ScreenFragment : Fragment(), CoroutineScope by MainScope() {

    protected lateinit var imageViewModel: ImageViewModel
    private lateinit var imagesDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        imagesDir = MainActivity.imagesDir(activity)
        imagesDir.mkdirs()
        imageViewModel = ViewModelProviders.of(activity).get(ImageViewModel::class.java)
    }

    override fun onDestroyView() {
        cancel() // CoroutineScope
        super.onDestroyView()
    }

}