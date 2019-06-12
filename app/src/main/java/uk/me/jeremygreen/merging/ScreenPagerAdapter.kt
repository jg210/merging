package uk.me.jeremygreen.merging

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.io.File
import java.lang.AssertionError
import kotlin.properties.Delegates

class ScreenPagerAdapter(
    fragmentManager: FragmentManager,
    val imageManager: ImageManager
) : FragmentStatePagerAdapter(fragmentManager),
    ImageManager.ChangeListener {

    private val TAG = "ScreenPagerAdapter"
    private val fragments: MutableSet<Fragment> = mutableSetOf()

    var images: List<Image> by Delegates.observable(imageManager.images) { _, old, new ->
        if (old != new) {
            Log.v(TAG, "notfifyDataSetChanged()")
            // Could leave Fragments that haven't moved in the MutableSet, but
            // it's easier to just remove them all, so that all the Fragments
            // are recreated.
            fragments.clear()
            // This must be called whenever getCount() will return a new value.
            notifyDataSetChanged()
        }
    }

    // From ImageManager.ChangeListener
    override fun onImagesChange(images: List<Image>) {
        Log.v(TAG, "onImagesChange(${images.size} images)")
        this.images = images
    }

    // From PagerAdapter
    override fun getCount(): Int {
        val count = images.size + 1
        Log.v(TAG, "getCount() = ${count}")
        return count;
    }

    // From PagerAdapter
    override fun getItem(position: Int): Fragment {
        Log.v(TAG, "getItem(${position})")
        val fragment: Fragment
        if (position < images.size) {
            fragment = ImageFragment.newInstance(images[position].id)
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
        // Need to return POSITION_NONE in general, otherwise deleted images aren't removed from the ViewPager.
        val position: Int
        if (obj is Fragment && fragments.contains(obj)) {
            position = FragmentStatePagerAdapter.POSITION_UNCHANGED
        } else {
            position = FragmentStatePagerAdapter.POSITION_NONE
        }
        Log.v(TAG, "getItemPosition(${obj}) = ${position}")
        return position
    }

    // From PagerAdapter
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = super.instantiateItem(container, position)
        Log.v(TAG, "instantiateItem(${position}) = ${item}")
        return item
    }

    // From PagerAdapter
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        super.destroyItem(container, position, obj)
        Log.v(TAG, "destroyItem(${position}, ${obj})")
        fragments.remove(obj)
    }

    // From PagerAdapter
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        val result = super.isViewFromObject(view, obj)
        Log.v(TAG, "isViewFromObject(${view},${obj}) = ${result}")
        return result
    }

} // ScreenPagerAdapter