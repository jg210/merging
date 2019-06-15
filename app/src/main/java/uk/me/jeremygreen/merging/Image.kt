package uk.me.jeremygreen.merging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.File

@Entity(tableName = "images")
data class Image(
    /**
     * The id is used to persist the image to storage. Images can be deleted, so there can be
     * gaps in the ids. It is generally not the same as the position of the image within the UI.
     *
     * Use autoGenerate=true since:
     *
     * Rely on ordering by primary key to time order the photos. Could use UTC timestamp instead, but can't rely on it being
     * correct).
     *
     * Reusing the same id causes problems for image deletion. If an image
     * is deleted and added and the id reused, a UI that is still getting updated
     * can get confused.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val file: String

) {}