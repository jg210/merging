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

@Composable
internal fun alertDialog(
    title: @Composable () -> Unit,
    onConfirm: () -> Unit
): () -> Unit {
    var visible by rememberSaveable { mutableStateOf(false) }
    if (visible) {
        val confirmButtonClick = {
            onConfirm()
            visible = false
        }
        val cancelButtonClick = { visible = false }
        val confirmButton = @Composable {
            Button(onClick = confirmButtonClick ) {
                Text(stringResource(R.string.ok))
            }
        }
        val dismissButton = @Composable {
            Button(onClick = cancelButtonClick) {
                Text(stringResource(R.string.cancel))
            }
        }
        AlertDialog(
            title = title,
            onDismissRequest = { visible = false },
            confirmButton = confirmButton,
            dismissButton = dismissButton,
        )
    }
    return {
        visible = true
    }
}
