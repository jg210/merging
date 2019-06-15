package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : FragmentActivity() {

    private val imageDao: ImageDao by lazy { ImageDao(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val pagerAdapter = PagerAdapterImpl(this, imageDao)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 2
        imageDao.addChangeListener(pagerAdapter)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is ScreenFragment) {
            fragment.imageDao = imageDao
        }
    }

} // MainActivity
