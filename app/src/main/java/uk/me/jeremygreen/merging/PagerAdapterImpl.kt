package uk.me.jeremygreen.merging

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapterImpl(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val TAG = "PagerAdapterImpl"
    private val ID__MERGED_IMAGE = -2L // -1 is taken by RecyclerView.NO_ID

    private val imageIds: MutableMap<Long, Image> = mutableMapOf()
    private var images: List<Image> = listOf()

    fun setImages(images: List<Image>) {
        Log.v(TAG, "onImagesChange(${images.size} images)")
        imageIds.clear()
        val changes = changes(this.images, images)
        images.forEach {image ->
            val id = image.id
            when (id) {
                ID__MERGED_IMAGE, RecyclerView.NO_ID ->
                    throw IllegalStateException("collides with non-image id: ${id}")
            }
            imageIds[id] = image
        }
        this.images = images
        changes.dispatchUpdatesTo(this)
    }

    private fun changes(old: List<Image>, new: List<Image>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition] == new[newItemPosition]
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].id == new[newItemPosition].id
            }
            override fun getNewListSize(): Int {
                return new.size
            }
            override fun getOldListSize(): Int {
                return old.size
            }
        })
    }

    override fun getItemId(position: Int): Long {
        val imageCount = images.size
        if (position >= 0 && position < imageCount) {
            return images[position].id
        }
        if (position < getItemCount()) {
            return ID__MERGED_IMAGE
        }
        return RecyclerView.NO_ID
    }

    // From FragmentStateAdapter
    override fun containsItem(id: Long): Boolean {
        if (id == ID__MERGED_IMAGE) {
            return true
        }
        return id in imageIds
    }

    // From FragmentStateAdapter
    override fun getItemCount(): Int {
        val count = if (images.size >= 2) {
            images.size + 1
        } else {
            images.size
        }
        Log.v(TAG, "getItemCount() = ${count}")
        return count
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