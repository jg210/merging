package uk.me.jeremygreen.merging2.main

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import uk.me.jeremygreen.merging2.R
import uk.me.jeremygreen.merging2.about.AboutActivity
import uk.me.jeremygreen.merging2.licences.LicencesActivity
import uk.me.jeremygreen.merging2.model.AppViewModel
import uk.me.jeremygreen.merging2.model.entity.Image
import java.io.File

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
internal fun Main(
    imagesDir: File,
    appViewModel: AppViewModel
) {
    val allImagesLiveData = appViewModel.allImages()
    val images: ImmutableList<Image> = allImagesLiveData.observeAsState(persistentListOf()).value
    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = { FloatingActionButtonImpl(imagesDir, appViewModel::addImage) }
    ) { innerPadding -> Pager(images, appViewModel, innerPadding)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar() {
    val context = LocalContext.current
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
