package uk.me.jeremygreen.merging.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.about.AboutActivity
import uk.me.jeremygreen.merging.licences.LicencesActivity
import uk.me.jeremygreen.merging.main.screen.AddImage
import uk.me.jeremygreen.merging.main.screen.InputImage
import uk.me.jeremygreen.merging.main.screen.MergedImage
import uk.me.jeremygreen.merging.model.AppViewModel
import uk.me.jeremygreen.merging.model.Image
import java.io.File
import java.util.*

internal class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_TAKE_PHOTO = 1

        fun imagesDir(activity: Activity): File {
            return File(activity.filesDir, "photos")
        }
    }

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
        // Firebase Analytics and Crashlytics are only enabled after have agreed to their
        // use, which is done using OnboardingActivity.
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        this.firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        if (!BuildConfig.DEBUG) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
        }
        setContent { Main() }
//        this.appViewModel.allImages().observe(this) { images ->
//            this.pagerAdapter.setImages(images)
//        }
    }

    @OptIn(
        ExperimentalFoundationApi::class,
        ExperimentalMaterial3Api::class
    )
    @Composable
    private fun Main() {
        val context = this
        val images = this.appViewModel.allImages().observeAsState().value
        //Log.i(TAG, "images: ${images?.size}")
        val pagerState = rememberPagerState(
            pageCount = {
                pagerPageCount(images)
            }
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text(text = stringResource(R.string.appName)) },
                    actions = {
                        OverflowMenu { closeMenu ->
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.actionAbout)) },
                                onClick = {
                                    closeMenu()
                                    val intent = Intent(context, AboutActivity::class.java)
                                    startActivity(intent)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.actionLicences)) },
                                onClick = {
                                    closeMenu()
                                    val intent = Intent(context, LicencesActivity::class.java)
                                    startActivity(intent)
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = ::handleTakePhoto,
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }

        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                HorizontalPager(
                    state = pagerState,
                    beyondBoundsPageCount = 2,
                    modifier =  Modifier.fillMaxHeight()
                ) { page ->
                    Pages(images, page)
                }
            }
        }
    }

    private fun isMergedImageShown(images: List<Image>?) = !images.isNullOrEmpty() && images.size > 1

    private fun pagerPageCount(images: List<Image>?): Int {
        var pageCount = 1 // add image page
        if (!images.isNullOrEmpty()) {
            pageCount += images.size
        }
        if (isMergedImageShown(images)) {
            pageCount += 1
        }
        //Log.i(TAG, "pagerPageCount: ${pageCount}")
        return pageCount
    }

    @Composable
    private fun Pages(images: List<Image>?, page: Int) {
        if (page == 0) {
            AddImage()
            return
        }
        val isLastPage = page == pagerPageCount(images) - 1
        if (isLastPage && isMergedImageShown(images)) {
            MergedImage()
            return
        }
        val image = images?.get(page - 1)
        InputImage(image)
    }

    @Composable
    fun OverflowMenu(content: @Composable (closeMenu: () -> Unit) -> Unit) {
        var showMenu by remember { mutableStateOf(false) }
        val closeMenu = { showMenu = false}
        IconButton(onClick = {
            showMenu = !showMenu
        }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = closeMenu
        ) {
            content(closeMenu)
        }
    }

    // TODO analytics
    private fun screenView(screenName: String?) {
        if (screenName == null) {
            return
        }
        Log.i(TAG, "screen name: ${screenName}")
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        this.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    // TODO binding.fab.setOnClickListener { handleTakePhoto() }

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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return intent
    }

    // Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            // TODO this.appViewModel.addImage(file.path)
        }
    }

} // MainActivity
