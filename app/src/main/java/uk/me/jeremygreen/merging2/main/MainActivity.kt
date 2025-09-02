package uk.me.jeremygreen.merging2.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import uk.me.jeremygreen.merging2.BuildConfig
import uk.me.jeremygreen.merging2.model.AppViewModel
import java.io.File

internal class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"

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
        setContent { Main(imagesDir, appViewModel) }
    }

    // TODO analytics
    @Suppress("unused")
    private fun screenView(screenName: String?) {
        if (screenName == null) {
            return
        }
        Log.i(TAG, "screen name: ${screenName}")
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        this.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

} // MainActivity
