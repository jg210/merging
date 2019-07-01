package uk.me.jeremygreen.merging.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.crashlytics.android.Crashlytics
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.main.*
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.about.AboutActivity
import uk.me.jeremygreen.merging.model.AppViewModel
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        fun imagesDir(activity: Activity): File {
            return File(activity.filesDir, "photos")
        }
    }

    private val TAG = "MainActivity"
    private val REQUEST_TAKE_PHOTO = 1
    private val BUNDLE_KEY__FILE = "file"
    private var file: File? = null
    private lateinit var pagerAdapter: PagerAdapterImpl
    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val imageViewModel by lazy {
        ViewModelProviders.of(this).get(AppViewModel::class.java)
    }

    private val imagesDir: File by lazy {
        imagesDir(this)
    }

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val fileString = savedInstanceState.getString(BUNDLE_KEY__FILE)
            if (fileString != null) {
                file = File(fileString)
            }
        }
        // Firebase Analytics and Crashlytics are only configured after have agreed to their use.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        setContentView(R.layout.main)
        setSupportActionBar(this.toolbar)
        this.pagerAdapter = PagerAdapterImpl(this)
        this.pager.adapter = this.pagerAdapter
        this.pager.offscreenPageLimit = 2
        this.pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val screenName: String? = this@MainActivity.pagerAdapter.screenName(pager)
                Log.i(TAG, "screen name: ${screenName}")
                if (screenName != null) {
                    this@MainActivity.firebaseAnalytics.setCurrentScreen(this@MainActivity, screenName, null)
                }
            }
        }
        val imageViewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)
        imageViewModel.allImages().observe(this, Observer { images ->
            this.pagerAdapter.setImages(images)
        })
        val licencesTitle = resources.getString(R.string.actionLicences)
        OssLicensesMenuActivity.setActivityTitle(licencesTitle)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        this.pager.registerOnPageChangeCallback(this.pageChangeCallback)
        this.fab.setOnClickListener { handleTakePhoto() }
    }

    // Activity
    override fun onPause() {
        this.fab.setOnClickListener(null)
        this.pager.unregisterOnPageChangeCallback(this.pageChangeCallback)
        super.onPause()
    }

    // Activity
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLicences -> {
                val intent: Intent = Intent(this, OssLicensesMenuActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.actionAbout -> {
                val intent: Intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val file = this.file
        if (file != null) {
            outState.putString(BUNDLE_KEY__FILE, file.path)
        }
    }

    private fun handleTakePhoto() {
        val intent = createTakePhotoIntent()
        if (intent == null) {
            Log.w(TAG, "No camera application available.")
        } else {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun createTakePhotoIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraActivity = intent.resolveActivity(baseContext.packageManager)
        if (cameraActivity == null) {
            return null
        }
        val uuid = UUID.randomUUID()
        val file = File(imagesDir, "${uuid}.jpg")
        val imageUri: Uri = FileProvider.getUriForFile(
            baseContext,
            "uk.me.jeremygreen.merging.fileprovider",
            file
        )
        this.file = file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return intent
    }

    // Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            val file = file
            if (file == null) {
                throw AssertionError()
            }
            imageViewModel.addImage(file.path)
        }
    }

} // MainActivity
