package uk.me.jeremygreen.merging.model

import androidx.room.Relation

data class Face(

    val id: Int,

    val imageId: Long,

    @Relation(parentColumn = "id", entityColumn = "faceId")
    val coordinates: List<Coordinate>

)
