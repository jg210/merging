package uk.me.jeremygreen.merging.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["faceId"])]
)
data class Coordinate(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val faceId: Long,

    val x: Float,
    val y: Float
)