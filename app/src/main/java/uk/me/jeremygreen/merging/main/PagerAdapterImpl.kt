package uk.me.jeremygreen.merging.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import uk.me.jeremygreen.merging.model.Image
import uk.me.jeremygreen.merging.main.screen.AddImageFragment
import uk.me.jeremygreen.merging.main.screen.ImageFragment
import uk.me.jeremygreen.merging.main.screen.MergedImageFragment

class PagerAdapterImpl(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        private const val TAG = "PagerAdapterImpl"
    }

    private var ids: Set<Long> = setOf()
    private var factories: List<ScreenFragmentFactory<*>> = listOf()

    fun setImages(images: List<Image>) {
        Log.v(TAG, "setImages(${images.size})")
        val newFactories: MutableList<ScreenFragmentFactory<*>> = mutableListOf()
        if (images.isEmpty()) {
            newFactories.add(AddImageFragment)
        }
        images.forEach {image ->
            newFactories.add(ImageFragment.createFactory(image))
        }
        if (images.size >= 2) {
            newFactories.add(MergedImageFragment)
        }
        val newIds = newFactories.map{ factory -> factory.id }.toSet()
        if (newIds.size != newFactories.size) {
            throw AssertionError("ids: ${this.ids} factories: ${this.factories}")
        }
        if (RecyclerView.NO_ID in newIds) {
            throw AssertionError("ids: ${this.ids} factories: ${this.factories}")
        }
        val changes = changes(this.factories, newFactories)
        this.factories = newFactories
        this.ids = newIds
        changes.dispatchUpdatesTo(this)
    }

    private fun changes(
        old: List<ScreenFragmentFactory<*>>,
        new: List<ScreenFragmentFactory<*>>
    ): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].id == new[newItemPosition].id
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

    /**
     * Firebase analytics "screen name".
     */
    fun screenName(viewPager2: ViewPager2): String? {
        if (this.factories.isEmpty()) {
            return null
        }
        val position = viewPager2.currentItem
        return this.factories[position].screenName()
    }

    // FragmentStateAdapter
    override fun getItemId(position: Int): Long {
        return this.factories[position].id // ...or RecyclerView.NO_ID?
    }

    // FragmentStateAdapter
    override fun containsItem(id: Long): Boolean {
        return id in this.ids
    }

    // FragmentStateAdapter
    override fun getItemCount(): Int {
        val count = this.ids.size
        Log.v(TAG, "getItemCount() = ${count}")
        return count
    }

    // FragmentStateAdapter
    override fun createFragment(position: Int): Fragment {
        Log.v(TAG, "createFragment(${position})")
        return this.factories[position].createInstance()
    }

} // FragmentStateAdapter