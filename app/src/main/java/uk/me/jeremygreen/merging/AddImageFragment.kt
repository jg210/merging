package uk.me.jeremygreen.merging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AddImageFragment : ScreenFragment() {

    private val TAG = "AddImageFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.merged_image_screen, container, false)
    }

}