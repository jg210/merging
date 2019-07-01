package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OnboardingDao {

    @Query("SELECT * from onboarding`` WHERE version = :version")
    suspend fun findById(version: Long): Onboarding?

    @Insert
    suspend fun add(version: Onboarding)

}

