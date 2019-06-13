package uk.me.jeremygreen.merging

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.io.File
import java.lang.AssertionError
import kotlin.properties.Delegates

class FSAdapterImpl(
    fragmentActivity: FragmentActivity,
    val imageManager: ImageManager
) : FragmentStateAdapter(fragmentActivity),
    ImageManager.ChangeListener {

    private val TAG = "FSAdapterImpl"

    private val ID__ADD_IMAGE = -2L; // -1 is taken by RecyclerView.NO_ID

    private val imageIds: MutableMap<Long, Image> = linkedMapOf()
    var images: List<Image> = listOf()
        set(value) {
            imageIds.clear()
            val changed = field != value
            field = value
            if (changed) {
                Log.v(TAG, "images changed.")
                notifyDataSetChanged()
            }
        }
        get() {
            return field
        }

    // From ImageManager.ChangeListener
    override fun onImagesChange(images: List<Image>) {
        Log.v(TAG, "onImagesChange(${images.size} images)")
        this.images = images
    }

    override fun getItemId(position: Int): Long {
        val imageCount = images.size
        return when {
            (position >= 0 && position < imageCount) -> images[position].id
            (position == imageCount) -> ID__ADD_IMAGE
            else -> RecyclerView.NO_ID
        }
    }

    // From FragmentStateAdapter
    override fun containsItem(id: Long): Boolean {
        if (id == ID__ADD_IMAGE) {
            return true
        }
        return id in imageIds
    }

    // From FragmentStateAdapter
    override fun getItemCount(): Int {
        val count = imageIds.size + 1
        Log.v(TAG, "getItemCount() = ${count}")
        return count;
    }

    override fun createFragment(position: Int): Fragment {
        Log.v(TAG, "createFragment(${position})")
        val fragment: Fragment
        if (position < images.size) {
            fragment = ImageFragment.newInstance(images[position].id)
        } else {
            fragment = AddImageFragment()
        }
        return fragment
    }

} // FragmentStateAdapter