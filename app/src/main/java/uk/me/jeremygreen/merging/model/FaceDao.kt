package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FaceDao {

    @Query("SELECT * from faces WHERE imageId = :imageId")
    suspend fun findById(imageId: Long): List<Face>

    @Insert
    suspend fun addAll(faces: List<FaceEntity>)

}

