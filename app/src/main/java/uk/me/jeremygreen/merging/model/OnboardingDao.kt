package uk.me.jeremygreen.merging.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OnboardingDao {

    @Query("SELECT * from onboarding`` WHERE version = :version")
    suspend fun findById(version: Long): Onboarding?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(version: Onboarding)

}

