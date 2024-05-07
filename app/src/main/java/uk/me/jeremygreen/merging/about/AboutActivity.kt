package uk.me.jeremygreen.merging.about

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.me.jeremygreen.merging.BuildConfig
import uk.me.jeremygreen.merging.R

internal class AboutActivity: AppCompatActivity() {

    private val versionName by lazy { packageManager.getPackageInfo(packageName, 0).versionName }

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { About() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun About() {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text(text = stringResource(R.string.actionAbout)) },
                    navigationIcon = { BackButton() }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                AboutText()
            }
        }
    }

    @Composable
    private fun AboutText() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.version, BuildConfig.APPLICATION_ID, versionName))
        }
    }

    @Composable
    private fun BackButton() {
        IconButton(onClick = { finish() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }

}
