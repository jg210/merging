package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {

    @Query("SELECT * from images ORDER BY id ASC")
    fun getImages(): LiveData<List<Image>>

    @Query("SELECT * from images WHERE id = :id")
    suspend fun findById(id: Long): Image

    @Delete
    suspend fun delete(image: Image)

    @Insert
    suspend fun add(image: Image)

}

