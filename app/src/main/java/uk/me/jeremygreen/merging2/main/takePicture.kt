package uk.me.jeremygreen.merging2.main

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.facebook.common.file.FileUtils
import uk.me.jeremygreen.merging2.BuildConfig
import java.io.File
import java.util.UUID

@Composable
fun takePicture(
    imagesDir: File,
    addImage : (String) -> Unit
): () -> Unit {
    var imageUri: Uri? by rememberSaveable { mutableStateOf(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            //Log.i(TAG, "takePicture: uri: $imageUri success: $success")
            if (success) {
                addImage(imageUri.toString())
            }
            // TODO else analytics
        }
    )
    imageUri = createTakeImageUri(LocalContext.current, imagesDir)
    return {
        FileUtils.mkdirs(imagesDir)
        cameraLauncher.launch(imageUri)
    }
}

private fun createTakeImageUri(context: Context, imagesDir: File): Uri {
    val uuid = UUID.randomUUID()
    val file = File(imagesDir, "${uuid}.jpg")
    return FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".fileprovider",
        file
    )
}
