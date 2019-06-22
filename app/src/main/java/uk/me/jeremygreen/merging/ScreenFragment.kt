package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import uk.me.jeremygreen.merging.model.ImageViewModel
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Base class for the ViewPager2 Fragments in the screen package.
 */
abstract class ScreenFragment : Fragment(), CoroutineScope {

    protected lateinit var imageViewModel: ImageViewModel
    protected lateinit var imagesDir: File

    // CoroutineScope: configure default CouroutineContext.
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

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