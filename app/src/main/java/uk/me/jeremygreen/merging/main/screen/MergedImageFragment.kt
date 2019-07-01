package uk.me.jeremygreen.merging.main.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.main.ScreenFragment
import uk.me.jeremygreen.merging.main.ScreenFragmentFactory

class MergedImageFragment : ScreenFragment() {

    private val TAG = "MergedImageFragment"

    companion object: ScreenFragmentFactory<MergedImageFragment> {
        override val id: Long = -3
        override fun createInstance(): MergedImageFragment {
            return MergedImageFragment()
        }
        override fun screenName(): String = "MergedImage"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.merged_image_screen, container, false)
    }

}