package uk.me.jeremygreen.merging.main.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.main.ScreenFragment
import uk.me.jeremygreen.merging.main.ScreenFragmentFactory

class AddImageFragment : ScreenFragment() {

    private val TAG = "AddImageFragment"

    companion object: ScreenFragmentFactory<AddImageFragment> {
        override val id: Long = -2
        override fun createInstance(): AddImageFragment {
            return AddImageFragment()
        }
        override fun screenName(): String = "AddImage"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_image_screen, container, false)
    }

}