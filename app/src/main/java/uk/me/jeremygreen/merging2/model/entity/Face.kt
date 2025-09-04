package uk.me.jeremygreen.merging2.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "faces",
    foreignKeys = [
        ForeignKey(
            entity = Image::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["imageId"])]
)
internal data class Face(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val imageId: Long

)
