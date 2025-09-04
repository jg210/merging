package uk.me.jeremygreen.merging2.model.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.File

@Entity(tableName = "images")
internal data class Image(

    /**
     * This database table stores id -> file mappings. It's not possible to
     * just use the set of stored files since the UI can still get updates
     * after deciding to delete the image, but before it has been removed
     * from the screen.
     *
     * Use autoGenerate=true since rely on ordering by primary key
     * to time order the photos. Could use UTC timestamp instead,
     * but can't rely on it being correct.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val file: String,

    /**
     * If null, then faces not found yet.
     */
    val faceDetectionAlgorithmVersion: Long? = null

) {

    @delegate:Ignore
    val uri: Uri by lazy { Uri.fromFile(File(this.file)) }

}
