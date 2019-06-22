package uk.me.jeremygreen.merging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MergedImageFragment : ScreenFragment() {

    private val TAG = "MergedImageFragment"

    companion object: ScreenFragmentFactory<MergedImageFragment> {
        override val id: Long = -3
        override fun createInstance(): MergedImageFragment {
            return MergedImageFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.merged_image_screen, container, false)
    }

}