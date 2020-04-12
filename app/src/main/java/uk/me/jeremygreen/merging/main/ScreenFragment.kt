package uk.me.jeremygreen.merging.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
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
        appViewModel = ViewModelProvider(activity).get(AppViewModel::class.java)
    }

    override fun onDestroyView() {
        cancel() // CoroutineScope
        super.onDestroyView()
    }

}