package uk.me.jeremygreen.merging

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : FragmentActivity() {

    lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        val pagerAdapter = PagerAdapterImpl(this)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 2
        imageViewModel.images.observe(this, Observer { images ->
            pagerAdapter.setImages(images)
        })
    }

} // MainActivity
