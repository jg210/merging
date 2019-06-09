package uk.me.jeremygreen.merging

import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val photosDir = File(filesDir, "photos")
        val photoManager = PhotoManager(this, photosDir);
        Log.i(TAG, "photo-manager photo count: ${photoManager.photos.size}")
        setContentView(R.layout.content_main)
        val pagerAdapter = ScreenPagerAdapter(supportFragmentManager, photoManager)
        pager.adapter = pagerAdapter
        photoManager.addChangeListener(pagerAdapter)
    }

    private inner class ScreenPagerAdapter(
        fragmentManager: FragmentManager,
        val photoManager: PhotoManager
    ) : FragmentStatePagerAdapter(fragmentManager),
        PhotoManager.ChangeListener {

        var photos: List<File>  by Delegates.observable(photoManager.photos) { _, old, new ->
            if (old != new) {
                // This must be called whenever getCount() will return a new value.
                notifyDataSetChanged()
            }
        }

        // From PhotoManager.ChangeListener
        override fun onPhotosChange(files: List<File>) {
            photos = files
        }

        // From PagerAdapter
        override fun getCount(): Int {
            return photos.size + 1;
        }

        // From PagerAdapter
        override fun getItem(position: Int): Fragment {
            if (position < photos.size) {
                return ImageFragment(photoManager, photos[position])
            } else {
                return AddImageFragment(photoManager)
            }
        }

        // From PagerAdapter
        override fun getItemPosition(`object`: Any): Int {
            // Need to return POSITION_NONE in general, otherwise deleted photos aren't removed from the ViewPager.
            // TODO for efficiency, only return POSITION_NONE if necessary.
            // ...https://stackoverflow.com/questions/10849552/update-viewpager-dynamically/10852046#10852046
            return FragmentStatePagerAdapter.POSITION_NONE
        }

    } // ScreenPagerAdapter

} // MainActivity
