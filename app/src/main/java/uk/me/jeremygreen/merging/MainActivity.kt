package uk.me.jeremygreen.merging

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val pagerAdapter = ScreenPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
    }

    private class ScreenPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        private val imageCount = 1;

        override fun getCount(): Int {
            return imageCount + 1;
        }

        override fun getItem(position: Int): Fragment {
            return when {
                position <= imageCount - 1 -> ImageFragment(position)
                else -> AddImageFragment()
            }
        }

    }

} // MainActivity
