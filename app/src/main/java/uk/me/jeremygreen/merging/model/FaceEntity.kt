package uk.me.jeremygreen.merging.model

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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["imageId"])]
)
data class FaceEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val imageId: Long

)