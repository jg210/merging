package uk.me.jeremygreen.merging2.model

import androidx.room.Relation

internal data class FaceWithCoordinates(

    val id: Long,

    val imageId: Long,

    @Relation(parentColumn = "id", entityColumn = "faceId")
    val coordinates: List<Coordinate>

)
