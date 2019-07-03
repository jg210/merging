package uk.me.jeremygreen.merging.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.about.*
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R

class AboutActivity: AppCompatActivity() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        setSupportActionBar(this.aboutToolbar)
        this.appVersion.text = getString(R.string.version, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        this.aboutToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // Activity
    override fun onPause() {
        super.onPause()
        this.aboutToolbar.setNavigationOnClickListener(null)
    }

}