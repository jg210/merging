package uk.me.jeremygreen.merging.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.model.AppViewModel

internal class OnboardingActivity: AppCompatActivity() {

    companion object {
        // Increase this whenever onboarding text is changed.
        const val version = 3L

        private const val PRIVACY_HTML = "file:///android_asset/privacy/index.html"

    }

    private lateinit var appViewModel: AppViewModel

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = AppViewModel.getInstance(this, application)
        setContent {
            Onboarding()
        }
        //setSupportActionBar(binding.onboardingToolbar)
//        binding.onboardingWebView.loadUrl(PRIVACY_HTML)
//        binding.onboardingWebView.setBackgroundColor(Color.TRANSPARENT)
//        binding.onboardingWebView.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                binding.onboardingAcceptCheckbox.visibility = View.VISIBLE
//            }
//            override fun shouldOverrideUrlLoading(webView: WebView?, url: String): Boolean {
//                if (url.startsWith("mailto:")) {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.setData(Uri.parse(url))
//                    startActivity(intent)
//                    return true
//                }
//                return false
//            }
//        }
    }

    /**
     * Update FloatingActionButton properties etc.
     */
//    private fun updateFabState() {
//        if (binding.onboardingAcceptCheckbox.isChecked) {
//            binding.onboardingAcceptButton.show()
//        } else {
//            binding.onboardingAcceptButton.hide()
//        }
//    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Onboarding() {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text(text = stringResource(R.string.appName)) }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { appViewModel.acceptOnboarding(version)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                        startActivity(intent)
                        finish()

                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = "onboarding text")
            }
        }
    }

}
