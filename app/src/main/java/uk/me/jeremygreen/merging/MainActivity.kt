package uk.me.jeremygreen.merging

import android.os.Bundle
import android.util.Log
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
    }

    private inner class ScreenPagerAdapter(
        fragmentManager: FragmentManager,
        val photoManager: PhotoManager
    ) : FragmentStatePagerAdapter(fragmentManager) {

        // TODO Update property when set of photos changes.
        var photos: List<File>  by Delegates.observable(photoManager.photos) { _, old, new ->
            if (old != new) {
                // This must be called whenever getCount() will return a new value.
                notifyDataSetChanged()
            }
        }

        override fun getCount(): Int {
            return photos.size + 1;
        }

        override fun getItem(position: Int): Fragment {
            if (position < photos.size) {
                return ImageFragment(photos[position])
            } else {
                return AddImageFragment(photoManager)
            }
        }

    } // ScreenPagerAdapter

} // MainActivity
