package uk.me.jeremygreen.merging

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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {

        init {
        }

        fun imagesDir(activity: Activity): File {
            return File(activity.filesDir, "photos")
        }
    }

    private val TAG = "MainActivity"
    private val REQUEST_TAKE_PHOTO = 1
    private val BUNDLE_KEY__FILE = "file"
    private var file: File? = null

    private val imageViewModel by lazy {
        ViewModelProviders.of(this).get(ImageViewModel::class.java)
    }

    val imagesDir: File by lazy {
        MainActivity.imagesDir(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val fileString = savedInstanceState.getString(BUNDLE_KEY__FILE)
            if (fileString != null) {
                file = File(fileString)
            }
        }
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val pagerAdapter = PagerAdapterImpl(this)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 2
        val imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        imageViewModel.allImages().observe(this, Observer { images ->
            pagerAdapter.setImages(images)
        })
        fab.setOnClickListener { handleTakePhoto() }
        val licencesTitle = getResources().getString(R.string.actionLicences)
        OssLicensesMenuActivity.setActivityTitle(licencesTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLicences -> {
                val intent: Intent = Intent(this, OssLicensesMenuActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            val file = file
            if (file == null) {
                throw AssertionError()
            }
            GlobalScope.launch(Dispatchers.IO) {
                imageViewModel.addImage(file.path)
            }
        }
    }

} // MainActivity
