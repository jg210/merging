package uk.me.jeremygreen.merging.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "coordinates",
    foreignKeys = [
        ForeignKey(
            entity=Face::class,
            parentColumns = ["id"],
            childColumns = ["faceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Coordinate(
    @PrimaryKey
    val id: Int,

    val faceId: Int,

    val x: Int,
    val y: Int
)