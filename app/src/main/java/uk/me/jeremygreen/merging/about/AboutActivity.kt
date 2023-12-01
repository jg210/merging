package uk.me.jeremygreen.merging.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.databinding.AboutBinding

class AboutActivity: AppCompatActivity() {

    private lateinit var aboutBinding: AboutBinding

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aboutBinding = AboutBinding.inflate(layoutInflater)
        setContentView(R.layout.about)
        setSupportActionBar(aboutBinding.aboutToolbar)
        aboutBinding.appVersion.text = getString(R.string.version, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        aboutBinding.aboutToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // Activity
    override fun onPause() {
        super.onPause()
        aboutBinding.aboutToolbar.setNavigationOnClickListener(null)
    }

}