package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImageDao {

    @Query("SELECT * from images ORDER BY id ASC")
    fun getImages(): LiveData<List<Image>>

    @Query("SELECT * from images WHERE id = :imageId")
    suspend fun findById(imageId: Long): Image

    @Query("SELECT processingStage from images WHERE id = :imageId")
    suspend fun getProcessingStage(imageId: Long): Int

    @Delete
    suspend fun delete(image: Image)

    @Insert
    suspend fun add(image: Image)

    @Update
    suspend fun update(image: Image)

}
