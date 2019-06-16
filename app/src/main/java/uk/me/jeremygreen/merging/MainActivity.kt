package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val pagerAdapter = PagerAdapterImpl(this)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 2
        val imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        imageViewModel.allImages().observe(this, Observer { images ->
            pagerAdapter.setImages(images)
        })
    }

} // MainActivity
