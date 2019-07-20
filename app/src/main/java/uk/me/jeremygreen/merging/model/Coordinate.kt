package uk.me.jeremygreen.merging.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "coordinates",
    foreignKeys = [
        ForeignKey(
            entity=FaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["faceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Coordinate(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val faceId: Long,

    val x: Int,
    val y: Int
)