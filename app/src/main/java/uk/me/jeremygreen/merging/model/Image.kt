package uk.me.jeremygreen.merging.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(

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

    val file: String

)