package uk.me.jeremygreen.merging

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import kotlin.properties.Delegates

class MainActivity : FragmentActivity() {

    private val TAG = "MainActivity"

    private val photoManager: PhotoManager by lazy { PhotoManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val pagerAdapter = ScreenPagerAdapter(supportFragmentManager, photoManager)
        pager.adapter = pagerAdapter
        photoManager.addChangeListener(pagerAdapter)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        if (fragment is ScreenFragment) {
            fragment.photoManager = photoManager
        }
    }

    private inner class ScreenPagerAdapter(
        fragmentManager: FragmentManager,
        val photoManager: PhotoManager
    ) : FragmentStatePagerAdapter(fragmentManager),
        PhotoManager.ChangeListener {

        var photos: List<File>  by Delegates.observable(photoManager.photos) { _, old, new ->
            if (old != new) {
                Log.d(TAG, "notfifyDataSetChanged()")
                // This must be called whenever getCount() will return a new value.
                notifyDataSetChanged()
            }
        }

        // From PhotoManager.ChangeListener
        override fun onPhotosChange(files: List<File>) {
            Log.d(TAG, "onPhotosChange(${files.size} photos)")
            photos = files
        }

        // From PagerAdapter
        override fun getCount(): Int {
            val count = photos.size + 1
            Log.d(TAG, "getCount() = ${count}")
            return count;
        }

        // From PagerAdapter
        override fun getItem(position: Int): Fragment {
            Log.d(TAG, "getItem(${position})")
            if (position < photos.size) {
                return ImageFragment.newInstance(photos[position])
            } else {
                return AddImageFragment()
            }
        }

        // From PagerAdapter
        override fun getItemPosition(`object`: Any): Int {
            // Need to return POSITION_NONE in general, otherwise deleted photos aren't removed from the ViewPager.
            // TODO for efficiency, only return POSITION_NONE if necessary.
            // ...https://stackoverflow.com/questions/10849552/update-viewpager-dynamically/10852046#10852046
            val position = FragmentStatePagerAdapter.POSITION_NONE
            Log.d(TAG, "getItemPosition(${`object`}) = ${position}")
            return position
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = super.instantiateItem(container, position)
            Log.d(TAG, "instantiateItem(${position}) = ${item}")
            return item
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            val result = super.isViewFromObject(view, `object`)
            Log.d(TAG, "isViewFromObject(${view},${`object`}) = ${result}")
            return result
        }

    } // ScreenPagerAdapter

} // MainActivity
