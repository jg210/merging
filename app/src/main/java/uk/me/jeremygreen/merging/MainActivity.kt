package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : FragmentActivity() {

    private val imageManager: ImageManager by lazy { ImageManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val pagerAdapter = ScreenPagerAdapter(supportFragmentManager, imageManager)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 2
        imageManager.addChangeListener(pagerAdapter)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        if (fragment is ScreenFragment) {
            fragment.imageManager = imageManager
        }
    }

} // MainActivity
