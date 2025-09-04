package uk.me.jeremygreen.merging2.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface FaceDao {

    @Transaction
    @Query("SELECT * from faces WHERE imageId = :imageId")
    fun findById(imageId: Long): LiveData<List<FaceWithCoordinates>>

    @Insert
    suspend fun addAll(faces: List<Face>): List<Long>

}

