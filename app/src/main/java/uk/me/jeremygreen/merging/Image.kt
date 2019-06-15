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
     * This database table stores id -> file mappings. It's not possible to
     * just use the set of stored files since the UI can still get updates
     * after deciding to delete the image but before it has been removed
     * from the screen.
     *
     * Use autoGenerate=true since rely on ordering by primary key
     * to time order the photos. Could use UTC timestamp instead,
     * but can't rely on it being correct).
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val file: String

) {}