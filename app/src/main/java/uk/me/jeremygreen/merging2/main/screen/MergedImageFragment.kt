package uk.me.jeremygreen.merging2.main.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.me.jeremygreen.merging2.R
import uk.me.jeremygreen.merging2.main.ScreenFragment
import uk.me.jeremygreen.merging2.main.ScreenFragmentFactory

internal class MergedImageFragment : ScreenFragment() {

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
