package uk.me.jeremygreen.merging2.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.me.jeremygreen.merging2.BuildConfig
import uk.me.jeremygreen.merging2.R
import uk.me.jeremygreen.merging2.databinding.AboutBinding

internal class AboutActivity: AppCompatActivity() {

    private val versionName by lazy { packageManager.getPackageInfo(packageName, 0).versionName }

    private lateinit var binding: AboutBinding

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.aboutToolbar)
        binding.appVersion.text = getString(R.string.version, BuildConfig.APPLICATION_ID, versionName)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        binding.aboutToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // Activity
    override fun onPause() {
        super.onPause()
        binding.aboutToolbar.setNavigationOnClickListener(null)
    }

}
