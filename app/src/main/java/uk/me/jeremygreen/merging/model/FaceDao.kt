package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FaceDao {

    @Transaction
    @Query("SELECT * from faces WHERE imageId = :imageId")
    fun findById(imageId: Long): LiveData<List<Face>>

    @Insert
    suspend fun addAll(faces: List<FaceEntity>): List<Long>

}

