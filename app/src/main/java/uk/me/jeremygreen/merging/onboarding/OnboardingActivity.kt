package uk.me.jeremygreen.merging.onboarding

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import uk.me.jeremygreen.merging.databinding.OnboardingBinding
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.model.AppViewModel


class OnboardingActivity: AppCompatActivity() {

    companion object {
        // Increase this whenever onboarding text is changed.
        const val version = 3L

        private const val PRIVACY_HTML = "file:///android_asset/privacy/index.html"

    }

    private lateinit var binding: OnboardingBinding
    private lateinit var appViewModel: AppViewModel

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = AppViewModel.getInstance(this, application)
        setSupportActionBar(binding.onboardingToolbar)
        binding.onboardingWebView.loadUrl(PRIVACY_HTML)
        binding.onboardingWebView.setBackgroundColor(Color.TRANSPARENT)
        binding.onboardingWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.onboardingAcceptCheckbox.visibility = View.VISIBLE
            }
            override fun shouldOverrideUrlLoading(webView: WebView?, url: String): Boolean {
                if (url.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                return false
            }
        }
    }

    // Activity
    override fun onResume() {
        super.onResume()
        binding.onboardingAcceptButton.setOnClickListener {
            binding.onboardingAcceptButton.setOnClickListener(null)
            appViewModel.acceptOnboarding(version)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            finish()
        }
        binding.onboardingAcceptCheckbox.setOnClickListener {
            updateFabState()
        }
        updateFabState()
    }

    /**
     * Update FloatingActionButton properties etc.
     */
    private fun updateFabState() {
        if (binding.onboardingAcceptCheckbox.isChecked) {
            binding.onboardingAcceptButton.show()
        } else {
            binding.onboardingAcceptButton.hide()
        }
    }

    // Activity
    override fun onPause() {
        binding.onboardingAcceptButton.setOnClickListener(null)
        binding.onboardingAcceptCheckbox.setOnClickListener(null)
        super.onPause()
    }

}
