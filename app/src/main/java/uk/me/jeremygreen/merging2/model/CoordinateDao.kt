package uk.me.jeremygreen.merging2.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uk.me.jeremygreen.merging2.model.entity.Coordinate

@Dao
internal interface CoordinateDao {

    @Query("SELECT * from coordinates WHERE faceId = :faceId")
    suspend fun findById(faceId: Long): List<Coordinate>

    @Insert
    suspend fun addAll(coordinates: List<Coordinate>)

}

