package uk.me.jeremygreen.merging

import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : FragmentActivity() {

    private val TAG = "MainActivity"

    lateinit var photoManager: PhotoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val photosDir = File(filesDir, "photos")
        photoManager = PhotoManager(this, photosDir);
        Log.i(TAG, "photo-manager photo count: ${photoManager.photoCount}")
        setContentView(R.layout.content_main)
        val pagerAdapter = ScreenPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
    }

    private inner class ScreenPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getCount(): Int {
            return photoManager.photoCount + 1;
        }

        override fun getItem(position: Int): Fragment {
            return when {
                position < photoManager.photoCount -> ImageFragment(position)
                else -> AddImageFragment(photoManager)
            }
        }

    }

} // MainActivity
