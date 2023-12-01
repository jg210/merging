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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.about.AboutActivity
import uk.me.jeremygreen.merging.databinding.MainBinding
import uk.me.jeremygreen.merging.model.AppViewModel
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_TAKE_PHOTO = 1
        private const val BUNDLE_KEY__FILE = "file"

        fun imagesDir(activity: Activity): File {
            return File(activity.filesDir, "photos")
        }
    }

    private var file: File? = null
    private lateinit var mainBinding: MainBinding
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
        this.mainBinding = MainBinding.inflate(layoutInflater)
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
        setSupportActionBar(mainBinding.toolbar)
        this.pagerAdapter = PagerAdapterImpl(this)
        mainBinding.pager.adapter = this.pagerAdapter
        mainBinding.pager.offscreenPageLimit = 2
        this.pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val screenName: String? = this@MainActivity.pagerAdapter.screenName(mainBinding.pager)
                screenView(screenName)
            }
        }
        this.appViewModel.allImages().observe(this) { images ->
            this.pagerAdapter.setImages(images)
        }
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
        mainBinding.pager.registerOnPageChangeCallback(this.pageChangeCallback)
        mainBinding.fab.setOnClickListener { handleTakePhoto() }
    }

    // Activity
    override fun onPause() {
        mainBinding.fab.setOnClickListener(null)
        mainBinding.pager.unregisterOnPageChangeCallback(this.pageChangeCallback)
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

    private fun createTakePhotoIntent(): Intent {
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
        val file = file
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO && file != null) {
            this.appViewModel.addImage(file.path)
        }
    }

} // MainActivity
