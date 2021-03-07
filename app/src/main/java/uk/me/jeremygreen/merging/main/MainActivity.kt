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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

    private val appViewModel by lazy {
        AppViewModel.getInstance(this, application)
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
                this.file = File(fileString)
            }
        }
        // Firebase Analytics and Crashlytics are only enabled after have agreed to their
        // use, which is done using OnboardingActivity.
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        this.firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        if (!BuildConfig.DEBUG) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
        }
        setContentView(R.layout.main)
        setSupportActionBar(this.toolbar)
        this.pagerAdapter = PagerAdapterImpl(this)
        this.pager.adapter = this.pagerAdapter
        this.pager.offscreenPageLimit = 2
        this.pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val screenName: String? = this@MainActivity.pagerAdapter.screenName(pager)
                screenView(screenName)
            }
        }
        this.appViewModel.allImages().observe(this, Observer { images ->
            this.pagerAdapter.setImages(images)
        })
        val licencesTitle = resources.getString(R.string.actionLicences)
        OssLicensesMenuActivity.setActivityTitle(licencesTitle)
    }

    private fun screenView(screenName: String?) {
        if (screenName == null) {
            return
        }
        Log.i(TAG, "screen name: ${screenName}")
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        this.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
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
        this.menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLicences -> {
                val intent = Intent(this, OssLicensesMenuActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.actionAbout -> {
                val intent = Intent(this, AboutActivity::class.java)
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
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)
    }

    private fun createTakePhotoIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uuid = UUID.randomUUID()
        val file = File(imagesDir, "${uuid}.jpg")
        val imageUri: Uri = FileProvider.getUriForFile(
            baseContext,
            BuildConfig.APPLICATION_ID + ".fileprovider",
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
            this.appViewModel.addImage(file.path)
        }
    }

} // MainActivity
