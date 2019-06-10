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
import java.lang.AssertionError
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
                // Could leave Fragments that haven't moved in the MutableSet, but
                // it's easier to just remove them all, so that all the Fragments
                // are recreated.
                fragments.clear()
                // This must be called whenever getCount() will return a new value.
                notifyDataSetChanged()
            }
        }

        private val fragments: MutableSet<Fragment> = mutableSetOf()

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
            val fragment: Fragment
            if (position < photos.size) {
                fragment = ImageFragment.newInstance(photos[position])
            } else {
                fragment = AddImageFragment()
            }
            if (!fragments.add(fragment)) {
                throw AssertionError("already present: ${fragment}")
            }
            return fragment
        }

        // From PagerAdapter
        override fun getItemPosition(obj: Any): Int {
            // Need to return POSITION_NONE in general, otherwise deleted photos aren't removed from the ViewPager.
            val position: Int
            if (obj is Fragment && fragments.contains(obj)) {
                position = FragmentStatePagerAdapter.POSITION_UNCHANGED
            } else {
                position = FragmentStatePagerAdapter.POSITION_NONE
            }
            Log.d(TAG, "getItemPosition(${obj}) = ${position}")
            return position
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = super.instantiateItem(container, position)
            Log.d(TAG, "instantiateItem(${position}) = ${item}")
            return item
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            val result = super.isViewFromObject(view, obj)
            Log.d(TAG, "isViewFromObject(${view},${obj}) = ${result}")
            return result
        }

    } // ScreenPagerAdapter

} // MainActivity
