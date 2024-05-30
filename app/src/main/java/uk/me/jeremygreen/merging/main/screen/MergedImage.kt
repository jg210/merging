package uk.me.jeremygreen.merging.main.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uk.me.jeremygreen.merging.R

// id: Long = -3
// screenName(): String = "MergedImage"

@Composable
fun MergedImage() {
    Text(text = stringResource(R.string.merged_image))
}
