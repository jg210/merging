package uk.me.jeremygreen.merging2.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import uk.me.jeremygreen.merging2.R
import uk.me.jeremygreen.merging2.about.AboutActivity
import uk.me.jeremygreen.merging2.licences.LicencesActivity
import uk.me.jeremygreen.merging2.main.screen.AddImage
import uk.me.jeremygreen.merging2.main.screen.InputImage
import uk.me.jeremygreen.merging2.main.screen.MergedImage
import uk.me.jeremygreen.merging2.model.AppViewModel
import uk.me.jeremygreen.merging2.model.Image
import java.io.File

private const val TAG = "Main"

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
internal fun Main(
    imagesDir: File,
    appViewModel: AppViewModel
) {
    val images = appViewModel.allImages().observeAsState().value
    val pagerState = rememberPagerState(
        pageCount = {
            pagerPageCount(images)
        }
    )
    Scaffold(
        topBar = { TopBar(LocalContext.current) },
        floatingActionButton = { FloatingActionButtonImpl(imagesDir, appViewModel::addImage) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 2,
                modifier =  Modifier.fillMaxHeight()
            ) { page ->
                Pages(images, page, appViewModel::delete)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(context: Context) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = stringResource(R.string.appName)) },
        actions = {
            OverflowMenu { closeMenu ->
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.actionAbout)) },
                    onClick = {
                        closeMenu()
                        val intent = Intent(context, AboutActivity::class.java)
                        context.startActivity(intent)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.actionLicences)) },
                    onClick = {
                        closeMenu()
                        val intent = Intent(context, LicencesActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    )
}

@Composable
private fun FloatingActionButtonImpl(imagesDir: File, addImage: (String) -> Unit) {
    FloatingActionButton(
        onClick = takePicture(imagesDir, addImage),
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}

private fun isMergedImageShown(images: ImmutableList<Image>?) = !images.isNullOrEmpty() && images.size > 1

private fun pagerPageCount(images: ImmutableList<Image>?): Int {
    if (images.isNullOrEmpty()) {
        return 1 // add image page
    }
    return if (isMergedImageShown(images)) {
        images.size + 1
    } else {
        images.size
    }
}

@Composable
private fun Pages(images: ImmutableList<Image>?, page: Int, deleteImage: (Image) -> Unit) {
    Log.i(TAG, "Pages: page=$page images=${images?.size}")
    if (images.isNullOrEmpty()) {
        AddImage()
        return
    }
    val isLastPage = page == pagerPageCount(images) - 1
    if (isLastPage && isMergedImageShown(images)) {
        MergedImage()
        return
    }
    if (page < 0 || page >= images.size) {
        // Non-zero beyondBoundsPageCount causes out-of-range page to be provided.
        return
    }
    val image = images[page]
    InputImage(image, onLongClick = { deleteImage(image) })
}

@Composable
private fun OverflowMenu(content: @Composable (closeMenu: () -> Unit) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val closeMenu = { showMenu = false}
    Column {
        IconButton(onClick = {
            showMenu = !showMenu
        }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = closeMenu
        ) {
            content(closeMenu)
        }
    }
}