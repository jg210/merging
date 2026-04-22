package uk.me.jeremygreen.merging2.model

import androidx.room.Relation
import uk.me.jeremygreen.merging2.model.entity.Coordinate

internal data class FaceWithCoordinates(

    val id: Long,

    val imageId: Long,

    @Relation(parentColumn = "id", entityColumn = "faceId")
    val coordinates: List<Coordinate>

)
