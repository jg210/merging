package uk.me.jeremygreen.merging.onboarding

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Onboarding() {
        var agreed by rememberSaveable { mutableStateOf(false) }
        var webViewLoaded by remember { mutableStateOf(false) }
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
                AnimatedVisibility(
                    visible = agreed,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            appViewModel.acceptOnboarding(version)
                            val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                            startActivity(intent)
                            finish()
                        },
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WebView(url = PRIVACY_HTML, onLoaded = { webViewLoaded = true })
                if (webViewLoaded) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Switch(
                            checked = agreed,
                            onCheckedChange = {
                                agreed = it
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WebView(
        @Suppress("SameParameterValue") url: String,
        onLoaded: () -> Unit
    ) {
        AndroidView(
            factory = { context ->
                return@AndroidView WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            onLoaded()
                        }
                    }
                }
            },
            update = {
                    it.loadUrl(url)
            }
        )
    }

}
