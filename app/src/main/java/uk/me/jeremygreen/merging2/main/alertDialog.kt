package uk.me.jeremygreen.merging2.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import uk.me.jeremygreen.merging2.R

/**
 * Show an [AlertDialog] by calling the returned function.
 */
@Composable
internal fun alertDialog(
    title: @Composable () -> Unit,
    onConfirm: () -> Unit
): () -> Unit {
    var visible by rememberSaveable { mutableStateOf(false) }
    @Composable fun DialogButton(
        resourceId: Int,
        onClick: () -> Unit = {}
    ) {
        Button(onClick = { visible = false ; onClick()}) {
            Text(stringResource(resourceId))
        }
    }
    if (visible) {
        val confirmButton = @Composable { DialogButton(R.string.ok) { onConfirm() } }
        val dismissButton = @Composable { DialogButton(R.string.cancel) }
        AlertDialog(
            title = title,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            onDismissRequest = { visible = false }
        )
    }
    return {
        visible = true
    }
}
