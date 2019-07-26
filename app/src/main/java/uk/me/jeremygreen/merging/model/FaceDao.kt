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
    suspend fun findById(imageId: Long): List<Face>

    @Query("SELECT COUNT(*) from faces WHERE imageId = :imageId")
    fun countById(imageId: Long): LiveData<Long>

    @Insert
    suspend fun addAll(faces: List<FaceEntity>): List<Long>

}

