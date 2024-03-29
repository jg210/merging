package uk.me.jeremygreen.merging.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import uk.me.jeremygreen.merging.model.AppViewModel
import java.io.File

/**
 * Base class for the ViewPager2 Fragments in the screen package.
 */
abstract class ScreenFragment : Fragment(), CoroutineScope by MainScope() {

    protected lateinit var appViewModel: AppViewModel
    private lateinit var imagesDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        imagesDir = MainActivity.imagesDir(activity)
        imagesDir.mkdirs()
        appViewModel = AppViewModel.getInstance(requireActivity(), activity.application)
    }

    override fun onDestroyView() {
        cancel() // CoroutineScope
        super.onDestroyView()
    }

}
