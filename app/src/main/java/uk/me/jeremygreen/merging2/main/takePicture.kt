package uk.me.jeremygreen.merging2.main

import android.content.Context
import android.net.Uri
import android.util.Log
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

private const val TAG = "takePicture"


@Composable
internal fun takePicture(
    imagesDir: File,
    addImage : (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    // App likely stops and start while taking picture, so persist imageFile with rememberSaveable.
    var imageFile: File? by rememberSaveable { mutableStateOf(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            Log.i(TAG, "takePicture: imageFile: $imageFile success: $success")
            if (success) {
                addImage(imageFile.toString())
            }
            // TODO else analytics
        }
    )
    // The following callback is called each time want new picture, generating a new file name.
    return {
        imageFile = imageFile(imagesDir)
        FileUtils.mkdirs(imagesDir)
        val contentProviderUri = contentProviderUri(context, imageFile!!)
        Log.i(TAG, "takePicture: imageFile: $imageFile contentProviderUri: $contentProviderUri")
        cameraLauncher.launch(contentProviderUri)
    }
}

/** Create a [File] the app can use to access the image. */
private fun imageFile(imagesDir: File): File {
    val uuid = UUID.randomUUID()
    return File(imagesDir, "${uuid}.jpg")
}

/**
 * Convert [File] to a [FileProvider] Uri the camera app can use. This has to match the FileProvider
 * configured in AndroidManifest.xml and file_paths.xml.
 * */
private fun contentProviderUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".fileprovider",
        file
    )
}
